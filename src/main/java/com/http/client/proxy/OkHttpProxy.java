package com.http.client.proxy;

import com.http.client.bo.HttpClientResponse;
import com.http.client.bo.HttpHeader;
import com.http.client.context.HttpRequestContext;
import com.http.client.utils.OkHttpClientUtil;
import com.squareup.okhttp.Response;

import java.util.List;
import java.util.Map;

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
        return HttpClientResponse.builder().contentType(response.body().contentType())
                .bytes(response.body().bytes())
                .httpHeader(getHeaders(response))
                .build();
    }

    public HttpHeader getHeaders(Response response){
        Map<String, List<String>> stringListMap = response.headers().toMultimap();
        HttpHeader httpHeader = new HttpHeader();
        for (Map.Entry<String, List<String>> entry : stringListMap.entrySet()) {
            httpHeader.getHeader(entry.getKey(),entry.getValue().get(0));
        }
        return httpHeader;
    }


}
