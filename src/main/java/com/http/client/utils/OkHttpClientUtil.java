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


    public static File downFile(Response response) {
        return downFile(response, "httpClient/");
    }

    public static File downFile(Response response, String downPath) {
        String fileName = getFilePath(response, downPath);
        try {
            InputStream is;
            is = response.body().byteStream();
            FileOutputStream fos = null;

            fos = new FileOutputStream(fileName);
            int len;
            byte[] bytes = new byte[4096];
            while ((len = is.read(bytes)) != -1) {
                fos.write(bytes, 0, len);
            }
            fos.flush();
            is.close();
            fos.close();
        } catch (Exception ex) {
            return null;
        }
        return new File(fileName);
    }

    /**
     * 获取文件
     *
     * @param response
     * @return
     * @throws IOException
     */
    public static MockMultipartFile getMockMultipartFile(Response response) throws IOException {
        //如果是文件下载
        byte[] bytes = response.body().bytes();
        InputStream inputStream = new ByteArrayInputStream(bytes);
        //创建文件
        return new MockMultipartFile(getFileName(response), inputStream);
    }

    private static String getFilePath(Response response, String path) {
        if (StringUtils.isBlank(path)) {
            return getFileName(response);
        }
        StringBuilder stringBuilder = new StringBuilder(path);
        if (path.lastIndexOf("/") != path.length()) {
            stringBuilder.append("/");
        }
        return stringBuilder.append(getFileName(response)).toString();
    }

    /**
     * 获取文件名称
     */
    private static String getFileName(Response response) {
        //从header中获取文件名称
        return Optional.ofNullable(getHeaderFileName(response))
                //如果header中没有文件名称,则从url上获取
                .orElse(getUrlFileName(response));
    }

    /**
     * 解析文件头
     * Content-Disposition:attachment;filename=FileName.txt
     * Content-Disposition: attachment; filename*="UTF-8''%E6%9B%BF%E6%8D%A2%E5%AE%9E%E9%AA%8C%E6%8A%A5%E5%91%8A.pdf"
     */
    private static String getHeaderFileName(Response response) {
        String dispositionHeader = response.header("Content-Disposition");
        if (StringUtils.isNotBlank(dispositionHeader)) {
            dispositionHeader.replace("attachment;filename=", "");
            dispositionHeader.replace("filename*=utf-8", "");
            String[] strings = dispositionHeader.split("; ");
            if (strings.length > 1) {
                dispositionHeader = strings[1].replace("filename=", "");
                dispositionHeader = dispositionHeader.replace("\"", "");
                return dispositionHeader;
            }
        }
        return null;
    }

    /**
     * 通过url获取文件名称
     *
     * @param response
     * @return
     */
    public static String getUrlFileName(Response response) {
        return Optional.ofNullable(response)
                .map(Response::request)
                .map(Request::url)
                .map(URL::toString)
                .map(o -> o.substring(o.lastIndexOf("/") + 1))
                .orElse("HttpClientDownFile");

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

    private static RequestBody GetFormEncodingBody(HttpRequestContext context) {
        List<From> params = context.getNameValueParams();
        if (CollectionUtils.isNotEmpty(params)) {
            FormEncodingBuilder builder = new FormEncodingBuilder();
            for (From param : params) {
                builder.add(param.getName(), param.getValue());
            }
            return builder.build();
        }

        return null;
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
