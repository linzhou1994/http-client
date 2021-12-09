package com.http.client;


import lombok.extern.log4j.Log4j2;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

@Log4j2
public class HttpClientTest extends BaseTest {

    @Autowired
    private HttpTestClient httpTestClient;
    @Autowired
    private HttpTestClient2 httpTestClient2;

    @Test
    public void test1() {
        LoginParam p = new LoginParam();
        p.setPassword("123");
        p.setPhone("134");

        String rlt = httpTestClient.login(p);
        log.info("rlt1:" + rlt);
//        rlt = httpTestClient2.getTest();
//        log.info("rlt2:"+rlt);
    }

}
