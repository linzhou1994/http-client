package com.http.client;

import com.http.client.annotation.HttpClient;
import com.http.client.annotation.HttpFile;
import com.http.client.annotation.HttpParam;
import com.http.client.enums.HttpRequestMethod;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@HttpClient(url = "http://127.0.0.1:8080/login",pathMethodName = false)
public interface HttpTestClient {


    @HttpClient(path = "getLogin")
    String getLogin(@HttpParam("name")String name,@HttpParam("password")String password);

    @HttpClient(path = "postLogin",method = HttpRequestMethod.POST)
    String postLogin(LoginParam param);

    @HttpClient(path = "post",method = HttpRequestMethod.POST)
    String post();

    @HttpClient(url = "http://127.0.0.1:8080/file",path = "uploadFile",method = HttpRequestMethod.POST)
    String uploadMultipartFile(@HttpFile("file") MultipartFile file, @HttpParam("param")String param);

    @HttpClient(url = "http://127.0.0.1:8080/file",path = "uploadFile",method = HttpRequestMethod.POST)
    String uploadFile(@HttpFile("file")File file, @HttpParam("param")String param);



}
