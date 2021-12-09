package com.http.client;

import com.http.client.annotation.HttpClient;
import com.http.client.enums.HttpRequestMethod;

@HttpClient(url = "http://127.0.0.1:8080/login",pathMethodName = false)
public interface HttpTestClient {


    String getTest();

    @HttpClient(path = "login",method = HttpRequestMethod.POST)
    String login(LoginParam param);



}
