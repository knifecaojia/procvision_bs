package com.imustsz.common.utils.bean;

import com.imustsz.common.utils.uuid.UUID;
import io.minio.*;
import io.minio.errors.*;
import io.minio.http.Method;
import io.minio.messages.Bucket;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import io.minio.messages.Item;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.*;
import java.nio.file.Files;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class MinioUtils {

    private final MinioClient minioClient;

    @Value("${minio.bucketName}")
    private String bucketName;

    @Value("${minio.presigned-url-expire}")
    private int presignedUrlExpire;

    public MinioUtils(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

//    public String uploadFile(String originalFilename, String dir) throws IOException, NoSuchAlgorithmException, ServerException, InvalidKeyException, InsufficientDataException, ErrorResponseException, InvalidResponseException, XmlParserException, InternalException {
//
//        boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
//        if (!found) {
//            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
//        }
//
//        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
//
//        String objectName = LocalDate.now().toString().replace("-", "") + UUID.randomUUID().toString().replace("-", "") + suffix;
//
//        minioClient.uploadObject(
//                UploadObjectArgs.builder()
//                        .bucket(bucketName)
//                        .object(dir + "/" + objectName)
//                        .filename(originalFilename)
//                        .build());
//
//        log.info("文件：{} 成功上传至bucket：{}", objectName, bucketName);
//
//        return objectName;
//    }

    /**
     * 项目启动时，自动创建默认桶（如果不存在）
     */
    @PostConstruct
    public void initDefaultBucket() {
        try {
            if (!bucketExists(bucketName)) {
                createBucket(bucketName);
            }
        } catch (Exception e) {
            throw new RuntimeException("初始化MinIO默认桶失败", e);
        }
    }

    // ======================== 桶相关操作 ========================

    /**
     * 创建桶
     * @param bucketName 桶名称
     * @throws Exception 异常
     */
    public void createBucket(String bucketName) throws Exception {
        if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
        }
    }

    /**
     * 判断桶是否存在
     * @param bucketName 桶名称
     * @return true-存在，false-不存在
     * @throws Exception 异常
     */
    public boolean bucketExists(String bucketName) throws Exception {
        return minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
    }

    /**
     * 获取所有桶列表
     * @return 桶列表
     * @throws Exception 异常
     */
    public List<Bucket> listBuckets() throws Exception {
        return minioClient.listBuckets();
    }

    /**
     * 删除桶（桶必须为空才能删除）
     * @param bucketName 桶名称
     * @throws Exception 异常
     */
    public void removeBucket(String bucketName) throws Exception {
        minioClient.removeBucket(RemoveBucketArgs.builder().bucket(bucketName).build());
    }

    // ======================== 文件相关操作 ========================

    /**
     * 上传文件（本地文件）
     * @param bucketName 桶名称
     * @param objectName 存储在MinIO中的文件名（可包含路径，如：test/1.jpg）
     * @param filePath 本地文件路径
     * @throws Exception 异常
     */
    public void uploadFile(String objectName, String filePath) throws Exception {
        File file = new File(filePath);
        uploadFile(objectName, Files.newInputStream(file.toPath()), file.length(), getFileContentType(objectName));
    }

    /**
     * 上传文件（输入流）
     * @param bucketName 桶名称
     * @param objectName 存储在MinIO中的文件名
     * @param inputStream 文件输入流
     * @param fileSize 文件大小（字节）
     * @param contentType 文件类型（如：image/jpeg）
     * @throws Exception 异常
     */
    public void uploadFile(String objectName, InputStream inputStream, long fileSize, String contentType) throws Exception {
        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .stream(inputStream, fileSize, -1) // -1表示自动检测分片大小
                        .contentType(contentType)
                        .build()
        );
    }

    /**
     * 上传文件（字节数组）
     * @param bucketName 桶名称
     * @param objectName 存储在MinIO中的文件名
     * @param bytes 文件字节数组
     * @param contentType 文件类型
     * @throws Exception 异常
     */
    public void uploadFile(String objectName, byte[] bytes, String contentType) throws Exception {
        uploadFile(objectName, new ByteArrayInputStream(bytes), bytes.length, contentType);
    }

    /**
     * 下载文件到本地
     * @param bucketName 桶名称
     * @param objectName MinIO中的文件名
     * @param localPath 本地保存路径（如：D:/download/1.jpg）
     * @throws Exception 异常
     */
    public void downloadFile(String bucketName, String objectName, String localPath) throws Exception {
        File file = new File(localPath);
        // 创建父目录
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        // 下载文件
        minioClient.downloadObject(
                DownloadObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .filename(localPath)
                        .build()
        );
    }

    /**
     * 获取文件的输入流
     * @param bucketName 桶名称
     * @param objectName MinIO中的文件名
     * @return 文件输入流
     * @throws Exception 异常
     */
    public InputStream getFileInputStream(String bucketName, String objectName) throws Exception {
        return minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .build()
        );
    }

    /**
     * 获取文件的字节数组
     * @param bucketName 桶名称
     * @param objectName MinIO中的文件名
     * @return 字节数组
     * @throws Exception 异常
     */
    public byte[] getFileBytes(String bucketName, String objectName) throws Exception {
        try (InputStream inputStream = getFileInputStream(bucketName, objectName)) {
            return IOUtils.toByteArray(inputStream);
        }
    }

    /**
     * 删除文件
     * @param objectName MinIO中的文件名
     * @throws Exception 异常
     */
    public void deleteFile(String objectName) throws Exception {
        minioClient.removeObject(
                RemoveObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .build()
        );
    }

    /**
     * 删除文件夹下的所有文件
     * @param bucketName 桶名称
     * @param prefix 文件夹路径(必须有'/'，例如 test/)
     * @throws Exception 异常
     */
    public void deleteDir(String bucketName, String prefix) throws Exception {
        List<Item> items = listFiles(bucketName, prefix);

        List<DeleteObject> deleteObjects = new ArrayList<>();
        for (Item item : items) {
            deleteObjects.add(new DeleteObject(item.objectName()));
        }

        if (!deleteObjects.isEmpty()){
            Iterable<Result<DeleteError>> results = minioClient.removeObjects(
                    RemoveObjectsArgs.builder()
                            .bucket(bucketName)
                            .objects(deleteObjects)
                            .build()
            );

            for (Result<DeleteError> result : results) {
                DeleteError error = result.get();
                log.error("删除文件失败，bucketName: {}, objectName: {}, code: {}, message: {}",
                        error.bucketName(), error.objectName(), error.code(), error.message());
            }
            log.info("删除文件成功，bucketName: {}, objectName: {}", bucketName, prefix);
        }
    }

    /**
     * 列出桶中的所有文件
     * @param bucketName 桶名称
     * @param prefix 文件名前缀（可选，如：test/ 表示列出test目录下的文件）
     * @return 文件列表
     * @throws Exception 异常
     */
    public List<Item> listFiles(String bucketName, String prefix) throws Exception {
        Iterable<Result<Item>> results = minioClient.listObjects(
                ListObjectsArgs.builder()
                        .bucket(bucketName)
                        .prefix(prefix)
                        .recursive(true) // 是否递归查询子目录
                        .build()
        );

        List<Item> items = new ArrayList<>();
        // 转换为List
        results.forEach(result -> {
            try {
                items.add(result.get());
            } catch (Exception e) {
                throw new RuntimeException("获取文件列表失败", e);
            }
        });
        return items;
    }

    /**
     * 获取文件的预签名访问链接
     * @param bucketName 桶名称
     * @param objectName MinIO中的文件名
     * @param expires 有效期（单位：秒）
     * @return 预签名访问链接
     * @throws Exception 异常
     */
    public String getPresignedUrl(String bucketName, String objectName, int expires) throws Exception {
        return minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .method(Method.GET) // 访问方式：GET（下载）、PUT（上传）等
                        .expiry(expires, TimeUnit.SECONDS)
                        .build()
        );
    }


    /**
     * 生成上传对象的预签名 URL
     * @param objectName 要上传的对象名（文件路径/文件名）
     * @return 预签名 URL 字符串
     */
    public String generatePresignedUploadUrl(String objectName) throws MinioException, InvalidKeyException, NoSuchAlgorithmException, IOException {
        // 生成预签名 URL
        return minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .method(Method.PUT)
                        .expiry(presignedUrlExpire, TimeUnit.SECONDS)
                        .build()
        );
    }

    /**
     * 获取文件的预签名访问链接（使用配置文件中的默认有效期）
     * @param objectName MinIO中的文件名
     * @return 预签名访问链接
     * @throws Exception 异常
     */
    public String getPresignedUrl(String objectName) throws Exception {
        return getPresignedUrl(bucketName, objectName, presignedUrlExpire);
    }

    /**
     * 获取文件的Content-Type（默认返回application/octet-stream）
     * @param fileName 文件名
     * @return Content-Type
     */
    private String getFileContentType(String fileName) {
        // 简单的文件类型映射，可根据需要扩展
        if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (fileName.endsWith(".png")) {
            return "image/png";
        } else if (fileName.endsWith(".txt")) {
            return "text/plain; charset=utf-8";
        } else if (fileName.endsWith(".json")) {
            return "application/json; charset=utf-8";
        } else {
            return "application/octet-stream"; // 二进制流（默认）
        }
    }

}
