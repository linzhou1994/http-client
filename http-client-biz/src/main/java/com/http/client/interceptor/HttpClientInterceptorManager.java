package com.http.client.interceptor;

import com.http.client.context.HttpRequestContext;
import com.http.client.proxy.AbstractHttpProxy;
import com.http.client.response.BaseHttpClientResponse;
import com.http.client.tool.spring.SpringUtil;

import java.util.List;
import java.util.Objects;

/**
 * CopyRight : <company domain>
 * Project :  http-client
 * Comments : <对此类的描述，可以引用系统设计中的描述>
 * JDK version : JDK1.8
 * Create Date : 2022-10-19 15:24
 *
 * @author : linzhou
 * @version : 1.0
 * @since : 1.0
 */
public class HttpClientInterceptorManager {

    /**
     * 拦截器集合
     */
    private static List<HttpClientInterceptor> httpClientInterceptorList;

    private static List<HttpClientInterceptor> getHttpClientInterceptorList() {
        if (httpClientInterceptorList == null) {
            synchronized (AbstractHttpProxy.class) {
                if (httpClientInterceptorList == null) {
                    httpClientInterceptorList = SpringUtil.getBeanList(HttpClientInterceptor.class);
                }
            }
        }
        return httpClientInterceptorList;
    }


    /**
     * 执行httpBefore方法
     *
     * @param context
     */
    public static Object runHttpBefore(HttpRequestContext context) {
        for (HttpClientInterceptor httpClientInterceptor : getHttpClientInterceptorList()) {
            Object rlt = httpClientInterceptor.runHttpBefore(context);
            if (Objects.nonNull(rlt)) {
                return rlt;
            }
        }
        return null;
    }

    /**
     * 执行runHttpAfter方法
     *
     * @param response
     */
    public static Object runHttpAfter(BaseHttpClientResponse response) throws Exception {
        for (HttpClientInterceptor httpClientInterceptor : getHttpClientInterceptorList()) {
            Object rlt = httpClientInterceptor.runHttpAfter(response);
            if (Objects.nonNull(rlt)) {
                return rlt;
            }
        }
        return null;
    }

    /**
     * 执行returnObjectAfter方法
     *
     * @param response
     */
    public static Object returnObjectAfter(BaseHttpClientResponse response, Object rlt) throws Exception {
        for (HttpClientInterceptor httpClientInterceptor : getHttpClientInterceptorList()) {
            rlt = httpClientInterceptor.returnObjectAfter(response, rlt);
        }
        return rlt;
    }

    /**
     * 执行httpAfter方法
     *
     * @param context
     * @param response
     * @param e
     */
    public static Object runHttpException(HttpRequestContext context, BaseHttpClientResponse response, Throwable e) throws Exception {
        Object rlt = null;
        for (HttpClientInterceptor httpClientInterceptor : getHttpClientInterceptorList()) {
            rlt = httpClientInterceptor.httpException(context, response, e);
        }
        return rlt;
    }


}
