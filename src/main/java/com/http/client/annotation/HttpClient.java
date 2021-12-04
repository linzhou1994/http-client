package com.http.client.annotation;


import com.http.client.enums.HttpRequestMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * HttpClient注解
 *
 * @author linzhou
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface HttpClient {

    /**
     * HTTP url路径
     *
     * @return
     */
    String url() default "";

    /**
     * http url后面的路径
     *
     * @return
     */
    String path() default "";

    /**
     * 如果path()不为"",这此方法不生效
     * 如果为true并且path()返回的是"",则会将方法名称作为path
     *
     * @return
     */
    boolean pathMethodName() default true;

    /**
     * 请求类型
     *
     * @return
     */
    HttpRequestMethod method() default HttpRequestMethod.NULL;


}
