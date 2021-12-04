package com.http.test;

import com.http.client.annotation.HttpClient;
import com.http.client.annotation.HttpClientProxy;
import com.http.client.annotation.HttpParam;
import com.http.client.proxy.DefaultProxy2;

@HttpClient(url = "www.qq.com",pathMethodName = true)
@HttpClientProxy(DefaultProxy2.class)
public interface HttpTestClient3 {


    String getTest(@HttpParam("param")String test);

}
