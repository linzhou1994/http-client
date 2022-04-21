package com.http.client.handler.http.request.impl;

import com.http.client.bo.HttpClientRequest;
import com.http.client.context.body.file.FileBody;
import com.http.client.handler.http.request.SetHttpParamHandler;
import org.springframework.stereotype.Component;

/**
 * @author linzhou
 * @ClassName SetFileBodyHandler.java
 * @createTime 2021年12月15日 12:05:00
 * @Description
 */
@Component
public class SetFileBodyHandler implements SetHttpParamHandler {
    @Override
    public boolean setHttpParam(HttpClientRequest request, Object methodParam) {
        if (methodParam instanceof FileBody) {
            //处理文件上传
            request.setUploadFile((FileBody) methodParam);
            return true;
        }
        return false;
    }
}
