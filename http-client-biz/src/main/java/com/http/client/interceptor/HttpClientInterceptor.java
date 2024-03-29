package com.http.client.interceptor;

import com.http.client.context.HttpRequestContext;
import com.http.client.response.BaseHttpClientResponse;
import com.http.client.response.HttpClientResponse;

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
     * @return 如果有返回值 则直接返回本方法的返回值当做本次请求的返回值
     */
    default Object runHttpBefore(HttpRequestContext context) {
        return null;
    }

    /**
     * http请求之后调用
     *
     * @param response 解析参数的上下文
     * @return 如果有返回值 则直接返回本方法的返回值当做本次请求的返回值
     * @throws Exception
     */
    default Object runHttpAfter(HttpClientResponse response) throws Exception {
        return null;
    }

    /**
     * 解析返回数据之后
     *
     * @param response 返回数据
     * @param rlt      原本要返回的值
     * @return 最终要返回值
     * @throws Exception
     */
    default Object returnObjectAfter(HttpClientResponse response, Object rlt) throws Exception {
        return rlt;
    }

    /**
     * http请求发生异常后调用
     *
     * @param context  请求上下文
     * @param response
     * @param e        异常类
     * @return 返回一个异常时的结果, 如果返回值为null, 则异常不处理
     * @throws Exception
     */
    default Object httpException(HttpRequestContext context, BaseHttpClientResponse response, Throwable e) throws Exception {
        return null;
    }


}
