package com.http.client.context.form;


import com.http.client.annotation.HttpParam;

/**
 * 表单提交参数存储类
 * @author linzhou
 */

public class NameValueParam implements Form {

    private String name;
    private String value;

    public NameValueParam(String name, String value) {
        this.name = name;
        this.value = value;
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

    public void setValue(String value) {
        this.value = value;
    }
}
