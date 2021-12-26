package com.http.client.context;


import com.http.client.annotation.HttpClient;
import com.http.client.context.body.FileBody;
import com.http.client.context.form.Form;
import com.http.client.context.header.HttpHeader;
import com.http.client.bo.HttpClientRequest;
import com.http.client.enums.HttpRequestMethod;
import com.http.client.factorybean.HttpFactoryBean;
import com.http.client.utils.UrlUtil;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * 请求上下文
 *
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
    private HttpClientRequest param;
    /**
     * http请求地址
     */
    private String httpUrl;

    /**
     * 类型
     */
    private Class<?> type;
    /**
     * 接口上的注解
     */
    private HttpClient interfaceHttpClient;
    /**
     * 方法上的注解对象
     */
    private HttpClient methodHttpClient;

    public HttpRequestContext(HttpFactoryBean httpFactoryBean, Class<?> type, HttpClient interfaceHttpClient, Object proxy, Method method, Object[] args) {
        this.method = method;
        this.httpFactoryBean = httpFactoryBean;
        this.proxy = proxy;
        this.args = args;
        this.type = type;
        this.interfaceHttpClient = interfaceHttpClient;
        //方法的注解
        this.methodHttpClient = method.getAnnotation(HttpClient.class);
        //参数的注解
        this.parameterAnnotations = method.getParameterAnnotations();
        this.httpRequestMethod = getHttpRequestMethod();

    }


    public String getBaseUrl(String defaultBaseUrl) {
        if (Objects.nonNull(param.getHttpUrl())) {
            return param.getHttpUrl().getUrl();
        }

        String url = Objects.isNull(methodHttpClient) ? null : methodHttpClient.url();
        if (StringUtils.isBlank(url)) {
            url = interfaceHttpClient.url();
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


    public boolean isPostEntity() {
        if (httpRequestMethod != HttpRequestMethod.POST) {
            return false;
        }

        return StringUtils.isNotBlank(param.getBody());
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
        HttpRequestMethod httpRequestMethod = Objects.nonNull(methodHttpClient) ? methodHttpClient.method() : null;
        if (Objects.isNull(httpRequestMethod) || Objects.equals(httpRequestMethod, HttpRequestMethod.NULL)) {
            //如果方法注解中没有获取到请求类型则从类注解中获取请求类型
            httpRequestMethod = interfaceHttpClient.method();
        }
        if (Objects.equals(httpRequestMethod, HttpRequestMethod.NULL)) {
            //如果方法注解和类注解中都没有标注请求类型,则默认get
            httpRequestMethod = HttpRequestMethod.GET;
        }
        return httpRequestMethod;
    }


    public List<Form> getNameValueParams() {

        if (Objects.nonNull(param)) {
            return param.getNameValueParams();
        }
        return Collections.emptyList();
    }

    public List<FileBody> getUploadFiles() {
        if (Objects.nonNull(param)) {
            return param.getUploadFiles();
        }
        return Collections.emptyList();
    }

    public HttpHeader getHttpHeader() {
        if (Objects.nonNull(param)) {
            return param.getHttpHeader();
        }
        return null;
    }

    public String getBody() {
        if (Objects.nonNull(param)) {
            return param.getBody();
        }
        return null;
    }
}
