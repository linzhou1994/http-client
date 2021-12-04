package com.http.client.bo;


import com.http.client.annotation.HttpParam;

/**
 * 表单提交参数存储类
 * @author linzhou
 */

public class NameValueParam {

    private HttpParam httpParam;
    private String value;

    public NameValueParam(HttpParam httpParam, String value) {
        this.httpParam = httpParam;
        this.value = value;
    }

    public String getName(){
        return httpParam.value();
    }

    public HttpParam getHttpParam() {
        return httpParam;
    }

    public void setHttpParam(HttpParam httpParam) {
        this.httpParam = httpParam;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
