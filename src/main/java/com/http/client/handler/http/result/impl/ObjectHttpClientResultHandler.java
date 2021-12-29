package com.http.client.handler.http.result.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.http.client.annotation.JsonToObject;
import com.http.client.handler.http.result.HttpClientResultHandler;
import com.http.client.response.HttpClientResponse;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author linzhou
 * @ClassName ObjectResultHandler.java
 * @createTime 2021年12月27日 12:02:00
 * @Description
 */
@Component
@Order
public class ObjectHttpClientResultHandler implements HttpClientResultHandler {
    private static final Logger logger = LoggerFactory.getLogger(ObjectHttpClientResultHandler.class);

    @Override
    public Object getReturnObject(HttpClientResponse response, Class<?> returnType) throws Exception {
        String result = response.result();
        logger.info("url:{},result:{}", response.getHttpUrl(), result);
        JSONObject jsonObject = JSON.parseObject(result);
        toObject(jsonObject, returnType);
        Type genericReturnType = response.getContext().getMethod().getGenericReturnType();
        return jsonObject.toJavaObject(genericReturnType);
    }

    private void toObject(JSONObject jsonObject, Class<?> returnType) {
        List<String> fieldNames = getFieldNames(returnType);
        if (CollectionUtils.isEmpty(fieldNames)) {
            return;
        }
        for (String fieldName : fieldNames) {
            Object object = jsonObject.get(fieldName);
            if (object instanceof String) {
                tryToObject(jsonObject, fieldName, (String) object);
            }
        }
    }

    private void tryToObject(JSONObject jsonObject, String fieldName, String s) {
        try {
            jsonObject.put(fieldName, JSON.parse(s));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<String> getFieldNames(Class<?> returnType) {
        List<String> rlt = new ArrayList<>();
        for (Field declaredField : returnType.getDeclaredFields()) {
            if (declaredField.isAnnotationPresent(JsonToObject.class)) {
                JSONField annotation = declaredField.getAnnotation(JSONField.class);
                String name = Objects.isNull(annotation) ? declaredField.getName() : annotation.name();
                rlt.add(name);
            }
        }
        return rlt;
    }
}
