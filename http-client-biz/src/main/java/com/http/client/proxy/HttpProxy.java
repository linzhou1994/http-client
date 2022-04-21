package com.http.client.proxy;

/**
 * 获取httpClient bean对象的接口
 * @author linzhou
 */
public interface HttpProxy {

    /**
     * 返回代理对象
     * @param <T>
     * @return
     */
    <T> T newProxyInstance();
}
