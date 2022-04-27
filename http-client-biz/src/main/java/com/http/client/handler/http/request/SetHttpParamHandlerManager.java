package com.http.client.handler.http.request;

import com.http.client.bo.HttpClientRequest;
import com.http.client.tool.spring.SpringUtil;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

/**
 * @author linzhou
 * @ClassName SetHttpParam.java
 * @createTime 2021年12月15日 11:47:00
 * @Description
 */
public class SetHttpParamHandlerManager {

    private static List<SetHttpParamHandler> setHttpParamHandlers;


    /**
     * 将参数添加到request中去
     *
     * @param request
     * @param o       需要添加到request中的参数
     */
   public static void setHttpParam(HttpClientRequest request, Object o) {
        List<SetHttpParamHandler> httpClientResultHandlers = getSetHttpParamHandlers();
        for (SetHttpParamHandler httpClientResultHandler : httpClientResultHandlers) {
            if (httpClientResultHandler.setHttpParam(request, o)) {
                return;
            }
        }
    }

    private static List<SetHttpParamHandler> getSetHttpParamHandlers() {
        if (CollectionUtils.isEmpty(setHttpParamHandlers)) {
            setHttpParamHandlers = SpringUtil.getBeanList(SetHttpParamHandler.class);
        }
        return setHttpParamHandlers;
    }

}
