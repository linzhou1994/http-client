package com.http.client.proxy;

import com.http.client.context.HttpRequestContext;

/**
 * @author linzhou
 */
public class DefaultProxy2 extends AbstractHttpProxy{
    @Override
    public Object doInvoke(HttpRequestContext context) throws Throwable {
        String httpUrl = context.getHttpUrl();
        return "DefaultProxy2"+ httpUrl;
    }
}
