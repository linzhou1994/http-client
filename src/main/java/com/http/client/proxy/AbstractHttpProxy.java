package com.http.client.proxy;


import com.biz.tool.spring.SpringUtil;
import com.http.client.annotation.HttpClient;
import com.http.client.bo.HttpClientRequest;
import com.http.client.config.HttpClientConfig;
import com.http.client.context.HttpRequestContext;
import com.http.client.enums.HttpRequestMethod;
import com.http.client.exception.ParamException;
import com.http.client.factorybean.HttpFactoryBean;
import com.http.client.handler.analysis.method.AnalysisMethodParamHandlerManager;
import com.http.client.handler.http.request.SetHttpParamHandlerManager;
import com.http.client.handler.http.result.HttpClientResultHandlerManager;
import com.http.client.interceptor.HttpClientInterceptor;
import com.http.client.response.BaseHttpClientResponse;
import com.http.client.utils.UrlUtil;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Objects;

/**
 * 动态代理基类
 *
 * @author linzhou
 */
@Data
public abstract class AbstractHttpProxy implements HttpProxy, InvocationHandler {

    private HttpFactoryBean httpFactoryBean;
    private List<HttpClientInterceptor> httpClientInterceptorList;
    private HttpClientConfig httpClientConfig;
    /**
     * 类型
     */
    private Class<?> type;

    private HttpClient httpClient;

    @Override
    public <T> T newProxyInstance() {
        Class<?> clazz = httpFactoryBean.getType();
        return (T) Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[]{clazz}, this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        HttpRequestContext context = new HttpRequestContext(httpFactoryBean, type, httpClient, proxy, method, args);
        return sendHttp(context);
    }

    private Object sendHttp(HttpRequestContext context) throws Throwable {
        try {
            //解析参数
            analysisMethodParam(context);
            //执行httpBefore方法
            Object rlt = runHttpBefore(context);
            //设置httpUrl
            setHttpUrl(context);
            if (Objects.nonNull(rlt)) {
                return rlt;
            }
            BaseHttpClientResponse response = doInvoke(context);
            response.setContext(context);
            rlt = HttpClientResultHandlerManager.getReturnObject(response);
            //执行httpAfter方法处理返回数据
            return runHttpAfter(response, rlt);
        } catch (Throwable throwable) {
            //执行异常拦截
            Object rlt = runHttpException(context, throwable);
            if (Objects.nonNull(rlt)) {
                return rlt;
            }
            throw throwable;
        }
    }


    /**
     * 子类实现http的实现方式,返回同一的请求结果
     *
     * @param context
     * @return
     * @throws Throwable
     */
    protected abstract BaseHttpClientResponse doInvoke(HttpRequestContext context) throws Throwable;


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


    /**
     * 解析方法参数
     *
     * @param context
     * @return
     */
    protected void analysisMethodParam(HttpRequestContext context) throws Exception {
        Object[] args = context.getArgs();
        Annotation[][] parameterAnnotations = context.getParameterAnnotations();
        HttpClientRequest result = new HttpClientRequest();

        if (args != null) {

            for (int i = 0; i < args.length; i++) {
                Object arg = args[i];
                if (arg == null) {
                    continue;
                }
                Annotation[] parameterAnnotation = parameterAnnotations[i];

                Object methodParam = AnalysisMethodParamHandlerManager.analysisMethodParam(arg, parameterAnnotation);
                SetHttpParamHandlerManager.setHttpParam(result, methodParam);
            }
        }
        context.setParam(result);
    }

    /**
     * 获取请求地址
     *
     * @return
     */
    public void setHttpUrl(HttpRequestContext context) {
        if (StringUtils.isBlank(context.getHttpUrl())) {
            if (Objects.isNull(context.getParam()) || Objects.isNull(context.getHttpRequestMethod())) {
                throw new ParamException("数据异常,methodParamResult or httpRequestMethod is null");
            }
            String baseUrl = context.getBaseUrl(getConfig().getBaseUrl());
            if (isGet(context)
                    || context.isPostEntity()) {
                context.setHttpUrl(UrlUtil.getParamUrl(baseUrl, context.getNameValueParams()));
            } else {
                context.setHttpUrl(baseUrl);
            }
        }
    }

    protected HttpClientConfig getConfig() {
        if (Objects.isNull(httpClientConfig)) {
            httpClientConfig = SpringUtil.getBean(HttpClientConfig.class);
        }
        return httpClientConfig;
    }


    /**
     * 执行httpBefore方法
     *
     * @param context
     */
    private Object runHttpBefore(HttpRequestContext context) {
        for (HttpClientInterceptor httpClientInterceptor : getHttpClientInterceptorList()) {
            Object rlt = httpClientInterceptor.httpBefore(context);
            if (Objects.nonNull(rlt)) {
                return rlt;
            }
        }
        return null;
    }

    /**
     * 执行httpAfter方法
     *
     * @param response
     */
    private Object runHttpAfter(BaseHttpClientResponse response, Object rlt) throws Exception {
        for (HttpClientInterceptor httpClientInterceptor : getHttpClientInterceptorList()) {
            rlt = httpClientInterceptor.httpAfter(response, rlt);
        }
        return rlt;
    }

    /**
     * 执行httpAfter方法
     *
     * @param context
     * @param e
     */
    private Object runHttpException(HttpRequestContext context, Throwable e) throws Exception {
        Object rlt = null;
        for (HttpClientInterceptor httpClientInterceptor : getHttpClientInterceptorList()) {
            rlt = httpClientInterceptor.httpException(context, e);
        }
        return rlt;
    }

    protected List<HttpClientInterceptor> getHttpClientInterceptorList() {
        if (httpClientInterceptorList == null) {
            synchronized (AbstractHttpProxy.class) {
                if (httpClientInterceptorList == null) {
                    httpClientInterceptorList = SpringUtil.getBeanList(HttpClientInterceptor.class);
                }
            }
        }
        return httpClientInterceptorList;
    }

    public void setHttpFactoryBean(HttpFactoryBean httpFactoryBean) {
        this.httpFactoryBean = httpFactoryBean;
        this.type = httpFactoryBean.getType();
        this.httpClient = type.getAnnotation(HttpClient.class);
    }
}
