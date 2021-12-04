package com.http.client.bo;

import java.util.ArrayList;
import java.util.List;

/**
 * http请求参数存储类
 *
 * @author linzhou
 */
public class MethodParamResult {

    /**
     * 表单格式的参数键值对
     */
    private List<NameValueParam> nameValueParams = new ArrayList<>();
    /**
     * 自定义的请求头
     */
    private HttpHeader httpHeader;
    /**
     * body形式的数据
     */
    private String body = null;
    /**
     * 需要上传的文件
     */
    private UploadFile uploadFile;
    /**
     * 指定url
     */
    private HttpUrl httpUrl;

    public void addNameValueParam(NameValueParam nameValueParam) {
        if (nameValueParam != null) {
            nameValueParams.add(nameValueParam);
        }
    }

    public HttpHeader getHttpHeader() {
        if (httpHeader != null) {
            return httpHeader;

        } else {
            return new HttpHeader();
        }
    }

    public void setHttpHeader(HttpHeader httpHeader) {
        this.httpHeader = httpHeader;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public UploadFile getUploadFile() {
        return uploadFile;
    }

    public void setUploadFile(UploadFile uploadFile) {
        this.uploadFile = uploadFile;
    }

    public List<NameValueParam> getNameValueParams() {
        return nameValueParams;
    }

    public void setNameValueParams(List<NameValueParam> nameValueParams) {
        this.nameValueParams = nameValueParams;
    }

    public HttpUrl getHttpUrl() {
        return httpUrl;
    }

    public void setHttpUrl(HttpUrl httpUrl) {
        this.httpUrl = httpUrl;
    }
}
