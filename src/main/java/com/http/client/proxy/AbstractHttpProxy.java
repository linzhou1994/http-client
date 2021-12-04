package com.http.client.proxy;


import com.alibaba.fastjson.JSON;
import com.http.client.annotation.HttpFile;
import com.http.client.annotation.HttpParam;
import com.http.client.bo.*;
import com.http.client.context.HttpRequestContext;
import com.http.client.exception.ParamException;
import com.http.client.factorybean.HttpFactoryBean;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.math.BigDecimal;

/**
 * 动态代理基类
 * @author linzhou
 */
@Data
public abstract class AbstractHttpProxy implements HttpProxy, InvocationHandler {

    private HttpFactoryBean httpFactoryBean;

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
        return doInvoke(context);
    }

    public abstract Object doInvoke(HttpRequestContext context) throws Throwable;




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


    public <T extends Annotation> T findHttpAnnotation(Annotation[] annotations, Class<T> clazz) {
        for (Annotation annotation : annotations) {
            if (clazz.isInstance(annotation)) {
                return (T) annotation;
            }
        }
        return null;
    }
}
