package com.http.client.bo;

import com.biz.tool.file.FileUtil;
import com.http.client.utils.HttpClientFileUtil;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * 文件上传存储类
 *
 * @author linzhou
 */
public class FileParam {

    private MultipartFile file;

    private Map<String, String> param;

    public FileParam(MultipartFile file, Map<String, String> param) {
        this.file = file;
        this.param = param;
    }

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }

    public void setFile(File file) throws IOException {
        this.file = FileUtil.getMockMultipartFile(file);
    }

    public Map<String, String> getParam() {
        return param;
    }

    public void setParam(Map<String, String> param) {
        this.param = param;
    }
}
