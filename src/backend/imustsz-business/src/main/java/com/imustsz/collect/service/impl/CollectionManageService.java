package com.imustsz.collect.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.imustsz.collect.domain.*;
import com.imustsz.collect.mapper.CollectionManageMapper;
import com.imustsz.common.exception.ServiceException;
import com.imustsz.common.utils.bean.MinioUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.Semaphore;

@Service
public class CollectionManageService {
    private static final Logger LOG = LoggerFactory.getLogger(CollectionManageService.class);
    private final CollectionManageMapper mapper;
    private final MinioUtils minio;
    private final String bucket;
    private final long maxBytes;
    private final long maxPixels;
    private final ObjectMapper json = new ObjectMapper();
    private final Semaphore conversionPermit = new Semaphore(1);

    public CollectionManageService(CollectionManageMapper mapper, MinioUtils minio,
        @Value("${minio.bucketName}") String bucket,
        @Value("${collection.storage.max-image-mb:256}") int maxMb,
        @Value("${collection.storage.max-pixels:60000000}") long maxPixels) {
        if (maxMb < 1 || maxPixels < 1) throw new IllegalArgumentException("图像存储配置无效");
        this.mapper = mapper; this.minio = minio; this.bucket = bucket;
        this.maxBytes = maxMb * 1024L * 1024L; this.maxPixels = maxPixels;
    }

    public List<BizDataCollection> list(CollectionManageQuery query) {
        List<BizDataCollection> rows = mapper.selectManagedList(query);
        // Preserve the PageHelper Page object and its total; do not stream into a new list.
        for (BizDataCollection row : rows) {
            List<String> keys = objectNames(row.getImagePath());
            List<String> urls = new ArrayList<>();
            for (String key : keys) {
                try { urls.add(minio.getPresignedUrl(key)); }
                catch (Exception ex) { urls.add(""); }
            }
            row.setImagePath(String.join(",", urls));
            if (row.getLabelImage() != null && !row.getLabelImage().trim().isEmpty()) {
                try { row.setLabelImage(minio.getPresignedUrl(row.getLabelImage())); }
                catch (Exception ex) { row.setLabelImage(null); }
            }
        }
        return rows;
    }

    public Map<String,Object> storageInfo(Long id) throws Exception {
        BizDataCollection record = managed(id);
        List<String> keys = objectNames(record.getImagePath());
        if (keys.isEmpty()) throw new ServiceException("此采集记录没有原图");
        io.minio.StatObjectResponse stat = minio.getObjectStat(keys.get(0));
        Map<String,Object> result = new LinkedHashMap<>();
        result.put("id",id); result.put("data",record.getData());
        result.put("contentType",stat.contentType()); result.put("bytes",stat.size());
        return result;
    }

    public Map<String,Object> convert(CollectionStorageRequest request, String username) throws Exception {
        request.validate();
        // Check business type before reading or creating any storage object.
        BizDataCollection record = managed(request.getId());
        List<String> oldKeys = objectNames(record.getImagePath());
        if (oldKeys.isEmpty()) throw new ServiceException("此采集记录没有原图");
        if (!conversionPermit.tryAcquire()) throw new ServiceException("其他图像正在转换，请稍后重试");
        List<Path> localFiles = new ArrayList<>();
        List<String> createdKeys = new ArrayList<>();
        boolean databaseAttempted = false;
        try {
            long beforeBytes = minio.getObjectStat(oldKeys.get(0)).size();
            if (beforeBytes > maxBytes) throw new ServiceException("原图超过配置的" + maxBytes / 1024 / 1024 + "MB上限");
            Path source = temporary(localFiles), target = temporary(localFiles), thumbnail = temporary(localFiles);
            try (InputStream input = minio.getFileInputStream(bucket,oldKeys.get(0)); OutputStream output = Files.newOutputStream(source)) {
                byte[] buffer = new byte[65536]; long total = 0; int count;
                while ((count = input.read(buffer)) != -1) {
                    total += count;
                    if (total > maxBytes) throw new ServiceException("原图超过配置的大小上限");
                    output.write(buffer,0,count);
                }
            }
            CollectionImageCodec.convert(source,target,thumbnail,request.getFormat(),request.getQuality(),maxPixels);
            String extension = "jpeg".equals(request.getFormat()) ? "jpg" : "png";
            String prefix = "collection/type1/" + record.getId() + "/" + UUID.randomUUID() + "/";
            String originalKey = prefix + "original." + extension, thumbKey = prefix + "thumb." + extension;
            String contentType = "image/" + request.getFormat();
            upload(target,originalKey,contentType,createdKeys);
            upload(thumbnail,thumbKey,contentType,createdKeys);
            List<String> newKeys = new ArrayList<>(Arrays.asList(originalKey,thumbKey));
            if (oldKeys.size() > 2) newKeys.addAll(oldKeys.subList(2,oldKeys.size()));
            // No surrounding transaction: this single conditional UPDATE commits atomically.
            databaseAttempted = true;
            int changed = mapper.replaceManagedImage(record.getId(),record.getImagePath(),json.writeValueAsString(newKeys),username);
            if (changed != 1) {
                databaseAttempted = false;
                throw new ServiceException("记录已被删除或图像已被其他操作修改，请刷新后重试");
            }
            Map<String,Object> result = new LinkedHashMap<>();
            result.put("id",record.getId()); result.put("format",request.getFormat()); result.put("quality",request.getQuality());
            result.put("beforeBytes",beforeBytes); result.put("afterBytes",Files.size(target));
            result.put("thumbnailBytes",Files.size(thumbnail));
            return result;
        } catch (Exception ex) {
            // A database transport failure may have committed. Keep new objects in that case
            // rather than delete a possibly referenced image. Old objects are never deleted.
            if (!databaseAttempted) {
                for (String key : createdKeys) {
                    try { minio.deleteFile(key); }
                    catch (Exception cleanup) { LOG.warn("未能清理失败转换的图像对象: {}",key,cleanup); }
                }
            }
            throw ex;
        } finally {
            for (Path file : localFiles) {
                try { Files.deleteIfExists(file); }
                catch (IOException ex) { LOG.warn("未能清理图像转换临时文件",ex); }
            }
            conversionPermit.release();
        }
    }
    private BizDataCollection managed(Long id) {
        BizDataCollection record = id == null ? null : mapper.selectManagedById(id);
        if (record == null || !Integer.valueOf(1).equals(record.getType())) throw new ServiceException("采集记录不存在或不属于type=1，不能修改");
        return record;
    }
    private Path temporary(List<Path> files) throws IOException {
        Path result = Files.createTempFile("collection-storage-", ".image"); files.add(result); return result;
    }
    private void upload(Path file, String key, String type, List<String> created) throws Exception {
        created.add(key);
        try (InputStream input = Files.newInputStream(file)) { minio.uploadFile(key,input,Files.size(file),type); }
    }
    static List<String> objectNames(String value) {
        if (value == null || value.trim().isEmpty()) return Collections.emptyList();
        List<String> names = new ArrayList<>();
        try {
            String[] parsed = new ObjectMapper().readValue(value,String[].class);
            for (String key : parsed) if (key != null && !key.trim().isEmpty()) names.add(key.trim());
        } catch (Exception ignored) {
            for (String key : value.replace("[","").replace("]","").replace("\"","").split(","))
                if (!key.trim().isEmpty()) names.add(key.trim());
        }
        return names;
    }
}
