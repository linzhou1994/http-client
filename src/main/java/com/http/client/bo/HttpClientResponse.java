package com.http.client.bo;

import com.http.client.context.HttpRequestContext;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.internal.Util;
import lombok.Builder;
import lombok.Data;

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
    private byte[] bytes;
    private MediaType contentType;
    private HttpHeader httpHeader = new HttpHeader();
    private HttpRequestContext context;

    private Charset charset() {
        MediaType contentType = this.contentType;
        return contentType != null ? contentType.charset(Util.UTF_8) : Util.UTF_8;
    }

    public final String string() throws IOException {
        return new String(this.bytes, this.charset().name());
    }

    public String getHeader(String name) {
        return httpHeader.getHeader(name,null);
    }

    public String getHttpUrl(){
        return context.getHttpUrl();
    }
}
