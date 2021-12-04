package com.http.client;


import com.http.test.HttpTestClient3;
import lombok.extern.log4j.Log4j2;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

@Log4j2
public class HttpClientTest extends BaseTest{

    @Autowired
    private HttpTestClient httpTestClient;
    @Autowired
    private HttpTestClient2 httpTestClient2;
    @Autowired
    private HttpTestClient3 httpTestClient3;

    @Test
    public void test1(){
        String rlt = httpTestClient.getTest("testParam");
        log.info("rlt1:"+rlt);
        rlt = httpTestClient2.getTest("testParam");
        log.info("rlt2:"+rlt);
        rlt = httpTestClient3.getTest("testParam");
        log.info("rlt3:"+rlt);
    }

}
