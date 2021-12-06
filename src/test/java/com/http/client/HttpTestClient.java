package com.http.client;

import com.http.client.annotation.HttpClient;
import com.http.client.annotation.HttpParam;

@HttpClient(url = "www.baidu.com",pathMethodName = false)
public interface HttpTestClient {


    String getTest();



}
