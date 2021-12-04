package com.http.client.context;


import com.http.client.annotation.HttpClient;
import com.http.client.annotation.HttpFile;
import com.http.client.bo.MethodParamResult;
import com.http.client.enums.HttpRequestMethod;
import com.http.client.exception.ParamException;
import com.http.client.factorybean.HttpFactoryBean;
import com.http.client.utils.UrlUtil;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * 请求上下文
 * @author linzhou
 */
@Data
public class HttpRequestContext {
    private Object proxy;
    /**
     * 执行方法对象
     */
    private Method method;
    /**
     * 方法上的注解对象
     */
    private HttpClient methodAnnotation;
    /**
     * 方法参数的上的注解对象集合
     */
    private Annotation[][] parameterAnnotations;
    private HttpFactoryBean httpFactoryBean;
    /**
     * 参数
     */
    private Object[] args;
    /**
     * 请求类型
     */
    private HttpRequestMethod httpRequestMethod;
    /**
     * http请求参数
     */
    private MethodParamResult param;
    /**
     * http请求地址
     */
    private String httpUrl;

    public HttpRequestContext(HttpFactoryBean httpFactoryBean, Object proxy, Method method, Object[] args) {
        this.method = method;
        this.httpFactoryBean = httpFactoryBean;
        this.args = args;
        //方法的注解
        this.methodAnnotation = method.getAnnotation(HttpClient.class);
        //参数的注解
        this.parameterAnnotations = method.getParameterAnnotations();
        this.httpRequestMethod = getHttpRequestMethod();
    }

    /**
     * 获取请求地址
     *
     * @return
     */
    public String getHttpUrl() {
        if (StringUtils.isBlank(httpUrl)) {
            if (Objects.isNull(param) || Objects.isNull(httpRequestMethod)) {
                throw new ParamException("数据异常,methodParamResult or httpRequestMethod is null");
            }
            String baseUrl = getBaseUrl();
            if (httpRequestMethod == HttpRequestMethod.GET
                    || httpRequestMethod == HttpRequestMethod.NULL
                    || isPostEntity()) {
                httpUrl = UrlUtil.getParamUrl(baseUrl, param.getNameValueParams());
            } else {
                httpUrl = baseUrl;
            }
        }

        return httpUrl;
    }

    private String getBaseUrl() {
        if (param.getHttpUrl() != null) {
            return param.getHttpUrl().getUrl();
        }

        String url = methodAnnotation == null ? null : methodAnnotation.url();
        if (StringUtils.isBlank(url)) {
            url = httpFactoryBean.getUrl();
        }
        if (StringUtils.isBlank(url)) {
            throw new IllegalArgumentException("url:error,url is blank");
        }
        if (!url.contains("://")) {
            url = "https://" + url;
        }
        //如果方法没有注解,则取方法名称作为路径
        String path = "";

        if (methodAnnotation != null) {
            path = methodAnnotation.path();
            if (StringUtils.isBlank(path) && methodAnnotation.pathMethodName()) {
                //如果path为空,但是注解标注使用方法名,则path使用方法名称
                path = method.getName();
            }
        } else if (httpFactoryBean.isPathMethodName()) {
            //如果类注解上标注了使用方法名称
            path = method.getName();
        }
        return UrlUtil.splicingUrl(url, path);
    }


    public boolean isPostEntity() {
        if (httpRequestMethod != HttpRequestMethod.POST) {
            return false;
        }

        return StringUtils.isNotBlank(param.getBody()) || Objects.nonNull(param.getUploadFile());
    }

    /**
     * 获取请求方式
     *
     * @return
     */
    public HttpRequestMethod getHttpRequestMethod() {
        if (httpRequestMethod != null) {
            return httpRequestMethod;
        }
        //从方法注解中获取请求类型
        HttpRequestMethod httpRequestMethod = methodAnnotation != null ? methodAnnotation.method() : null;
        if (httpRequestMethod == null || httpRequestMethod == HttpRequestMethod.NULL) {
            //如果方法注解中没有获取到请求类型则从类注解中获取请求类型
            httpRequestMethod = httpFactoryBean.getMethod();
        }
        if (httpRequestMethod == HttpRequestMethod.NULL) {
            //如果方法注解和类注解中都没有标注请求类型,则默认get
            httpRequestMethod = HttpRequestMethod.GET;
        }
        return httpRequestMethod;
    }


}
