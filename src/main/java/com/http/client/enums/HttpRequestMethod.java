package com.http.client.enums;

/**
 * 请求方式枚举
 * @author linzhou
 */
public enum HttpRequestMethod {
    GET,
    POST,
    /**
     * 不指定,默认get
     */
    NULL
    ;

    HttpRequestMethod() {
    }
}
