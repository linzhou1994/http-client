package com.http.client.utils;


import com.http.client.context.body.FileBody;
import com.http.client.context.form.From;
import com.http.client.context.header.HttpHeader;
import com.http.client.context.HttpRequestContext;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.mock.web.MockMultipartFile;

import java.io.*;
import java.net.*;
import java.util.*;

public class OkHttpClientUtil {

    private static OkHttpClient mOkHttpClient;


    private OkHttpClientUtil() {
        mOkHttpClient = new OkHttpClient();
        //cookie enabled
        mOkHttpClient.setCookieHandler(new CookieManager(null, CookiePolicy.ACCEPT_ORIGINAL_SERVER));
    }

    static {
        mOkHttpClient = new OkHttpClient();
        //cookie enabled
        mOkHttpClient.setCookieHandler(new CookieManager(null, CookiePolicy.ACCEPT_ORIGINAL_SERVER));
    }

    /**
     * 同步的Get请求
     *
     * @param context
     * @return Response
     */
    public static Response getResponse(HttpRequestContext context) throws IOException {

        Headers.Builder builder = getHeadersBuilder(context);

        final Request request = new Request.Builder()
                .url(context.getHttpUrl())
                .headers(builder.build())
                .build();
        Call call = mOkHttpClient.newCall(request);
        return call.execute();
    }

    private static Headers.Builder getHeadersBuilder(HttpRequestContext context) {
        Headers.Builder builder = new Headers.Builder();
        HttpHeader httpHeader = context.getHttpHeader();
        if (httpHeader != null && !httpHeader.isEmpty()) {
            for (Map.Entry<String, List<String>> header : httpHeader.getHeaders().entrySet()) {
                for (String value : header.getValue()) {
                    builder.add(header.getKey(), value);
                }
            }
        }
        return builder;
    }

    /**
     * 同步的Post请求
     *
     * @param context post的参数
     * @return
     */
    public static Response postResponse(HttpRequestContext context) throws IOException {

        return post(context);
    }


    /**
     * 同步的Post请求
     *
     * @param context post的参数
     * @return
     */
    private static Response post(HttpRequestContext context) throws IOException {
        Request request = buildPostRequest(context);
        return mOkHttpClient.newCall(request).execute();
    }


    private static String guessMimeType(String path) {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String contentTypeFor = fileNameMap.getContentTypeFor(path);
        if (contentTypeFor == null) {
            contentTypeFor = "application/octet-stream";
        }
        return contentTypeFor;
    }


    private static Request buildPostRequest(HttpRequestContext context) throws IOException {

        String body = context.getBody();
        Request.Builder requestBuilder = getRequestBuilder(context);

        RequestBody requestBody;
//        if (CollectionUtils.isNotEmpty(context.getUploadFiles())) {
//            requestBody = getMultipartBuilderBody(context);
//        } else
        if (StringUtils.isNotBlank(body)) {
            requestBody = getRequestBody(body);

        } else if (CollectionUtils.isNotEmpty(context.getUploadFiles())
                || CollectionUtils.isNotEmpty(context.getNameValueParams())) {
            requestBody = getMultipartBuilderBody(context);
        } else {
            requestBody = RequestBody.create(null, "");
        }
        requestBuilder.post(requestBody);

        return requestBuilder.build();
    }

    private static RequestBody getMultipartBuilderBody(HttpRequestContext context) throws IOException {
        RequestBody requestBody;
        MultipartBuilder builder = new MultipartBuilder()
                .type(MultipartBuilder.FORM);

        List<FileBody> uploadFiles = context.getUploadFiles();
        List<From> params = context.getNameValueParams();
        for (From param : params) {
            builder.addFormDataPart(param.getName(), param.getValue());
        }
        if (CollectionUtils.isNotEmpty(uploadFiles)) {
            RequestBody fileBody = null;
            for (FileBody uploadFile : uploadFiles) {

                String fileName = uploadFile.getFileName();
                fileBody = RequestBody.create(MediaType.parse(guessMimeType(fileName)), uploadFile.getFileBytes());
                //根据文件名设置contentType
                builder.addPart(Headers.of("Content-Disposition",
                        "form-data; name=\"" + uploadFile.getName() + "\"; filename=\"" + fileName + "\""),
                        fileBody);
            }
        }

        requestBody = builder.build();
        return requestBody;
    }


    private static RequestBody getRequestBody(String body) {
        RequestBody requestBody;
        //修改样式
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        //修改样式和上传json参数
        requestBody = RequestBody.create(JSON, body);
        return requestBody;
    }

    private static Request.Builder getRequestBuilder(HttpRequestContext context) {
        String url = context.getHttpUrl();
        Headers.Builder headersBuilder = getHeadersBuilder(context);
        Request.Builder requestBuilder = new Request.Builder()
                .url(url)
                .headers(headersBuilder.build());
        return requestBuilder;
    }


}
