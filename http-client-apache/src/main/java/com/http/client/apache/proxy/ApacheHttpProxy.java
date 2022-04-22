package com.http.client.apache.proxy;


import com.http.client.context.HttpRequestContext;
import com.http.client.context.body.Body;
import com.http.client.context.body.file.FileBody;
import com.http.client.context.form.Form;
import com.http.client.context.header.HttpHeader;
import com.http.client.proxy.AbstractHttpProxy;
import com.http.client.response.BaseHttpClientResponse;
import com.http.client.utils.AutoCloseUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
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

    private static final int CONNECTION_TIMEOUT = 300000;
    private static final int SOCKET_TIMEOUT = 300000;
    private static final int CONNECTION_REQUEST_TIMEOUT = 300000;
    private static final int MAX_CONNECTION_PER_HOST = 20;
    private static final int MAX_TOTAL_CONNECTIONS = 100;
    private static final String SSL_PROTOCOL = "SSL";

    private CloseableHttpClient client;

    public static final String DEFAULT_ENCODING = "UTF-8";


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
        HttpRequestBase httpRequest;
        switch (context.getHttpRequestMethod()) {
            case POST:
                httpRequest = getHttpPost(context);
                break;
            case GET:
            case NULL:
                httpRequest = new HttpGet(context.getHttpUrl());
                break;
            default:
                throw new HttpException("不支持的请求类型");
        }
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

    /**
     * 创建post请求
     *
     * @param context
     * @return
     * @throws UnsupportedEncodingException
     */
    private HttpRequestBase getHttpPost(HttpRequestContext context) throws Throwable {
        String url = context.getHttpUrl();
        HttpPost httpPost = new HttpPost(url);

        HttpEntity entity = getHttpEntity(context);
        if (entity != null) {
            //设置entity
            httpPost.setEntity(entity);
        }
        return httpPost;
    }

    private HttpEntity getHttpEntity(HttpRequestContext context) throws IOException {

        Body body = context.getBody();
        if (Objects.nonNull(body) && StringUtils.isNotBlank(body.getBody())) {
            //设置body
            String mimeType = body.mimeType();
            String charset = body.charset();
            ContentType contentType = ContentType.create(mimeType, charset);
            return new StringEntity(body.getBody(), ContentType.APPLICATION_JSON);
        }
        List<FileBody> uploadFiles = context.getUploadFiles();
        List<Form> nameValueParams = context.getNameValueParams();
        if (CollectionUtils.isEmpty(uploadFiles)) {

            List<NameValuePair> pairs = new ArrayList<>(nameValueParams.size());
            for (Form nameValueParam : nameValueParams) {
                String value = nameValueParam.getValue();
                if (value != null) {
                    pairs.add(new BasicNameValuePair(nameValueParam.getName(), value));
                }
            }
            return new UrlEncodedFormEntity(pairs, StandardCharsets.UTF_8);
        }
        //设置文件上传
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        //加上此行代码解决返回中文乱码问题
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        if (CollectionUtils.isNotEmpty(uploadFiles)) {
            FileBody fileBody = uploadFiles.get(0);

            ByteArrayBody byteArrayBody = new ByteArrayBody(fileBody.getFileBytes(), fileBody.getFileName());
            String value = StringUtils.isBlank(fileBody.getName()) ? "src/test/resources/file" : fileBody.getName();
            builder.addPart(value, byteArrayBody);

        }

        if (CollectionUtils.isNotEmpty(nameValueParams)) {
            for (Form nameValueParam : nameValueParams) {
                String mimeType = nameValueParam.mimeType();
                String charset = nameValueParam.charset();
                ContentType contentType = ContentType.create(mimeType, charset);
                builder.addTextBody(nameValueParam.getName(), nameValueParam.getValue(), contentType);
            }
        }
        return builder.build();
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
        // 设置协议http和https对应的处理socket链接工厂的对象
        RegistryBuilder<ConnectionSocketFactory> socketFactoryBuilder = RegistryBuilder.create();
        socketFactoryBuilder.register("http", PlainConnectionSocketFactory.INSTANCE);
        // 设置协议http和https对应的处理socket链接工厂的对象
        Optional.ofNullable(createIgnoreVerifySSL())
                .ifPresent(sslContext -> socketFactoryBuilder.register("https",
                        new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE)));
        // 设置链接池
        PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(socketFactoryBuilder.build());
        connManager.setMaxTotal(MAX_TOTAL_CONNECTIONS);
        connManager.setDefaultMaxPerRoute(MAX_CONNECTION_PER_HOST);
        //创建自定义的httpclient对象
        return HttpClients.custom().setConnectionManager(connManager)
                .setDefaultRequestConfig(RequestConfig.custom().setSocketTimeout(CONNECTION_TIMEOUT)
                        .setConnectTimeout(SOCKET_TIMEOUT).setConnectionRequestTimeout(CONNECTION_REQUEST_TIMEOUT).build()
                ).build();
    }

    /**
     * 创建ssl
     *
     * @return
     */
    private static SSLContext createIgnoreVerifySSL() {
        try {
            SSLContext ctx = SSLContext.getInstance(SSL_PROTOCOL);
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
