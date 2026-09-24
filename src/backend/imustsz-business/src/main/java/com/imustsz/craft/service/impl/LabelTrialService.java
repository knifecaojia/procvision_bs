package com.imustsz.craft.service.impl;

import com.fasterxml.jackson.databind.*;
import com.imustsz.craft.domain.BizStep;
import com.imustsz.craft.domain.dto.LabelTrialRequest;
import com.imustsz.craft.mapper.LabelTrialMapper;
import com.imustsz.common.exception.ServiceException;
import com.imustsz.common.utils.bean.MinioUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.security.*;
import java.util.*;
import java.util.concurrent.Semaphore;

@Service
public class LabelTrialService {

    private final LabelTrialMapper mapper;
    private final MinioUtils minio;
    private final String bucket;
    private final long maxPixels, maxBytes;
    private final byte[] secret = new byte[32];
    private final Semaphore rendering = new Semaphore(1);
    private final ObjectMapper json = new ObjectMapper();

    public LabelTrialService(LabelTrialMapper mapper, MinioUtils minio, @Value("${minio.bucketName}") String bucket, @Value("${craft.label-trial.max-pixels:120000000}") long maxPixels, @Value("${craft.label-trial.max-bytes:268435456}") long maxBytes) {
        this.mapper = mapper;
        this.minio = minio;
        this.bucket = bucket;
        this.maxPixels = maxPixels;
        this.maxBytes = maxBytes;
        new SecureRandom().nextBytes(secret);
    }

    private BizStep required(Long id) {
        BizStep step = id == null ? null : mapper.read(id);
        if (step == null) throw new ServiceException("工步不存在");
        return step;
    }

    private String sign(String value) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(secret, "HmacSHA256"));
        return Base64.getUrlEncoder().withoutPadding().encodeToString(mac.doFinal(value.getBytes(StandardCharsets.UTF_8)));
    }

    private String version(BizStep step) throws Exception {
        return sign(json.writeValueAsString(Arrays.asList(step.getId(), step.getGuideMapUrl(), step.getGuideInfo())));
    }

    private String token(Long id, String key) throws Exception {
        return sign(id + "\n" + key);
    }

    private String original(BizStep step) throws Exception {
        if (step.getGuideMapUrl() == null || step.getGuideMapUrl().isEmpty()) return null;
        JsonNode keys = json.readTree(step.getGuideMapUrl());
        if (!keys.isArray() || keys.size() == 0 || !keys.get(0).isTextual())
            throw new ServiceException("历史图片地址格式无效");
        return keys.get(0).asText();
    }

    public Map<String, Object> info(Long id) throws Exception {
        BizStep step = required(id);
        String key = original(step);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("id", id);
        result.put("code", step.getCode());
        result.put("content", step.getContent());
        result.put("coordsInfo", step.getGuideInfo());
        result.put("version", version(step));
        result.put("originalKey", key);
        result.put("originalToken", key == null ? null : token(id, key));
        result.put("guideMapUrl", key == null ? null : minio.getPresignedUrl(key));
        return result;
    }

    public Map<String, Object> upload(Long id) throws Exception {
        required(id);
        String key = "craft/label-trial/" + id + "/" + UUID.randomUUID() + "/original";
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("objectName", key);
        result.put("token", token(id, key));
        result.put("url", minio.generatePresignedUploadUrlHTTPS(key));
        return result;
    }

    public Map<String, Object> save(LabelTrialRequest request) throws Exception {
        BizStep old = required(request.getId());
        if (!version(old).equals(request.getVersion()))
            throw new ServiceException("工步已变化或服务已重启，请重新打开后再保存");
        JsonNode coords = request.getCoordsInfo();
        if (coords == null || !coords.isArray() || coords.size() > 10000)
            throw new ServiceException("标注数据格式或数量无效");
        int count = 0;
        for (JsonNode group : coords) {
            if (request.isPackageMode()) {
                if (!group.path("productInfo").isTextual() || !group.path("quantity").isIntegralNumber() || group.path("quantity").asInt() < 1)
                    throw new ServiceException("包装信息无效");
            } else {
                if (!group.path("label").isTextual() || !group.path("posList").isArray())
                    throw new ServiceException("标注分组无效");
                count += group.path("posList").size();
                if (count > 10000) throw new ServiceException("标注框数量超过上限");
            }
        }
        String coordsText = json.writeValueAsString(coords);
        if (coordsText.length() > 2000000) throw new ServiceException("标注数据过大");
        String key = request.getOriginalKey();
        if (key == null || key.isEmpty()) {
            if (!request.isPackageMode() || original(old) != null) throw new ServiceException("缺少原图");
            if (mapper.save(old.getId(), null, coordsText, old.getGuideMapUrl(), old.getGuideInfo()) != 1)
                throw new ServiceException("工步已被修改，请重新加载");
            old.setGuideInfo(coordsText);
            return result(old, null, null);
        }
        String expected = token(old.getId(), key);
        if (request.getOriginalToken() == null || !MessageDigest.isEqual(expected.getBytes(StandardCharsets.UTF_8), request.getOriginalToken().getBytes(StandardCharsets.UTF_8)))
            throw new ServiceException("原图凭据失效，请重新打开工步");
        if (!rendering.tryAcquire()) throw new ServiceException("服务器正在处理另一张大图，请稍后重试");
        Path source = null, target = null;
        String output = null;
        boolean committed = false;
        try {
            if (minio.getObjectStat(key).size() > maxBytes) throw new ServiceException("原图超过试验版文件大小上限");
            source = Files.createTempFile("label-source-", ".image");
            target = Files.createTempFile("label-render-", ".png");
            try (InputStream in = minio.getFileInputStream(bucket, key); OutputStream out = Files.newOutputStream(source)) {
                byte[] buffer = new byte[65536];
                long size = 0;
                int n;
                while ((n = in.read(buffer)) != -1) {
                    size += n;
                    if (size > maxBytes) throw new ServiceException("原图超过文件大小上限");
                    out.write(buffer, 0, n);
                }
            }
            LabelTrialRenderer.render(source, target, request.getWidth(), request.getHeight(), coords, request.isPackageMode(), maxPixels);
            output = "craft/label-trial/" + old.getId() + "/" + UUID.randomUUID() + "/annotated.png";
            try (InputStream in = Files.newInputStream(target)) {
                minio.uploadFile(output, in, Files.size(target), "image/png");
            }
            String images = json.writeValueAsString(Arrays.asList(key, output));
            if (mapper.save(old.getId(), images, coordsText, old.getGuideMapUrl(), old.getGuideInfo()) != 1)
                throw new ServiceException("工步已被修改，请重新加载；原数据未覆盖");
            committed = true;
            old.setGuideMapUrl(images);
            old.setGuideInfo(coordsText);
            return result(old, key, output);
        } catch (IOException ex) {
            throw new ServiceException(ex.getMessage());
        } finally {
            if (!committed && output != null) {
                try {
                    minio.deleteFile(output);
                } catch (Exception ignored) {
                }
            }
            if (source != null) {
                try {
                    Files.deleteIfExists(source);
                } catch (IOException ignored) {
                }
            }
            if (target != null) {
                try {
                    Files.deleteIfExists(target);
                } catch (IOException ignored) {
                }
            }
            rendering.release();
        }
    }

    private Map<String, Object> result(BizStep step, String key, String output) throws Exception {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("version", version(step));
        data.put("originalKey", key);
        data.put("originalToken", key == null ? null : token(step.getId(), key));
        data.put("annotatedKey", output);
        return data;
    }
}
