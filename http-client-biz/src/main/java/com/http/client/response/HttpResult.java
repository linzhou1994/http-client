package com.http.client.response;

import com.http.client.context.header.HttpHeader;

/**
 * CopyRight : <company domain>
 * Project :  http-client
 * Comments : <对此类的描述，可以引用系统设计中的描述>
 * JDK version : JDK1.8
 * Create Date : 2022-10-13 10:44
 *
 * @author : linzhou
 * @version : 1.0
 * @since : 1.0
 */
public class HttpResult <T>{

    private final int code;

    private HttpHeader responseHeard;

    private T result;

    public HttpResult(BaseHttpClientResponse response,T result) {
        this.code = response.getCode();
        this.responseHeard = response.getResponseHeard();
        this.result = result;
    }

    public int getCode() {
        return code;
    }

    public HttpHeader getResponseHeard() {
        return responseHeard;
    }

    public T getResult() {
        return result;
    }
}
