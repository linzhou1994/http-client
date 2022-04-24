package com.http.client.apache.proxy;


import com.biz.tool.spring.SpringUtil;
import com.http.client.apache.config.ApacheHttpConfig;
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
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HTTP;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.security.cert.X509Certificate;
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


    private ApacheHttpConfig apacheHttpConfig;



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
                    this.client = initClient();
                }
            }
        }
        return client;
    }

    /**
     * 初始化httpClient
     *
     * @return
     */
    private static CloseableHttpClient initClient() {
        ApacheHttpConfig apacheHttpConfig = SpringUtil.getBean(ApacheHttpConfig.class);
        // 设置协议http和https对应的处理socket链接工厂的对象
        RegistryBuilder<ConnectionSocketFactory> socketFactoryBuilder = RegistryBuilder.create();
        socketFactoryBuilder.register("http", PlainConnectionSocketFactory.INSTANCE);
        // 设置协议http和https对应的处理socket链接工厂的对象
        Optional.ofNullable(createIgnoreVerifySSL(apacheHttpConfig))
                .ifPresent(sslContext -> socketFactoryBuilder.register("https",
                        new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE)));
        // 设置链接池
        PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(socketFactoryBuilder.build());
        connManager.setMaxTotal(apacheHttpConfig.getMaxTotalConnections());
        connManager.setDefaultMaxPerRoute(apacheHttpConfig.getMaxConnectionPerHost());
        //创建自定义的httpclient对象
        return HttpClients.custom().setConnectionManager(connManager)
                .setDefaultRequestConfig(RequestConfig.custom().setSocketTimeout(apacheHttpConfig.getSocketTimeout())
                        .setConnectTimeout(apacheHttpConfig.getConnectionTimeout()).setConnectionRequestTimeout(apacheHttpConfig.getConnectionRequestTimeout()).build()
                ).build();
    }

    /**
     * 创建ssl
     *
     * @return
     * @param apacheHttpConfig
     */
    private static SSLContext createIgnoreVerifySSL(ApacheHttpConfig apacheHttpConfig) {
        try {
            SSLContext ctx = SSLContext.getInstance(apacheHttpConfig.getSslProtocol());
            X509TrustManager tm = new X509TrustManager() {
                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                @Override
                public void checkClientTrusted(X509Certificate[] arg0, String arg1) {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] arg0, String arg1) {
                }
            };
            ctx.init(null, new TrustManager[]{tm}, null);
            return ctx;
        } catch (Exception e) {
            log.error("SSL创建失败:" + e);
        }
        return null;
    }
}
