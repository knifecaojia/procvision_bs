package com.imustsz.collect.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.imustsz.collect.domain.BizDataCollection;
import com.imustsz.collect.domain.BizDataset;
import com.imustsz.collect.mapper.BizDataCollectionMapper;
import com.imustsz.collect.mapper.BizDatasetMapper;
import com.imustsz.collect.service.IBizDatasetService;
import com.imustsz.common.utils.bean.MinioUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class BizDatasetServiceImpl implements IBizDatasetService {

    @Autowired
    private BizDatasetMapper bizDatasetMapper;

    @Autowired
    private BizDataCollectionMapper bizDataCollectionMapper;

    @Autowired
    private MinioUtils minioUtils;

    @Value("${minio.bucketName}")
    private String bucketName;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public BizDataset selectBizDatasetById(Long id) {
        return bizDatasetMapper.selectBizDatasetById(id);
    }

    @Override
    public List<BizDataset> selectBizDatasetList(BizDataset BizDataset) {
        return bizDatasetMapper.selectBizDatasetList(BizDataset);
    }

    @Override
    public int insertBizDataset(BizDataset BizDataset) {
        return bizDatasetMapper.insertBizDataset(BizDataset);
    }

    @Override
    public int updateBizDataset(BizDataset BizDataset) {
        return bizDatasetMapper.updateBizDataset(BizDataset);
    }

    @Override
    public int deleteBizDatasetByIds(Long[] ids) {
        return bizDatasetMapper.deleteBizDatasetByIds(ids);
    }

    @Override
    public int deleteBizDatasetById(Long id) {
        return bizDatasetMapper.deleteBizDatasetById(id);
    }

    @Override
    public void writeDatasetArchive(Long id, OutputStream outputStream) throws IOException {
        BizDataset dataset = bizDatasetMapper.selectBizDatasetById(id);
        if (dataset == null) {
            throw new IllegalArgumentException("数据集不存在");
        }

        BizDataCollection query = new BizDataCollection();
        query.setDatasetId(id.intValue());
        query.setType(1);
        List<BizDataCollection> samples = bizDataCollectionMapper.selectBizDataCollectionList(query);
        List<String> warnings = new ArrayList<>();

        try (ZipOutputStream zip = new ZipOutputStream(outputStream, StandardCharsets.UTF_8)) {
            for (BizDataCollection sample : samples) {
                String baseName = sample.getId() + "_" + safeEntryName(sample.getData());
                List<String> imageObjects = parseObjectNames(sample.getImagePath());
                if (!imageObjects.isEmpty()) {
                    addMinioObject(zip, imageObjects.get(0), "images/original/" + baseName, warnings);
                } else {
                    warnings.add("记录 " + sample.getId() + " 没有原图");
                }

                if (sample.getLabelImage() != null && !sample.getLabelImage().trim().isEmpty()) {
                    addMinioObject(zip, sample.getLabelImage(), "images/annotated/" + baseName, warnings);
                }

                if (sample.getCoordsInfo() != null && !sample.getCoordsInfo().trim().isEmpty()) {
                    addTextEntry(zip, "annotations/" + baseName + ".json", sample.getCoordsInfo());
                }
            }

            Map<String, Object> manifest = new LinkedHashMap<>();
            manifest.put("datasetId", dataset.getId());
            manifest.put("name", dataset.getName());
            manifest.put("description", dataset.getDesc());
            manifest.put("sampleCount", samples.size());
            manifest.put("warnings", warnings);
            addTextEntry(zip, "dataset.json",
                    objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(manifest));
            zip.finish();
        }
    }

    private List<String> parseObjectNames(String imagePath) {
        List<String> result = new ArrayList<>();
        if (imagePath == null || imagePath.trim().isEmpty()) {
            return result;
        }
        try {
            String[] names = objectMapper.readValue(imagePath, String[].class);
            for (String name : names) {
                if (name != null && !name.trim().isEmpty()) result.add(name.trim());
            }
        } catch (Exception ignored) {
            String normalized = imagePath.replace("[", "").replace("]", "").replace("\"", "");
            for (String name : normalized.split(",")) {
                if (!name.trim().isEmpty()) result.add(name.trim());
            }
        }
        return result;
    }

    private void addMinioObject(ZipOutputStream zip, String objectName, String entryBaseName,
                                List<String> warnings) {
        try {
            String extension = extensionFor(objectName);
            zip.putNextEntry(new ZipEntry(entryBaseName + extension));
            try (InputStream input = minioUtils.getFileInputStream(bucketName, objectName)) {
                byte[] buffer = new byte[8192];
                int length;
                while ((length = input.read(buffer)) != -1) {
                    zip.write(buffer, 0, length);
                }
            }
            zip.closeEntry();
        } catch (Exception e) {
            try {
                zip.closeEntry();
            } catch (Exception ignored) {
                // 当前条目尚未创建时无需关闭。
            }
            warnings.add("文件 " + objectName + " 下载失败: " + e.getMessage());
        }
    }

    private String extensionFor(String objectName) {
        int slash = objectName.lastIndexOf('/');
        int dot = objectName.lastIndexOf('.');
        if (dot > slash && dot < objectName.length() - 1) {
            return objectName.substring(dot);
        }
        try {
            String contentType = minioUtils.getObjectStat(objectName).contentType();
            if ("image/png".equalsIgnoreCase(contentType)) return ".png";
            if ("image/jpeg".equalsIgnoreCase(contentType)) return ".jpg";
        } catch (Exception ignored) {
            // 无法读取元信息时使用通用图片扩展名。
        }
        return ".jpg";
    }

    private void addTextEntry(ZipOutputStream zip, String entryName, String content) throws IOException {
        zip.putNextEntry(new ZipEntry(entryName));
        zip.write(content.getBytes(StandardCharsets.UTF_8));
        zip.closeEntry();
    }

    private String safeEntryName(String name) {
        if (name == null || name.trim().isEmpty()) return "sample";
        String safe = name.replaceAll("[\\\\/:*?\"<>|\\r\\n]", "_").trim();
        return safe.isEmpty() ? "sample" : safe;
    }
}
