package com.http.client.handler.http.request;

import com.http.client.bo.HttpClientRequest;

/**
 * @author linzhou
 * @ClassName SetHttpParam.java
 * @createTime 2021年12月15日 11:47:00
 * @Description
 */
public interface SetHttpParamHandler {

    /**
     * 将参数添加到request中去
     *
     * @param request
     * @param o       需要添加到request中的参数
     * @return 是否拦截后面的所有处理器
     */
    boolean setHttpParam(HttpClientRequest request, Object o);

}
