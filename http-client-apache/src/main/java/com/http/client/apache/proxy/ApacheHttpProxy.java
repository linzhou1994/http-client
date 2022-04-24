package com.http.client.apache.proxy;


import com.biz.tool.spring.SpringUtil;
import com.http.client.apache.proxy.handler.ApacheHttpProxyHandler;
import com.http.client.apache.proxy.handler.ApacheHttpProxyHandlerManager;
import com.http.client.context.HttpRequestContext;
import com.http.client.context.header.HttpHeader;
import com.http.client.proxy.AbstractHttpProxy;
import com.http.client.response.BaseHttpClientResponse;
import com.http.client.utils.AutoCloseUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.protocol.HTTP;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.*;

/**
 * @author linzhou
 * @version 1.0.0
 * @ClassName DefaultHttpProxy.java
 * @Description TODO
 * @createTime 2021年07月05日 16:25:00
 */
@Slf4j
public class ApacheHttpProxy extends AbstractHttpProxy {

    private CloseableHttpClient client;



    @Override
    protected BaseHttpClientResponse doInvoke(HttpRequestContext context) throws Throwable {

        HttpRequestBase httpRequest = getHttpRequestBase(context);
        try {
            CloseableHttpResponse response = getClient().execute(httpRequest);
            //设置自动关闭,在请求结束后自动关闭response
            AutoCloseUtil.addCloseable(response);
            HttpEntity entity = response.getEntity();
            int statusCode = response.getStatusLine().getStatusCode();
            InputStream content = entity.getContent();
            HttpHeader headers = getHeaders(response);
            Charset charset = getCharset(entity);
            return BaseHttpClientResponse.builder()
                    .charset(charset)
                    .responseHeard(headers)
                    .inputStream(content)
                    .code(statusCode)
                    .build();
        } catch (Exception e) {
            throw e;
        }
    }

    private HttpRequestBase getHttpRequestBase(HttpRequestContext context) throws Throwable {
        ApacheHttpProxyHandler handler = ApacheHttpProxyHandlerManager.getHandler(context.getHttpRequestMethod());
        if (handler == null) {
            throw new IllegalArgumentException("不支持的请求方式:" + context.getHttpRequestMethod());
        }
        HttpRequestBase httpRequest = handler.getRequest(context);
        setHeader(httpRequest, context);
        return httpRequest;
    }

    private void setHeader(HttpRequestBase httpRequest, HttpRequestContext context) {
        Map<String, List<String>> headers = context.getHttpHeader().getHeaders();
        for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
            String key = entry.getKey();
            for (String value : entry.getValue()) {
                httpRequest.addHeader(key, value);
            }
        }
    }

    private Charset getCharset(HttpEntity entity) {
        ContentType contentType = ContentType.get(entity);
        if (Objects.nonNull(contentType)) {
            return contentType.getCharset();
        }
        return HTTP.DEF_CONTENT_CHARSET;
    }

    private HttpHeader getHeaders(CloseableHttpResponse response) {
        Header[] allHeaders = response.getAllHeaders();
        HttpHeader httpHeader = new HttpHeader();
        for (Header header : allHeaders) {
            httpHeader.addHeader(header.getName(), header.getValue());
        }
        return httpHeader;
    }


    public CloseableHttpClient getClient() {
        if (Objects.isNull(client)) {
            synchronized (ApacheHttpProxy.class) {
                if (Objects.isNull(client)) {
                    this.client = SpringUtil.getBean(CloseableHttpClient.class);
                }
            }
        }
        return client;
    }
}
