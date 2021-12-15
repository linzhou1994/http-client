package com.http.client.handler.http.request.impl;

import com.http.client.bo.HttpClientRequest;
import com.http.client.handler.http.request.SetHttpParamHandler;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @author linzhou
 * @ClassName SetBodyHandler.java
 * @createTime 2021年12月15日 12:05:00
 * @Description
 */
@Component
@Order
public class SetBodyHandler implements SetHttpParamHandler {
    @Override
    public boolean setHttpParam(HttpClientRequest request, Object o) {

        if (o instanceof String){
            request.setBody((String) o);
            return true;
        }

        return false;
    }
}
