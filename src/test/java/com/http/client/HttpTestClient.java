package com.http.client;

import com.http.client.annotation.HttpClient;
import com.http.client.annotation.HttpFile;
import com.http.client.annotation.HttpParam;
import com.http.client.enums.HttpRequestMethod;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@HttpClient(url = "http://127.0.0.1:8080/login",pathMethodName = false)
public interface HttpTestClient {
    /**
     * 普通get请求
     * @param name
     * @param password
     * @return
     */
    @HttpClient(path = "getLogin")
    String getLogin(@HttpParam("name")String name,@HttpParam("password")String password);

    /**
     * post请求
     * @param param
     * @return
     */
    @HttpClient(path = "postLogin",method = HttpRequestMethod.POST)
    String postLogin(LoginParam param);

    /**
     * 无参的get请求
     * @return
     */
    @HttpClient(path = "getInt",method = HttpRequestMethod.GET)
    int getInt();
    /**
     * 无参的post请求
     * @return
     */
    @HttpClient(path = "getDouble",method = HttpRequestMethod.POST)
    double getDouble();
    /**
     * 无参的post请求
     * @return
     */
    @HttpClient(path = "post",method = HttpRequestMethod.POST)
    String post();

    /**
     * 文件上传
     * @param file
     * @param param
     * @return
     */
    @HttpClient(url = "http://127.0.0.1:8080/file",path = "uploadFile",method = HttpRequestMethod.POST)
    String uploadMultipartFile(@HttpFile("file") MultipartFile file, @HttpParam("param")String param);

    /**
     * 文件上传
     * @param file
     * @param param
     * @return
     */
    @HttpClient(url = "http://127.0.0.1:8080/file",path = "uploadFile",method = HttpRequestMethod.POST)
    String uploadFile(@HttpFile("file")File file, @HttpParam("param")String param);
}
