package com.http.client;

import com.http.client.annotation.HttpClient;
import com.http.client.annotation.HttpClientProxy;
import com.http.client.annotation.HttpParam;
import com.http.client.proxy.DefaultProxy2;

@HttpClient(url = "www.alibaba.com",pathMethodName = true)
@HttpClientProxy(DefaultProxy2.class)
public interface HttpTestClient2 {


    String getTest(@HttpParam("param")String test);

}
