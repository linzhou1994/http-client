package com.http.client.context.body.file;

import com.http.client.annotation.HttpFile;
import com.http.client.tool.file.FileUtil;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * 文件上传存储类
 *
 * @author linzhou
 */
public class UploadFile implements FileBody {

    private HttpFile httpFile;

    private File file;

    private Map<String, String> param;

    public UploadFile(HttpFile httpFile, File file) {
        this.httpFile = httpFile;
        this.file = file;
    }

    public UploadFile(HttpFile httpFile, File file, Map<String, String> param) {
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

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public Map<String, String> getParam() {
        return param;
    }

    public void setParam(Map<String, String> param) {
        this.param = param;
    }

    @Override
    public String getName() {
        return httpFile.value();
    }

    @Override
    public String getFileName() {
        return file.getName();
    }

    @Override
    public byte[] getFileBytes() throws IOException {
        return FileUtil.getFileBytes(file);
    }
}
