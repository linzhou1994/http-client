package com.http.client.interceptor.impl;


import com.alibaba.fastjson.JSON;
import com.http.client.context.HttpRequestContext;
import com.http.client.context.form.Form;
import com.http.client.interceptor.HttpClientInterceptor;
import com.http.client.response.HttpClientResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.List;

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
        String body = context.getBody();
        List<Form> nameValueParams = context.getNameValueParams();

        String result = getResult(response, rlt);
        LogInfo logInfo = new LogInfo(httpUrl, body, nameValueParams, result);

        logger.info(JSON.toJSONString(logInfo));

        return rlt;
    }

    @Override
    public Object httpException(HttpRequestContext context, Throwable e) throws Exception {
        String httpUrl = context.getHttpUrl();
        String body = context.getBody();
        List<Form> nameValueParams = context.getNameValueParams();
        LogInfo logInfo = new LogInfo(httpUrl, body, nameValueParams, null);
        logger.error(JSON.toJSONString(logInfo), e);
        return null;
    }

    private String getResult(HttpClientResponse response, Object rlt) throws IOException {
        return (rlt instanceof File) || (rlt instanceof MockMultipartFile) ? "is file" : response.result();
    }

    public static class LogInfo {
        private String httpUrl;
        private String body;
        private List<Form> form;
        private String result;

        public LogInfo(String httpUrl, String body, List<Form> form, String result) {
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

        public String getBody() {
            return body;
        }

        public void setBody(String body) {
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
