package com.http.client.annotation;

import com.http.client.context.ContentType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 表单提交参数注解
 *
 * @author linzhou
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface HttpParam {

    /**
     * 参数名称
     *
     * @return
     */
    String value() default "";

    /**
     * 编码格式
     *
     * @return
     */
    String charset() default ContentType.UTF8;

    String mimeType() default ContentType.APPLICATION_X_WWW_FORM_URLENCODED;
}
