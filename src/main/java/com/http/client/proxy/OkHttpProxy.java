package com.http.client.proxy;

import com.alibaba.fastjson.JSON;
import com.http.client.context.HttpRequestContext;
import com.http.client.utils.OkHttpClientUtil;
import com.squareup.okhttp.Response;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;

/**
 * 使用OkHttp的动态代理
 */
public class OkHttpProxy extends AbstractHttpProxy {
    @Override
    protected Object doInvoke(HttpRequestContext context) throws Throwable {

        Response response;
        if (isGet(context)) {
            response = OkHttpClientUtil.getResponse(context);
        } else {
            response = OkHttpClientUtil.postResponse(context);
        }

        return getReturnObject(response, context.getMethod().getReturnType());
    }


    private Object getReturnObject(Response response, Class<?> returnType) throws IOException {

        if (returnType == MultipartFile.class) {
            //如果是文件下载
            byte[] bytes = response.body().bytes();
            InputStream inputStream = new ByteArrayInputStream(bytes);
            //创建文件
            return new MockMultipartFile("httpClientDownFile", inputStream);
        }

        String result = response.body().string();
        if (returnType == String.class) {
            return result;
        }
        if (returnType == Integer.class) {
            return Integer.parseInt(result);
        }
        if (returnType == Double.class) {
            return Double.parseDouble(result);
        }
        if (returnType == Float.class) {
            return Float.parseFloat(result);
        }
        if (returnType == Long.class) {
            return Long.parseLong(result);
        }
        if (returnType == BigDecimal.class) {
            return new BigDecimal(result);
        }

        return JSON.parseObject(result, returnType);
    }
}
