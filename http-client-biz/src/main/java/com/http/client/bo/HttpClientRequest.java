package com.http.client.bo;

import com.http.client.context.body.Body;
import com.http.client.context.body.file.FileBody;
import com.http.client.context.form.Form;
import com.http.client.context.header.HttpHeader;
import com.http.client.context.url.Url;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * http请求参数存储类
 *
 * @author linzhou
 */
public class HttpClientRequest {

    /**
     * 表单格式的参数键值对
     */
    private List<Form> nameValueParams = new ArrayList<>();
    /**
     * 自定义的请求头
     */
    private HttpHeader httpHeader = new HttpHeader();
    /**
     * body形式的数据
     */
    private Body body = null;
    /**
     * 需要上传的文件
     */
    private List<FileBody> uploadFiles = new ArrayList<>();
    /**
     * 指定url
     */
    private Url httpUrl;

    public void addNameValueParam(Form nameValueParam) {
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

    public void addHttpHeader(HttpHeader httpHeader) {
        if (Objects.isNull(this.httpHeader)){
            this.httpHeader = httpHeader;
        }else {
            this.httpHeader.addHeader(httpHeader);
        }
    }

    public Body getBody() {
        return body;
    }

    public void setBody(Body body) {
        this.body = body;
    }

    public List<FileBody> getUploadFiles() {
        return uploadFiles;
    }

    public void setUploadFiles(List<FileBody> uploadFiles) {
        this.uploadFiles = uploadFiles;
    }

    public void setUploadFile(FileBody uploadFile) {
        if (Objects.nonNull(uploadFile)) {
            this.uploadFiles.add(uploadFile);
        }
    }

    public List<Form> getNameValueParams() {
        return nameValueParams;
    }

    public void setNameValueParams(List<Form> nameValueParams) {
        this.nameValueParams = nameValueParams;
    }

    public Url getHttpUrl() {
        return httpUrl;
    }

    public void setHttpUrl(Url httpUrl) {
        this.httpUrl = httpUrl;
    }
}
