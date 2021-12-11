package com.http.client;

import com.http.client.annotation.HttpClient;
import com.http.client.annotation.HttpFile;
import com.http.client.annotation.HttpParam;
import com.http.client.enums.HttpRequestMethod;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@HttpClient(url = "http://127.0.0.1:8080/file")
public interface HttpUploadFileClient {


    @HttpClient(path = "uploadFile")
    String uploadFile(@HttpFile("file")File file, @HttpParam("param")String param);

    @HttpClient(path = "uploadFile")
    String uploadMultipartFile(@HttpFile("file") MultipartFile file, @HttpParam("param")String param);



}
