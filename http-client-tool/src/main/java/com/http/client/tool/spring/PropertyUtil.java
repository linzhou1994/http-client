package com.http.client.tool.spring;

import org.springframework.core.env.Environment;

import java.util.Objects;

/**
 * @author linzhou
 * @ClassName PropertyUtil.java
 * @createTime 2022年01月28日 18:30:00
 * @Description
 */
public class PropertyUtil {
    private static Environment environment;

    private static Environment getEnvironment() {
        if (Objects.isNull(environment)) {
            environment = SpringUtil.getBean(Environment.class);
        }
        return environment;
    }

    public static String getProperty(String key) {
        return getEnvironment().getProperty(key);
    }
}
