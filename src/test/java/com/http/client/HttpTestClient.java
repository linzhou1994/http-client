package com.http.client;

import com.http.client.annotation.HttpClient;
import com.http.client.annotation.HttpParam;

@HttpClient(url = "www.baidu.com",pathMethodName = true)
public interface HttpTestClient {


    String getTest(@HttpParam("param")String test);

}
