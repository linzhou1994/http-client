package com.http.controller;


import com.alibaba.fastjson.JSONObject;
import com.biz.tool.file.FileUtil;
import com.biz.tool.spring.http.ResponseUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
@RestController
@RequestMapping("file")
public class FileController {
    private static final String FILE_NAME = "downloadTestFile.txt";
    private static final String FILE_PATH = "download/downloadTestFile.txt";



    @PostMapping("uploadFile")
    public String uploadFile(MultipartFile file,String param) throws IOException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("url","uploadFile");
        jsonObject.put("param",param);
        FileUtil.getFile(file,"src/main/resources/upload");
        return jsonObject.toJSONString();
    }
    @PostMapping("downloadFile")
    public void uploadFile(String param, HttpServletResponse response) throws IOException {
        if ("123".equals(param)){
            ResponseUtil.downloadFile(response,FILE_PATH,FILE_NAME);
            return;
        }
        throw new IllegalArgumentException("param error");

    }
}
