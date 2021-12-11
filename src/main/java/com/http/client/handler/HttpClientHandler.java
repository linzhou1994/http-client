package com.http.client.handler;

import com.http.client.response.HttpClientResponse;
import com.http.client.context.HttpRequestContext;

/**
 * @author linzhou
 * @ClassName HttpClientHandler.java
 * @createTime 2021年12月08日 14:31:00
 * @Description
 */
public interface HttpClientHandler {

    /**
     * 参数解析之后,调用http请求之前执行
     *
     * @param context 解析参数的上下文
     */
    void httpBefore(HttpRequestContext context);

    /**
     * http请求之后调用
     *
     * @param response 返回数据
     * @return
     */
    void httpAfter(HttpClientResponse response) throws Exception;
}
