package com.http.client.handler.http.result.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.http.client.handler.http.result.HttpClientResultHandler;
import com.http.client.response.HttpClientResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

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
        logger.info("url:{},result:{}", response.getContext().getHttpUrl(), result);
        if (returnType == String.class){
            return result;
        }
        Object object =toJson(result);
        if (!(object instanceof JSON)){
            //无法转成json,则不处理
            return null;
        }
        JSON jsonObject = (JSON) object;
        toObject(jsonObject);
        Type genericReturnType = response.getContext().getMethod().getGenericReturnType();
        return jsonObject.toJavaObject(genericReturnType);
    }

    private void toObject(JSON json) {
        if (Objects.isNull(json)) {
            return;
        }
        if (json instanceof JSONObject) {
            toObject((JSONObject) json);
        } else {
            toObject((JSONArray) json);
        }

    }

    private void toObject(JSONArray jsonArray) {
        if (Objects.isNull(jsonArray) || jsonArray.isEmpty()) {
            return;
        }

        for (int i = 0; i < jsonArray.size(); i++) {
            Object value = jsonArray.get(i);
            if (value instanceof String) {
                value = toJson((String) value);
                jsonArray.set(i,value);
            }

            if (value instanceof JSON) {
                toObject((JSON) value);
            }
        }
    }


    private void toObject(JSONObject jsonObject) {
        if (Objects.isNull(jsonObject) || jsonObject.isEmpty()) {
            return;
        }
        Set<Map.Entry<String, Object>> entrySet = jsonObject.entrySet();
        for (Map.Entry<String, Object> entry : entrySet) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (Objects.isNull(value)) {
                continue;
            }
            if (value instanceof String) {
                value = tryToObject(jsonObject, key, (String) value);
            }

            if (value instanceof JSON) {
                toObject((JSON) value);
            }
        }
    }

    private Object tryToObject(JSONObject jsonObject, String fieldName, String s) {
        try {
            Object parse = toJson(s);
            jsonObject.put(fieldName, parse);
            return parse;
        } catch (Exception e) {
            e.printStackTrace();
            return s;
        }
    }

    private Object toJson(String value) {
        try {
            return isJson(value) ? JSON.parse(value) : value;
        } catch (Exception e) {
            e.printStackTrace();
            return value;
        }
    }

    private boolean isJson(String value) {
        if (StringUtils.isBlank(value)) {
            return false;
        }
        char firstChar = value.charAt(0);
        char endChar = value.charAt(value.length() - 1);
        return (firstChar == '{' && endChar == '}') || (firstChar == '[' && endChar == ']');
    }
}
