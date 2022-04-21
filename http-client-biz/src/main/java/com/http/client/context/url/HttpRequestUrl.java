package com.http.client.context.url;

import lombok.Data;

/**
 * 自定义http地址存储类
 * @author linzhou
 */
@Data
public class HttpRequestUrl implements Url{
    private String url;

    public HttpRequestUrl(String url) {
        this.url = url;
    }
}
