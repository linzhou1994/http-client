package com.http.client.bo;

import java.util.HashMap;
import java.util.Map;

/**
 * 请求头存储类
 * @author linzhou
 */
public class HttpHeader {

    private Map<String, String> headers;

    public HttpHeader() {
        this.headers = new HashMap<>();
    }

    public HttpHeader(Map<String, String> header) {
        if (header != null) {
            this.headers = header;
        } else {
            this.headers = new HashMap<>();
        }
    }

    public void addHeader(HttpHeader httpHeader) {
        if (httpHeader != null && !httpHeader.isEmpty()) {
            headers.putAll(httpHeader.getHeaders());
        }
    }

    public void addHeader(String key, String value) {
        headers.put(key, value);
    }

    public void removeHeader(String key) {
        headers.remove(key);
    }

    public void clearHeader() {
        headers.clear();
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public boolean isEmpty() {
        return headers.isEmpty();
    }
}
