package com.http.client.proxy;

import com.http.client.response.HttpClientResponse;
import com.http.client.bo.HttpHeader;
import com.http.client.context.HttpRequestContext;
import com.http.client.utils.OkHttpClientUtil;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 使用OkHttp的动态代理
 */
public class OkHttpProxy extends AbstractHttpProxy {
    @Override
    protected HttpClientResponse doInvoke(HttpRequestContext context) throws Throwable {

        Response response;
        if (isGet(context)) {
            response = OkHttpClientUtil.getResponse(context);
        } else {
            response = OkHttpClientUtil.postResponse(context);
        }
        assert response != null;
        return HttpClientResponse.builder()
                .charset(Optional.of(response).map(Response::body).map(ResponseBody::contentType).map(MediaType::charset).orElse(null))
                .httpHeader(getHeaders(response))
                .inputStream(response.body().byteStream())
                .code(response.code())
                .build();
    }

    public HttpHeader getHeaders(Response response){
        Map<String, List<String>> stringListMap = response.headers().toMultimap();
        HttpHeader httpHeader = new HttpHeader();
        for (Map.Entry<String, List<String>> entry : stringListMap.entrySet()) {
            httpHeader.addHeader(entry.getKey(),entry.getValue());
        }
        return httpHeader;
    }


}
