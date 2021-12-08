package com.http.client.proxy;


import com.alibaba.fastjson.JSON;
import com.http.client.annotation.HttpFile;
import com.http.client.annotation.HttpParam;
import com.http.client.bo.FileParam;
import com.http.client.bo.HttpClientResponse;
import com.http.client.bo.HttpHeader;
import com.http.client.bo.HttpUrl;
import com.http.client.bo.MethodParamResult;
import com.http.client.bo.NameValueParam;
import com.http.client.bo.UploadFile;
import com.http.client.config.HttpClientConfig;
import com.http.client.context.HttpRequestContext;
import com.http.client.enums.HttpRequestMethod;
import com.http.client.exception.ParamException;
import com.http.client.factorybean.HttpFactoryBean;
import com.http.client.handler.HttpClientHandler;
import com.http.client.utils.FileUtil;
import com.http.client.utils.UrlUtil;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 动态代理基类
 *
 * @author linzhou
 */
@Data
public abstract class AbstractHttpProxy implements HttpProxy, InvocationHandler {

    private HttpFactoryBean httpFactoryBean;
    private HttpClientConfig config;
    private ApplicationContext applicationContext;
    private List<HttpClientHandler> httpClientHandlerList;

    @Override
    public <T> T newProxyInstance() {
        Class<?> clazz = httpFactoryBean.getType();
        return (T) Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[]{clazz}, this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        HttpRequestContext context = new HttpRequestContext(httpFactoryBean, proxy, method, args);
        //解析参数
        analysisMethodParam(context);
        //设置httpUrl
        setHttpUrl(context);
        //执行httpBefore方法
        runHttpBefore(context);
        HttpClientResponse response = doInvoke(context);
        response.setContext(context);
        //执行httpAfter方法处理返回数据
        runHttpAfter(context, response);
        return getReturnObject(context,response);
    }

