package com.http.client.bo;

import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 请求头存储类
 *
 * @author linzhou
 */
public class HttpHeader {

    private Map<String, List<String>> headers;

    public HttpHeader() {
        this.headers = new HashMap<>();
    }

    public HttpHeader(Map<String, List<String>> header) {
        if (header != null) {
            this.headers = header;
        } else {
            this.headers = new HashMap<>();
        }
    }

    public void addHeader(HttpHeader httpHeader) {
        if (httpHeader != null && !httpHeader.isEmpty()) {
            for (Map.Entry<String, List<String>> entry : httpHeader.getHeaders().entrySet()) {
                addHeader(entry.getKey(),entry.getValue());
            }
        }
    }

    public void addHeader(String key, String value) {
        List<String> values = headers.get(key);
        if (Objects.isNull(values)) {
            values = new ArrayList<>();
            headers.put(key, values);
        }
        values.add(value);
    }

    public void addHeader(String key, List<String> addValues) {
        List<String> values = headers.get(key);
        if (Objects.isNull(values)) {
            values = new ArrayList<>();
            headers.put(key, values);
        }
        values.addAll(addValues);
    }

    public void removeHeader(String key) {
        headers.remove(key);
    }

    public void clearHeader() {
        headers.clear();
    }

    public Map<String, List<String>> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        if (Objects.isNull(headers)){
            return;
        }
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            addHeader(entry.getKey(),entry.getValue());
        }
    }

    public boolean isEmpty() {
        return headers.isEmpty();
    }

    public String getHeader(String name, String defaultValue) {
        if (headers.containsKey(name)) {
            List<String> values = headers.get(name);
            if (CollectionUtils.isNotEmpty(values)) {
                return values.get(0);
            }
        }
        return defaultValue;
    }
}
