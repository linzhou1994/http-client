package com.http.client.proxy;


import com.http.client.annotation.HttpClient;
import com.http.client.bo.HttpClientRequest;
import com.http.client.context.HttpRequestContext;
import com.http.client.enums.HttpRequestMethod;
import com.http.client.factorybean.HttpFactoryBean;
import com.http.client.handler.analysis.method.AnalysisMethodParamHandlerManager;
import com.http.client.handler.analysis.url.AnalysisUrlHandlerManager;
import com.http.client.handler.http.request.SetHttpParamHandlerManager;
import com.http.client.handler.http.result.HttpClientResultHandlerManager;
import com.http.client.interceptor.HttpClientInterceptorManager;
import com.http.client.response.BaseHttpClientResponse;
import lombok.Data;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Objects;

/**
 * 动态代理基类
 *
 * @author linzhou
 */
@Data
public abstract class AbstractHttpProxy implements HttpProxy, InvocationHandler {

    private HttpFactoryBean httpFactoryBean;

    /**
     * 类型
     */
    private Class<?> type;

    private HttpClient interfaceHttpClient;

    @Override
    public <T> T newProxyInstance() {
        Class<?> clazz = httpFactoryBean.getType();
        return (T) Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[]{clazz}, this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        HttpRequestContext context = new HttpRequestContext(httpFactoryBean, type, interfaceHttpClient, proxy, method, args);
        return sendHttp(context);
    }

    private Object sendHttp(HttpRequestContext context) throws Throwable {
        BaseHttpClientResponse response = null;
        try {
            //解析参数
            analysisMethodParam(context);
            //执行httpBefore方法
            Object rlt = HttpClientInterceptorManager.runHttpBefore(context);
            //设置httpUrl
            setHttpUrl(context);
            if (Objects.nonNull(rlt)) {
                return rlt;
            }
            response = doInvoke(context);
            response.setContext(context);
            rlt = HttpClientInterceptorManager.runHttpAfter(response);
            if (Objects.nonNull(rlt)) {
                return rlt;
            }
            rlt = HttpClientResultHandlerManager.getReturnObject(response);
            //执行httpAfter方法处理返回数据
            return HttpClientInterceptorManager.returnObjectAfter(response, rlt);
        } catch (Throwable throwable) {
            //执行异常拦截
            Object rlt = HttpClientInterceptorManager.runHttpException(context,response, throwable);
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
    public void setHttpUrl(HttpRequestContext context) throws Exception {
        String url = AnalysisUrlHandlerManager.analysisUrl(context);
        context.setHttpUrl(url);
    }



    public void setHttpFactoryBean(HttpFactoryBean httpFactoryBean) {
        this.httpFactoryBean = httpFactoryBean;
        this.type = httpFactoryBean.getType();
        this.interfaceHttpClient = type.getAnnotation(HttpClient.class);
    }
}
