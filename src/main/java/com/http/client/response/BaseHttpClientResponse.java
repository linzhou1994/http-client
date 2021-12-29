package com.http.client.response;

import com.http.client.context.HttpRequestContext;
import com.http.client.context.header.HttpHeader;
import lombok.Builder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * @author linzhou
 * @ClassName HttpClientResponse.java
 * @createTime 2021年12月08日 15:40:00
 * @Description
 */
@Builder
public class BaseHttpClientResponse implements HttpClientResponse {
    private final int code;
    private final Charset charset;
    private HttpHeader responseHeard;
    private HttpRequestContext context;
    private final InputStream inputStream;
    private String result;

    private Charset charset() {
        return charset != null ? charset : StandardCharsets.UTF_8;
    }

    @Override
    public String result() throws IOException {
        if (Objects.isNull(result)){
            result = new String(getByte(), this.charset().name());
        }
        return result;
    }

    public byte[] getByte() throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int n = 0;
        while (-1 != (n = inputStream.read(buffer))) {
            output.write(buffer, 0, n);
        }
        return output.toByteArray();
    }

    @Override
    public String getResponseHeard(String name) {
        return getResponseHeard().getHeader(name, null);
    }

    @Override
    public String getHttpUrl() {
        return context.getHttpUrl();
    }

    @Override
    public HttpRequestContext getContext() {
        return this.context;
    }

    @Override
    public void setContext(HttpRequestContext context) {
        this.context = context;
    }

    @Override
    public InputStream getInputStream() {
        return this.inputStream;
    }

    @Override
    public int getCode() {
        return this.code;
    }

    @Override
    public boolean isSuccessful() {
        return this.code >= 200 && this.code < 300;
    }

    @Override
    public HttpHeader getResponseHeard() {
        if (Objects.isNull(this.responseHeard)){
            this.responseHeard = new HttpHeader();
        }
        return responseHeard;
    }
}
