package com.http.client.interceptor.impl;


import com.alibaba.fastjson.JSON;
import com.http.client.context.HttpRequestContext;
import com.http.client.context.body.Body;
import com.http.client.context.form.Form;
import com.http.client.interceptor.HttpClientInterceptor;
import com.http.client.response.BaseHttpClientResponse;
import com.http.client.response.HttpClientResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * @description:
 * @author: linzhou
 * @create: 2021-12-31 17:04
 **/
@Component
public class LogInterceptor implements HttpClientInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(LogInterceptor.class);


    @Override
    public Object httpAfter(HttpClientResponse response, Object rlt) throws Exception {
        String httpUrl = response.getHttpUrl();
        HttpRequestContext context = response.getContext();
        Body body = context.getBody();
        List<Form> nameValueParams = context.getNameValueParams();

        String result = getResult(response, response.getContext().getReturnType());
        LogInfo logInfo = new LogInfo(httpUrl, body, nameValueParams, result);

        logger.info(JSON.toJSONString(logInfo));

        return rlt;
    }

    @Override
    public Object httpException(HttpRequestContext context, BaseHttpClientResponse response, Throwable e) throws Exception {
        String httpUrl = context.getHttpUrl();
        Body body = context.getBody();
        List<Form> nameValueParams = context.getNameValueParams();
        String result = getResult(response, context.getReturnType());
        LogInfo logInfo = new LogInfo(httpUrl, body, nameValueParams, result);
        logger.error(JSON.toJSONString(logInfo), e);
        return null;
    }

    private String getResult(HttpClientResponse response, Class<?> returnType) throws IOException {
        if (Objects.isNull(response)){
            return "null";
        }

        return returnType.isAssignableFrom( File.class) || returnType.isAssignableFrom( MultipartFile.class) ? "is file" : response.result();
    }

    public static class LogInfo {
        private String httpUrl;
        private Body body;
        private List<Form> form;
        private String result;

        public LogInfo(String httpUrl, Body body, List<Form> form, String result) {
            this.httpUrl = httpUrl;
            this.body = body;
            this.form = form;
            this.result = result;
        }

        public String getHttpUrl() {
            return httpUrl;
        }

        public void setHttpUrl(String httpUrl) {
            this.httpUrl = httpUrl;
        }

        public Body getBody() {
            return body;
        }

        public void setBody(Body body) {
            this.body = body;
        }

        public List<Form> getForm() {
            return form;
        }

        public void setForm(List<Form> form) {
            this.form = form;
        }

        public String getResult() {
            return result;
        }

        public void setResult(String result) {
            this.result = result;
        }
    }

}