    private Object getReturnObject(HttpRequestContext context, HttpClientResponse response) throws IOException {
        Class<?> returnType = context.getMethod().getReturnType();

        if (returnType == MultipartFile.class) {
            return FileUtil.getMockMultipartFile(response);
        }
        if (returnType == File.class){
            return FileUtil.downFile(response);
        }

        String result = response.string();
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


    protected abstract HttpClientResponse doInvoke(HttpRequestContext context) throws Throwable;


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
    protected void analysisMethodParam(HttpRequestContext context) {
        Object[] args = context.getArgs();
        Annotation[][] parameterAnnotations = context.getParameterAnnotations();
        MethodParamResult result = new MethodParamResult();

        HttpHeader httpHeader = new HttpHeader();
        if (args != null) {

            for (int i = 0; i < args.length; i++) {
                Object arg = args[i];
                if (arg == null) {
                    continue;
                }
                Annotation[] parameterAnnotation = parameterAnnotations[i];
                HttpParam httpParam = findHttpAnnotation(parameterAnnotation, HttpParam.class);
                HttpFile httpFile = findHttpAnnotation(parameterAnnotation, HttpFile.class);
                if (httpParam != null) {
                    //处理表单参数
                    NameValueParam nameValueParam = getNameValueParam(i, arg, httpParam);
                    result.addNameValueParam(nameValueParam);
                } else if (httpFile != null) {
                    //处理文件上传
                    result.setUploadFile(getUploadFile(i, arg, httpFile));
                } else if (arg instanceof HttpHeader) {
                    //设置请求头
                    httpHeader.addHeader((HttpHeader) arg);
                } else if (arg instanceof HttpUrl) {
                    //处理自定义url
                    result.setHttpUrl((HttpUrl) arg);
                } else {
                    //处理body
                    result.setBody(getBody(arg));
                }
            }
        }

        result.setHttpHeader(httpHeader);
        context.setParam(result);
    }

    /**
     * 处理body
     *
     * @param arg
     * @return
     */
    private String getBody(Object arg) {
        String body;
        if (arg instanceof String) {
            body = (String) arg;
        } else {
            body = JSON.toJSONString(arg);
        }
        return body;
    }

    /**
     * 处理文件上传
     *
     * @param i
     * @param arg
     * @param httpFile
     * @return
     */
    private UploadFile getUploadFile(int i, Object arg, HttpFile httpFile) {
        UploadFile uploadFile;
        //保存需要上传的文件信息
        if (arg instanceof MultipartFile) {
            uploadFile = new UploadFile(httpFile, (MultipartFile) arg);
        } else if (arg instanceof FileParam) {
            FileParam fileParam = (FileParam) arg;
            uploadFile = new UploadFile(httpFile, fileParam.getFile(), fileParam.getParam());
        } else {
            throw new ParamException("第" + i + "个参数格式错误,上传文件应为MultipartFile类型,当前类型:" + arg.getClass().getName());
        }
        return uploadFile;
    }

    /**
     * 处理表单
     */
    private NameValueParam getNameValueParam(int i, Object arg, HttpParam httpParam) {
        //如果是表单参数,则当做表单处理
        String name = httpParam.value();
        if (StringUtils.isBlank(name)) {
            throw new ParamException("第" + i + "个参数格式错误,没有发现表单参数对应的名称");
        }
        String value;
        if (arg == null) {
            value = null;
        } else if (isNameValuePair(arg)) {
            value = arg.toString();
        } else {
            value = JSON.toJSONString(arg);
        }
        return new NameValueParam(httpParam, value);
    }

    /**
     * 是否是基础类型
     *
     * @param o
     * @return
     */
    protected boolean isNameValuePair(Object o) {
        if (o instanceof Integer
                || o instanceof String
                || o instanceof Double
                || o instanceof Float
                || o instanceof Long
                || o instanceof BigDecimal) {
            return true;
        }
        return false;
    }


    /**
     * 查找指定注解
     *
     * @param annotations
     * @param clazz       要查找的注解类型
     * @param <T>
     * @return
     */
    public <T extends Annotation> T findHttpAnnotation(Annotation[] annotations, Class<T> clazz) {
        for (Annotation annotation : annotations) {
            if (clazz.isInstance(annotation)) {
                return (T) annotation;
            }
        }
        return null;
    }


    /**
     * 获取请求地址
     *
     * @return
     */
    public String setHttpUrl(HttpRequestContext context) {
        if (StringUtils.isBlank(context.getHttpUrl())) {
            if (Objects.isNull(context.getParam()) || Objects.isNull(context.getHttpRequestMethod())) {
                throw new ParamException("数据异常,methodParamResult or httpRequestMethod is null");
            }
            String baseUrl = context.getBaseUrl(config.getBaseUrl());
            if (isGet(context)
                    || context.isPostEntity()) {
                context.setHttpUrl(UrlUtil.getParamUrl(baseUrl, context.getNameValueParams()));
            } else {
                context.setHttpUrl(baseUrl);
            }
        }

        return context.getHttpUrl();
    }


    /**
     * 执行httpBefore方法
     *
     * @param context
     */
    private void runHttpBefore(HttpRequestContext context) {
        for (HttpClientHandler httpClientHandler : getHttpClientHandlerList()) {
            httpClientHandler.httpBefore(context);
        }
    }

    /**
     * 执行httpAfter方法
     *
     * @param context
     */
    private void runHttpAfter(HttpRequestContext context, HttpClientResponse response) throws Exception {
        for (HttpClientHandler httpClientHandler : getHttpClientHandlerList()) {
           httpClientHandler.httpAfter(context, response);
        }
    }

    protected List<HttpClientHandler> getHttpClientHandlerList() {
        if (httpClientHandlerList == null) {
            synchronized (AbstractHttpProxy.class) {
                if (httpClientHandlerList == null) {
                    httpClientHandlerList = new ArrayList<>();
                    Map<String, HttpClientHandler> httpClientHandlerMap = getHttpClientHandlerMap();
                    httpClientHandlerList.addAll(httpClientHandlerMap.values());
                    httpClientHandlerList.sort(Comparator.comparingInt(AbstractHttpProxy::getOrder));
                }
            }
        }
        return httpClientHandlerList;
    }

    protected Map<String, HttpClientHandler> getHttpClientHandlerMap() {
        return applicationContext.getBeansOfType(HttpClientHandler.class);
    }

    public static int getOrder(Object o) {
        Order order = AnnotationUtils.findAnnotation(o.getClass(), Order.class);
        if (order != null) {
            return order.value();
        }
        return 0;
    }
}
