package com.imustsz.utils;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "webservice")
@Data
public class WebServiceProperties {
    private String url;
    private String targetNamespace;
    private String serviceName;
    private String targetSysName;
    private String targetSysNum;
    private String oriSysName;
    private String oriSysNum;
    private String secLevel;
}
