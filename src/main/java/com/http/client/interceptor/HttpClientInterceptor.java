package com.http.client.interceptor;

import com.http.client.response.HttpClientResponse;
import com.http.client.context.HttpRequestContext;

/**
 * @author linzhou
 * @ClassName HttpClientHandler.java
 * @createTime 2021年12月08日 14:31:00
 * @Description
 */
public interface HttpClientInterceptor {

    /**
     * 参数解析之后,调用http请求之前执行
     *
     * @param context 解析参数的上下文
     * @return  如果有返回值 则直接返回本方法的返回值当做本次请求的返回值
     */
    default Object httpBefore(HttpRequestContext context) {
        return null;
    }

    /**
     * http请求之后调用
     *
     * @param response 返回数据
     * @param rlt 原本要返回的值
     * @return 最终要返回值
     */
    default Object httpAfter(HttpClientResponse response,Object rlt) throws Exception {
        return rlt;
    }


}
