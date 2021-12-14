package com.http.client.context.url;

import lombok.Data;

/**
 * 自定义http地址存储类
 * @author linzhou
 */
@Data
public class HttpUrl implements Url{
    private String url;

    public HttpUrl(String url) {
        this.url = url;
    }
}
