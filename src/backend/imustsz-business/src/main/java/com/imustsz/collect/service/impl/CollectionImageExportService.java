package com.imustsz.collect.service.impl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.imustsz.collect.domain.*;
import com.imustsz.collect.mapper.CollectionManageMapper;
import com.imustsz.common.exception.ServiceException;
import com.imustsz.common.utils.bean.MinioUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.security.MessageDigest;
import java.util.*;
import java.util.zip.*;

@Service
public class CollectionImageExportService {
    private final CollectionManageMapper mapper;
    private final MinioUtils minio;
    private final String bucket;
    private final long maxBytes;
    public CollectionImageExportService(CollectionManageMapper mapper, MinioUtils minio,
        @Value("${minio.bucketName}") String bucket,
        @Value("${collection.export.max-total-bytes:1073741824}") long maxBytes) {
        this.mapper=mapper; this.minio=minio; this.bucket=bucket; this.maxBytes=maxBytes;
    }
    /** Finish on disk before sending headers: missing objects cannot become a partial success ZIP. */
    public Path createArchive(CollectionExportRequest request) throws Exception {
        if(request.getIds()==null || request.getIds().isEmpty() || request.getIds().size()>100)
            throw new ServiceException("请选择1至100条图像记录导出");
        Set<Long> unique=new LinkedHashSet<>(request.getIds());
        if(unique.contains(null) || unique.stream().anyMatch(id->id<=0)) throw new ServiceException("选中的记录编号无效");
        CollectionManageQuery query=request.getQuery()==null ? new CollectionManageQuery() : request.getQuery();
        query.validate();
        List<BizDataCollection> records=mapper.selectManagedExport(query,new ArrayList<>(unique));
        if(records.size()!=unique.size() || records.stream().anyMatch(r->!Integer.valueOf(1).equals(r.getType())))
            throw new ServiceException("部分记录已删除、不属于采集业务或不符合筛选条件，请重新查询并选择");
        List<String> keys=new ArrayList<>(), names=new ArrayList<>(); long total=0;
        for(BizDataCollection row:records){
            List<String> objects=CollectionManageService.objectNames(row.getImagePath());
            if(objects.isEmpty()) throw new ServiceException("记录"+row.getId()+"没有原图，请取消该记录后重试");
            String key=objects.get(0); io.minio.StatObjectResponse stat=minio.getObjectStat(key);
            total+=stat.size(); if(total>maxBytes) throw new ServiceException("所选原图合计超过导出上限，请分批选择（默认1GB）");
            keys.add(key);
            names.add(segment(row.getProductModel(),"未填写型号")+"/"+segment(row.getProductBatch(),"未填写批次")+"/"
                +segment(row.getProcessNum(),"未填写工序")+"/"+row.getId()+"_"+segment(row.getData(),"图像")+extension(key,stat.contentType()));
        }
        Path archive=Files.createTempFile("collection-export-", ".zip"); boolean complete=false;
        try {
            List<Map<String,Object>> manifest=new ArrayList<>(); long copied=0;
            try(ZipOutputStream zip=new ZipOutputStream(Files.newOutputStream(archive),StandardCharsets.UTF_8)){
                zip.setLevel(Deflater.NO_COMPRESSION);
                byte[] buffer=new byte[65536];
                for(int i=0;i<records.size();i++){
                    BizDataCollection row=records.get(i);
                    zip.putNextEntry(new ZipEntry(names.get(i)));
                    try(InputStream input=minio.getFileInputStream(bucket,keys.get(i))){
                        int count;while((count=input.read(buffer))!=-1){copied+=count;if(copied>maxBytes)throw new ServiceException("图片内容变化后超出导出上限，请分批导出");zip.write(buffer,0,count);}
                    }
                    zip.closeEntry();
                    Map<String,Object> item=new LinkedHashMap<>();item.put("id",row.getId());item.put("file",names.get(i));
                    item.put("data",row.getData());item.put("productTime",row.getProductTime()==null ? null : new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(row.getProductTime()));
                    item.put("productModel",row.getProductModel());item.put("productBatch",row.getProductBatch());item.put("processNum",row.getProcessNum());item.put("otherInfo",row.getOtherInfo());manifest.add(item);
                }
                zip.putNextEntry(new ZipEntry("manifest.json"));zip.write(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsBytes(manifest));zip.closeEntry();
            }
            complete=true;return archive;
        }finally{if(!complete)Files.deleteIfExists(archive);}
    }
    static String segment(String value,String fallback) throws Exception {
        if(value==null || value.trim().isEmpty())return fallback;
        String original=value.trim();String safe=original.replaceAll("[\\\\/:*?\"<>|\\p{Cntrl}]","_").replaceAll("[. ]+$","");
        if(safe.isEmpty())safe=fallback;
        if(safe.matches("(?i)(CON|PRN|AUX|NUL|COM[1-9]|LPT[1-9])(\\..*)?"))safe="_"+safe;
        if(safe.length()>48)safe=safe.substring(0,48);
        if(!safe.equals(original)){
            byte[] digest=MessageDigest.getInstance("SHA-256").digest(original.getBytes(StandardCharsets.UTF_8));
            StringBuilder suffix=new StringBuilder();for(int i=0;i<4;i++)suffix.append(String.format("%02x",digest[i]&255));safe+="_"+suffix;
        }
        return safe;
    }
    private String extension(String key,String mime){
        int dot=key.lastIndexOf('.');String ext=dot<0?"":key.substring(dot).toLowerCase(Locale.ROOT);
        if(Arrays.asList(".jpg",".jpeg",".png",".bmp",".tif",".tiff",".gif",".webp").contains(ext))return ext;
        if("image/jpeg".equals(mime))return ".jpg";if("image/png".equals(mime))return ".png";return ".bin";
    }
}
