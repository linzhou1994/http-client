package com.http.client.response;

import com.http.client.bo.HttpHeader;
import com.http.client.context.HttpRequestContext;
import lombok.Builder;
import lombok.Data;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * @author linzhou
 * @ClassName HttpClientResponse.java
 * @createTime 2021年12月08日 15:40:00
 * @Description
 */
@Data
@Builder
public class HttpClientResponse {
    private int code;
    private final Charset charset;
    private HttpHeader httpHeader = new HttpHeader();
    private HttpRequestContext context;
    private InputStream inputStream;

    private Charset charset() {
        return charset != null ? charset : Charset.forName("UTF-8");
    }

    public String string() throws IOException {
        return new String(getByte(), this.charset().name());
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

    public String getHeader(String name) {
        return httpHeader.getHeader(name, null);
    }

    public String getHttpUrl() {
        return context.getHttpUrl();
    }

    public boolean isSuccessful() {
        return this.code >= 200 && this.code < 300;
    }
}
