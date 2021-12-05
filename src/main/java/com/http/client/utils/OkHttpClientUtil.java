package com.http.client.utils;


import com.http.client.bo.HttpHeader;
import com.http.client.bo.NameValueParam;
import com.http.client.bo.UploadFile;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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
                .url(context.getBaseUrl())
                .headers(builder.build())
                .build();
        Call call = mOkHttpClient.newCall(request);
        return call.execute();
    }

    private static Headers.Builder getHeadersBuilder(HttpRequestContext context) {
        Headers.Builder builder = new Headers.Builder();
        HttpHeader httpHeader = context.getHttpHeader();
        if (httpHeader != null && !httpHeader.isEmpty()) {
            for (Map.Entry<String, String> header : httpHeader.getHeaders().entrySet()) {
                builder.add(header.getKey(), header.getValue());
            }
        }
        return builder;
    }

    /**
     * 同步的Post请求
     *
     * @param url
     * @param params post的参数
     * @return
     */
    private static Response postResponse(String url, String body, List<NameValueParam> params) throws IOException {
        Request request = buildPostRequest(url, body, params);
        return mOkHttpClient.newCall(request).execute();
    }

    public static Response postResponse(HttpRequestContext context) throws IOException {
        List<UploadFile> uploadFiles = context.getUploadFiles();
        String httpUrl = context.getHttpUrl();
        List<NameValueParam> nameValueParams = context.getNameValueParams();
        if (CollectionUtils.isEmpty(uploadFiles)) {
            return post(httpUrl, context.getBody(), nameValueParams);
        }

        return post(httpUrl, uploadFiles, nameValueParams);
    }


    /**
     * 同步的Post请求
     *
     * @param url
     * @param params post的参数
     * @return 字符串
     */
    private static Response post(String url, String body, List<NameValueParam> params) throws IOException {
        return postResponse(url, body, params);
    }


    /**
     * 同步基于post的文件上传
     *
     * @param params
     * @return
     */
    private static Response post(String url, List<UploadFile> uploadFiles, List<NameValueParam> params) throws IOException {
        Request request = buildMultipartFormRequest(url, uploadFiles, params);
        return mOkHttpClient.newCall(request).execute();
    }

    public static boolean downFile(Response response, String downPath) {
        try {
            InputStream is;
            is = response.body().byteStream();
            FileOutputStream fos = null;
            fos = new FileOutputStream(downPath);
            int len;
            byte[] bytes = new byte[4096];
            while ((len = is.read(bytes)) != -1) {
                fos.write(bytes, 0, len);
            }
            fos.flush();
            is.close();
            fos.close();
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public static MultipartFile downFile(String downUrl) {
        try {

            Request request = new Request.Builder().url(downUrl).build();
            Response response = mOkHttpClient.newCall(request).execute();
            //如果是文件下载
            byte[] bytes = response.body().bytes();
            InputStream inputStream = new ByteArrayInputStream(bytes);
            //创建文件
            return new MockMultipartFile("httpClientDownFile", inputStream);
        } catch (Exception ex) {
            return null;
        }
    }


    private static Request buildMultipartFormRequest(String url, List<UploadFile> uploadFiles, List<NameValueParam> params) throws IOException {
        params = validateParam(params);

        MultipartBuilder builder = new MultipartBuilder()
                .type(MultipartBuilder.FORM);

        for (NameValueParam param : params) {
            builder.addPart(Headers.of("Content-Disposition", "form-data; name=\"" + param.getName() + "\""),
                    RequestBody.create(null, param.getValue()));
        }
        if (CollectionUtils.isNotEmpty(uploadFiles)) {
            RequestBody fileBody = null;
            for (UploadFile uploadFile : uploadFiles) {

                String fileName = uploadFile.getFile().getName();
                fileBody = RequestBody.create(MediaType.parse(guessMimeType(fileName)), uploadFile.getFile().getBytes());
                //根据文件名设置contentType
                builder.addPart(Headers.of("Content-Disposition",
                        "form-data; name=\"" + uploadFile.getName() + "\"; filename=\"" + fileName + "\""),
                        fileBody);
            }
        }

        RequestBody requestBody = builder.build();
        return new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
    }

    private static String guessMimeType(String path) {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String contentTypeFor = fileNameMap.getContentTypeFor(path);
        if (contentTypeFor == null) {
            contentTypeFor = "application/octet-stream";
        }
        return contentTypeFor;
    }


    private static List<NameValueParam> validateParam(List<NameValueParam> params) {
        if (params == null) {
            return Collections.emptyList();
        } else {
            return params;
        }
    }

    private static Request buildPostRequest(String url, String body, List<NameValueParam> params) {
        if (StringUtils.isNotBlank(body)) {
            //修改样式
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            //修改样式和上传json参数
            RequestBody requestBody = RequestBody.create(JSON, body);
            return new Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .build();
        }

        FormEncodingBuilder builder = new FormEncodingBuilder();
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(params)) {
            for (NameValueParam param : params) {
                builder.add(param.getName(), param.getValue());
            }
        }
        RequestBody requestBody = builder.build();
        return new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
    }


}
