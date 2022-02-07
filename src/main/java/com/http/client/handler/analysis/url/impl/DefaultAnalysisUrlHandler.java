package com.http.client.handler.analysis.url.impl;

import com.biz.tool.spring.SpringUtil;
import com.http.client.annotation.HttpClient;
import com.http.client.config.HttpClientConfig;
import com.http.client.context.HttpRequestContext;
import com.http.client.enums.HttpRequestMethod;
import com.http.client.exception.ParamException;
import com.http.client.handler.analysis.url.AnalysisUrlHandler;
import com.http.client.utils.UrlUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.Objects;

/**
 * @author linzhou
 * @ClassName DefaultAnalysisUrlHandler.java
 * @createTime 2022年02月07日 12:07:00
 * @Description
 */
@Component
public class DefaultAnalysisUrlHandler implements AnalysisUrlHandler {

    @Autowired
    private HttpClientConfig httpClientConfig;

    @Override
    public AnalysisUrlResult analysisUrl(HttpRequestContext context, String url) throws Exception {
        return new AnalysisUrlResult(getHttpUrl(context));
    }

    /**
     * 获取请求地址
     *
     * @return
     */
    public String getHttpUrl(HttpRequestContext context) {
        if (StringUtils.isBlank(context.getHttpUrl())) {
            if (Objects.isNull(context.getParam()) || Objects.isNull(context.getHttpRequestMethod())) {
                throw new ParamException("数据异常,methodParamResult or httpRequestMethod is null");
            }
            String baseUrl = getUrl(context,httpClientConfig.getBaseUrl());
            if (isGet(context)
                    || context.isPostEntity()) {
               return UrlUtil.getParamUrl(baseUrl, context.getNameValueParams());
            } else {
                return baseUrl;
            }
        }
        return null;
    }


    /**
     * 是否是get方法
     *
     * @param context
     * @return
     */
    protected boolean isGet(HttpRequestContext context) {
        HttpRequestMethod httpRequestMethod = context.getHttpRequestMethod();
        return HttpRequestMethod.POST != httpRequestMethod;
    }

    public String getUrl(HttpRequestContext context, String defaultBaseUrl) {
        if (Objects.nonNull(context.getParam().getHttpUrl())) {
            return context.getParam().getHttpUrl().getUrl();
        }
        HttpClient interfaceHttpClient = context.getInterfaceHttpClient();
        HttpClient methodHttpClient = context.getMethodHttpClient();
        Method method = context.getMethod();

        String url = getBasePath(context,defaultBaseUrl);

        //拼接类上的根路径
        String basePath = interfaceHttpClient.path();
        if (StringUtils.isNotBlank(basePath)) {
            url = UrlUtil.splicingUrl(url, basePath);
        }
        //如果方法没有注解,则取方法名称作为路径
        String path = "";


        if (Objects.nonNull(methodHttpClient)) {
            path = methodHttpClient.path();
            if (StringUtils.isBlank(path) && methodHttpClient.pathMethodName()) {
                //如果path为空,但是注解标注使用方法名,则path使用方法名称
                path = method.getName();
            }
        } else if (interfaceHttpClient.pathMethodName()) {
            //如果类注解上标注了使用方法名称
            path = method.getName();
        }
        return UrlUtil.splicingUrl(url, path);
    }

    private String getBasePath(HttpRequestContext context, String defaultBaseUrl) {
        HttpClient interfaceHttpClient = context.getInterfaceHttpClient();
        HttpClient methodHttpClient = context.getMethodHttpClient();
        String url = Objects.isNull(methodHttpClient) ? null : UrlUtil.getUrl(methodHttpClient);
        if (StringUtils.isBlank(url)) {
            url = UrlUtil.getUrl(interfaceHttpClient);
        }
        if (StringUtils.isBlank(url)) {
            if (StringUtils.isNotBlank(defaultBaseUrl)) {
                url = defaultBaseUrl;
            } else {
                throw new IllegalArgumentException("url:error,url is blank");
            }
        }
        if (!url.contains("://")) {
            url = "https://" + url;
        }
        return url;
    }
}
