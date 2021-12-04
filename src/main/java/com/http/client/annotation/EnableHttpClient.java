package com.http.client.annotation;

import com.http.client.registrar.HttpClientRegistrar;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * httpClient启动注解
 *
 * @author linzhou
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(HttpClientRegistrar.class)
public @interface EnableHttpClient {

    /**
     * 设置httpclient的包名
     *
     * @return
     */
    String[] basePackages() default {};
}
