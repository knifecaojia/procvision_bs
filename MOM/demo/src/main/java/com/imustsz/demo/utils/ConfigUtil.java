package com.imustsz.demo.utils;

import java.io.InputStream;
import java.util.Properties;

public class ConfigUtil {
    private static Properties properties = new Properties();

    static {
        try (InputStream input = ConfigUtil.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input != null) {
                properties.load(input);
            }
        } catch (Exception ex) {
            System.err.println("未找到 config.properties，将完全依赖环境变量");
        }
    }

    // 提供一个获取配置的方法
    public static String get(String key) {
        // 1. 将 key 转换为大写并替换点号为下划线，符合环境变量规范
        // 例如：service.address 变成 SERVICE_ADDRESS
        String envKey = key.toUpperCase().replace(".", "_");

        // 2. 优先尝试从 Docker 环境中获取
        String envValue = System.getenv(envKey);
        if (envValue != null && !envValue.trim().isEmpty()) {
            return envValue; // 如果 docker-compose 配置了，直接返回！
        }

        // 3. 如果环境中没有，则回退到 config.properties 中的配置
        return properties.getProperty(key);
    }
}