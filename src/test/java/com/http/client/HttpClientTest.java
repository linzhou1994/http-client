package com.http.client;


import lombok.extern.log4j.Log4j2;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

@Log4j2
public class HttpClientTest extends BaseTest{

    @Autowired
    private HttpTestClient httpTestClient;

    @Test
    public void test1(){
        String rlt = httpTestClient.getTest();
        log.info("rlt1:"+rlt);
    }

}
