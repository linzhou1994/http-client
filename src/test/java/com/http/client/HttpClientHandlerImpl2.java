package com.http.client;

import com.http.client.context.HttpRequestContext;
import com.http.client.handler.HttpClientHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

/**
 * @author linzhou
 * @ClassName HttpClientHandler.java
 * @createTime 2021年12月08日 14:50:00
 * @Description
 */
@Service
@Slf4j
@Order()
public class HttpClientHandlerImpl2 implements HttpClientHandler {
    @Override
    public void httpBefore(HttpRequestContext context) {
        log.info("HttpClientHandlerImpl2:httpBefore" );
    }

    @Override
    public Object httpAfter(HttpRequestContext context, Object rlt) {
        log.info("HttpClientHandlerImpl2:httpAfter" + rlt.toString());
        return rlt;
    }
}
