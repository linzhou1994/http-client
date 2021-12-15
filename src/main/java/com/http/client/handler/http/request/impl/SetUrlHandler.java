package com.http.client.handler.http.request.impl;

import com.http.client.bo.HttpClientRequest;
import com.http.client.context.url.Url;
import com.http.client.handler.http.request.SetHttpParamHandler;
import org.springframework.stereotype.Component;

/**
 * @author linzhou
 * @ClassName SetBodyHandler.java
 * @createTime 2021年12月15日 12:05:00
 * @Description
 */
@Component
public class SetUrlHandler implements SetHttpParamHandler {
    @Override
    public boolean setHttpParam(HttpClientRequest request, Object o) {
        if (o instanceof Url){
            request.setHttpUrl((Url) o);
            return true;
        }
        return false;
    }
}
