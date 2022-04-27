package com.http.client.tool.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author linzhou
 * @ClassName SpringConfig.java
 * @createTime 2021年12月13日 10:53:00
 * @Description
 */
@Configuration
public class HttpClientToolSpringConfig {

    @Bean
    public SpringUtil getSpringUtil(){
        return new SpringUtil();
    }
}
