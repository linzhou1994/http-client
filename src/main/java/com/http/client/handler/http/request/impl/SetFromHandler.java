package com.http.client.handler.http.request.impl;

import com.http.client.bo.HttpClientRequest;
import com.http.client.context.form.From;
import com.http.client.handler.http.request.SetHttpParamHandler;
import org.springframework.stereotype.Component;

/**
 * @author linzhou
 * @ClassName SetBodyHandler.java
 * @createTime 2021年12月15日 12:05:00
 * @Description
 */
@Component
public class SetFromHandler implements SetHttpParamHandler {
    @Override
    public boolean setHttpParam(HttpClientRequest request, Object o) {
        if (o instanceof From) {
            //处理表单参数
            request.addNameValueParam((From) o);
            return true;
        }

        return false;
    }
}
