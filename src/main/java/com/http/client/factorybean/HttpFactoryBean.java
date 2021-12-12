package com.http.client.factorybean;

import com.http.client.config.HttpClientConfig;
import com.http.client.enums.HttpRequestMethod;
import com.http.client.exception.ParamException;
import com.http.client.proxy.AbstractHttpProxy;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Objects;

/**
 * HTTPClient工厂bean
 *
 * @author linzhou
 */
public class HttpFactoryBean implements FactoryBean<Object>, InitializingBean, ApplicationContextAware {
    private ApplicationContext applicationContext;
    /**
     * 类型
     */
    private Class<?> type;
    /**
     * 请求地址
     */
    private String url;
    /**
     * 类上的请求跟路径
     */
    private String basePath;
    /**
     * 是否使用方法名称作为路径
     */
    private boolean pathMethodName;
    /**
     * 请求类型
     */
    private HttpRequestMethod method;

    public Class<? extends AbstractHttpProxy> proxyClass;


    @Override
    public Object getObject() throws Exception {
        if (Objects.isNull(proxyClass)) {
            throw new ParamException("未找到动态代理类");
        }
        AbstractHttpProxy proxy = proxyClass.newInstance();
        proxy.setHttpFactoryBean(this);
        return proxy.newProxyInstance();
    }

    @Override
    public Class<?> getObjectType() {
        return type;
    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        this.applicationContext = context;

    }


    public Class<?> getType() {
        return type;
    }

    public void setType(Class<?> type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public HttpRequestMethod getMethod() {
        return method;
    }

    public void setMethod(HttpRequestMethod method) {
        this.method = method;
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public boolean isPathMethodName() {
        return pathMethodName;
    }

    public void setPathMethodName(boolean pathMethodName) {
        this.pathMethodName = pathMethodName;
    }


    public Class<? extends AbstractHttpProxy> getProxyClass() {
        return proxyClass;
    }

    public void setProxyClass(Class<? extends AbstractHttpProxy> proxyClass) {
        this.proxyClass = proxyClass;
    }

    public String getBasePath() {
        return basePath;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }
}