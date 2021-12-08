package com.http.client;

import com.http.client.annotation.HttpClient;
import com.http.client.annotation.HttpParam;
import com.http.client.bo.HttpHeader;
import com.http.client.enums.HttpRequestMethod;

@HttpClient(url = "www.baidu.com",pathMethodName = false)
public interface HttpTestClient {


    String getTest();



}
