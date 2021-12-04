package com.http.client.config;


import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 全局配置类
 *
 * @author linzhou
 */
@Component
@ConfigurationProperties(prefix = "httpclient")
@Data
public class HttpClientConfig {

    private static final String SPLIT = ",";

    /**
     * 全局默认的动态代理
     */
    private String defaultProxyClass;

    private String scanHttpClientPackages;

    public List<String> getScanHttpClientPackages() {
        if (StringUtils.isNotBlank(scanHttpClientPackages)) {
            return Arrays.asList(scanHttpClientPackages.split(SPLIT));
        }
        return Collections.emptyList();
    }
}
