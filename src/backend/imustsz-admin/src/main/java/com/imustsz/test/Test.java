package com.imustsz.test;

import com.imustsz.common.core.domain.AjaxResult;
import com.imustsz.common.utils.bean.MinioUtils;
import io.minio.errors.*;
import io.minio.messages.Item;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/test")
public class Test {

    @Autowired
    private MinioUtils minioUtils;

    @GetMapping
    public AjaxResult test() throws Exception {

        String url = minioUtils.getPresignedUrl("demo", "demo.jpeg");
        List<Item> items = minioUtils.listFiles("demo", "");
        log.info("url: {}", url);
        items.forEach(item -> log.info("item: {},{},{}", item.objectName(), item.storageClass(), item.etag()));
        minioUtils.deleteDir("demo", "test/");
        return AjaxResult.success(url);
    }
}
