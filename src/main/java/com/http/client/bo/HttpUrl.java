package com.http.client.bo;

import lombok.Data;

/**
 * 自定义http地址存储类
 * @author linzhou
 */
@Data
public class HttpUrl {
    private String url;

    public HttpUrl(String url) {
        this.url = url;
    }
}
