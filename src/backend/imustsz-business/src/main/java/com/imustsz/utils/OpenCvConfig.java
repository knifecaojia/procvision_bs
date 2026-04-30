package com.imustsz.utils;

import lombok.extern.slf4j.Slf4j;
import nu.pattern.OpenCV;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * OpenCV 全局配置类 (基于 OpenPNP)
 */
@Slf4j
@Configuration
public class OpenCvConfig {

    @PostConstruct
    public void init() {
        try {
            OpenCV.loadShared();
            log.info("==== OpenCV (OpenPNP) 本地库加载成功 ====");
        } catch (Exception e) {
            log.error("==== OpenCV (OpenPNP) 本地库加载失败 ====", e);
        }
    }
}
