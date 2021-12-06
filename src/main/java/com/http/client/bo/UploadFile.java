package com.http.client.bo;

import com.http.client.annotation.HttpFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * 文件上传存储类
 *
 * @author linzhou
 */
public class UploadFile {

    private HttpFile httpFile;

    private MultipartFile file;

    private Map<String, String> param;

    public UploadFile(HttpFile httpFile, MultipartFile file) {
        this.httpFile = httpFile;
        this.file = file;
    }

    public UploadFile(HttpFile httpFile, MultipartFile file, Map<String, String> param) {
        this.httpFile = httpFile;
        this.file = file;
        this.param = param;
    }

    public HttpFile getHttpFile() {
        return httpFile;
    }

    public void setHttpFile(HttpFile httpFile) {
        this.httpFile = httpFile;
    }

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }

    public Map<String, String> getParam() {
        return param;
    }

    public void setParam(Map<String, String> param) {
        this.param = param;
    }

    public String getName(){
        return httpFile.value();
    }
}
