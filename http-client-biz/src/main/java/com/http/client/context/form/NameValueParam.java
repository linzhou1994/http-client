package com.http.client.context.form;


import com.http.client.context.ContentType;

/**
 * 表单提交参数存储类
 *
 * @author linzhou
 */

public class NameValueParam implements Form {

    private String name;
    private String value;
    private String mimeType;
    private String charset;

    public NameValueParam(String name, String value) {
        this.name = name;
        this.value = value;
        this.mimeType = ContentType.APPLICATION_X_WWW_FORM_URLENCODED;
        this.charset = ContentType.UTF8;
    }

    public NameValueParam(String name, String value, String mimeType, String charset) {
        this.name = name;
        this.value = value;
        this.mimeType = mimeType;
        this.charset = charset;
    }


    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public String mimeType() {
        return mimeType;
    }

    @Override
    public String charset() {
        return charset;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }


    public void setCharset(String charset) {
        this.charset = charset;
    }
}
