package com.http.client.annotation;

import com.http.client.proxy.AbstractHttpProxy;
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
     * 设置需要扫描的httpclient的包名
     *
     * @return
     */
    String[] basePackages() default {};

    /**
     * 设置全局默认代理类,优先级高于配置文件中的设置
     *
     *  @return 动态代理类
     */
    Class<?extends AbstractHttpProxy> defaultProxy() ;
}
