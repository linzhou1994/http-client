package com.http.client;

import com.http.client.annotation.HttpClient;
import com.http.client.annotation.HttpParam;

@HttpClient(path = "12345")
public interface HttpTestClient2 {

    @HttpClient(path = "werwer")
    String getTest();

}
