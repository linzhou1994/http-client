package com.http.client.enums;

/**
 * 请求方式枚举
 * @author linzhou
 */
public enum HttpRequestMethod {
    /**
     * get请求
     */
    GET,
    /**
     * post请求
     */
    POST,
    /**
     * 不指定,如果方法上为null,则会去取接口上的值,如果接口上也是null,默认get
     */
    NULL,
    PUT,
    DELETE,
    HEAD,
    OPTIONS,
    TRACE,
    PATCH
}
