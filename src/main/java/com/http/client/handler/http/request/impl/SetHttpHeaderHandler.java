package com.http.client.handler.http.request.impl;

import com.http.client.bo.HttpClientRequest;
import com.http.client.context.header.HttpHeader;
import com.http.client.handler.http.request.SetHttpParamHandler;
import org.springframework.stereotype.Component;

/**
 * @author linzhou
 * @ClassName SetHttpHeaderHandler.java
 * @createTime 2021年12月15日 12:05:00
 * @Description
 */
@Component
public class SetHttpHeaderHandler implements SetHttpParamHandler {
    @Override
    public boolean setHttpParam(HttpClientRequest request, Object o) {
        if (o instanceof HttpHeader) {
            //设置请求头
            request.addHttpHeader((HttpHeader) o);
            return true;
        }
        return false;
    }
}
