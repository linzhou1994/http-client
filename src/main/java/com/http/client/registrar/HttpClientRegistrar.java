package com.http.client.registrar;


import com.http.client.annotation.EnableHttpClient;
import com.http.client.annotation.HttpClient;
import com.http.client.annotation.HttpClientProxy;
import com.http.client.factorybean.HttpFactoryBean;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * httpClient 动态注册类
 * @author linzhou
 */
public class HttpClientRegistrar implements ImportBeanDefinitionRegistrar, ResourceLoaderAware, EnvironmentAware {

    private ResourceLoader resourceLoader;

    private Environment environment;


    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        ClassPathScanningCandidateComponentProvider scanner = getScanner();
        scanner.setResourceLoader(this.resourceLoader);
        AnnotationTypeFilter annotationTypeFilter = new AnnotationTypeFilter(HttpClient.class);
        scanner.addIncludeFilter(annotationTypeFilter);

        Set<String> basePackages = getBasePackages(importingClassMetadata);

        for (String basePackage : basePackages) {
            Set<BeanDefinition> candidateComponents = scanner
                    .findCandidateComponents(basePackage);
            for (BeanDefinition candidateComponent : candidateComponents) {
                if (candidateComponent instanceof AnnotatedBeanDefinition) {
                    AnnotatedBeanDefinition beanDefinition = (AnnotatedBeanDefinition) candidateComponent;
                    AnnotationMetadata annotationMetadata = beanDefinition.getMetadata();
                    Assert.isTrue(annotationMetadata.isInterface(), "@HttpClient 只能作用在接口类型上");

                    register(registry, annotationMetadata);
                }
            }
        }

    }

    protected ClassPathScanningCandidateComponentProvider getScanner() {
        return new ClassPathScanningCandidateComponentProvider(false, this.environment) {
            @Override
            protected boolean isCandidateComponent(
                    AnnotatedBeanDefinition beanDefinition) {
                boolean isCandidate = false;
                if (beanDefinition.getMetadata().isIndependent()) {
                    if (!beanDefinition.getMetadata().isAnnotation()) {
                        isCandidate = true;
                    }
                }
                return isCandidate;
            }
        };
    }

    protected Set<String> getBasePackages(AnnotationMetadata importingClassMetadata) {

        Map<String, Object> attributes = importingClassMetadata
                .getAnnotationAttributes(EnableHttpClient.class.getCanonicalName());

        Set<String> basePackages = new HashSet<>();
        if (attributes != null) {
            for (String pkg : (String[]) attributes.get("basePackages")) {
                if (StringUtils.isNotBlank(pkg)) {
                    basePackages.add(pkg);
                }
            }
        }

        if (basePackages.isEmpty()) {
            basePackages.add(
                    ClassUtils.getPackageName(importingClassMetadata.getClassName()));
        }
        return basePackages;
    }

    private void register(BeanDefinitionRegistry registry,
                          AnnotationMetadata annotationMetadata) {

        Map<String, Object> httpClientAttributes = annotationMetadata
                .getAnnotationAttributes(HttpClient.class.getCanonicalName());
        Map<String, Object> httpClientProxyAttributes = annotationMetadata
                .getAnnotationAttributes(HttpClientProxy.class.getCanonicalName());


        String className = annotationMetadata.getClassName();
        BeanDefinitionBuilder definition = BeanDefinitionBuilder
                .genericBeanDefinition(HttpFactoryBean.class);
        definition.addPropertyValue("url", httpClientAttributes.get("url"));
        definition.addPropertyValue("method", httpClientAttributes.get("method"));
        definition.addPropertyValue("pathMethodName", httpClientAttributes.get("pathMethodName"));
        definition.addPropertyValue("type", className);
        if (Objects.nonNull(httpClientProxyAttributes)) {
            definition.addPropertyValue("proxyClass", httpClientProxyAttributes.get("value"));
        }
        definition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);

        AbstractBeanDefinition beanDefinition = definition.getBeanDefinition();

        beanDefinition.setPrimary(true);

        BeanDefinitionHolder holder = new BeanDefinitionHolder(beanDefinition, className,
                new String[]{});
        BeanDefinitionReaderUtils.registerBeanDefinition(holder, registry);
    }


}
