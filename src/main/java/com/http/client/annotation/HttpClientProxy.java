package com.http.client.annotation;


import com.http.client.proxy.AbstractHttpProxy;
import com.http.client.proxy.DefaultProxy;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 对httpclient设置动态代理对象注解
 *
 * @author linzhou
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface HttpClientProxy {

    /**
     * 设置代理类
     *
     *  @return 动态代理类
     */
    Class<?extends AbstractHttpProxy> value() default DefaultProxy.class;
}
