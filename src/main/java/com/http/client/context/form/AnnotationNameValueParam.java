package com.http.client.context.form;


import com.http.client.annotation.HttpParam;

/**
 * 表单提交参数存储类
 * @author linzhou
 */

public class AnnotationNameValueParam implements Form {

    private HttpParam httpParam;
    private String value;

    public AnnotationNameValueParam(HttpParam httpParam, String value) {
        this.httpParam = httpParam;
        this.value = value;
    }

    @Override
    public String getName(){
        return httpParam.value();
    }

    public HttpParam getHttpParam() {
        return httpParam;
    }

    public void setHttpParam(HttpParam httpParam) {
        this.httpParam = httpParam;
    }

    @Override
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
