package com.http.client.bo;

import com.http.client.context.body.FileBody;
import com.http.client.context.form.From;
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
public class MethodParamResult {

    /**
     * 表单格式的参数键值对
     */
    private List<From> nameValueParams = new ArrayList<>();
    /**
     * 自定义的请求头
     */
    private HttpHeader httpHeader = new HttpHeader();
    /**
     * body形式的数据
     */
    private String body = null;
    /**
     * 需要上传的文件
     */
    private List<FileBody> uploadFiles = new ArrayList<>();
    /**
     * 指定url
     */
    private Url httpUrl;

    public void addNameValueParam(From nameValueParam) {
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

    public List<From> getNameValueParams() {
        return nameValueParams;
    }

    public void setNameValueParams(List<From> nameValueParams) {
        this.nameValueParams = nameValueParams;
    }

    public Url getHttpUrl() {
        return httpUrl;
    }

    public void setHttpUrl(Url httpUrl) {
        this.httpUrl = httpUrl;
    }

    public void addMethodParam(Object methodParam) {
        if (methodParam instanceof From) {
            //处理表单参数
            addNameValueParam((From) methodParam);
        } else if (methodParam instanceof FileBody) {
            //处理文件上传
            setUploadFile((FileBody) methodParam);
        } else if (methodParam instanceof HttpHeader) {
            //设置请求头
            httpHeader.addHeader((HttpHeader) methodParam);
        } else if (methodParam instanceof Url) {
            //处理自定义url
            setHttpUrl((Url) methodParam);
        } else {
            //处理body
            setBody((String) methodParam);
        }
    }
}
