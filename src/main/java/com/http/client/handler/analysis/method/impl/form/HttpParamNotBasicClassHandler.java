package com.http.client.handler.analysis.method.impl.form;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.biz.tool.annotations.AnnotationUtil;
import com.http.client.annotation.HttpParam;
import com.http.client.context.form.AnnotationNameValueParam;
import com.http.client.context.form.NameValueParam;
import com.http.client.exception.ParamException;
import com.http.client.handler.analysis.method.AnalysisMethodParamHandler;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * ////////////////////////////////////////////////////////////////////
 * //                          _ooOoo_                               //
 * //                         o8888888o                              //
 * //                         88" . "88                              //
 * //                         (| ^_^ |)                              //
 * //                         O\  =  /O                              //
 * //                      ____/`---'\____                           //
 * //                    .'  \\|     |//  `.                         //
 * //                   /  \\|||  :  |||//  \                        //
 * //                  /  _||||| -:- |||||-  \                       //
 * //                  |   | \\\  -  /// |   |                       //
 * //                  | \_|  ''\---/''  |   |                       //
 * //                  \  .-\__  `-`  ___/-. /                       //
 * //                ___`. .'  /--.--\  `. . ___                     //
 * //              ."" '<  `.___\_<|>_/___.'  >'"".                  //
 * //            | | :  `- \`.;`\ _ /`;.`/ - ` : | |                 //
 * //            \  \ `-.   \_ __\ /__ _/   .-` /  /                 //
 * //      ========`-.____`-.___\_____/___.-`____.-'========         //
 * //                           `=---='                              //
 * //      ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^        //
 * //         ????????????           ??????BUG           ????????????              //
 * //          ??????:                                                  //
 * //                 ?????????????????????????????????????????????;                      //
 * //                 ?????????????????????????????????????????????.                      //
 * //                 ?????????????????????????????????????????????;                      //
 * //                 ?????????????????????????????????????????????.                      //
 * //                 ?????????????????????????????????????????????;                      //
 * //                 ?????????????????????????????????????????????.                      //
 * //                 ?????????????????????????????????????????????;                      //
 * //                 ??????????????????????????????????????????????                      //
 * ////////////////////////////////////////////////////////////////////
 *
 * @date : 2021/12/12 15:24
 * @author: linzhou
 * @description : ????????????????????????????????????
 */
@Component
public class HttpParamNotBasicClassHandler implements AnalysisMethodParamHandler {
    @Override
    public Object analysisMethodParam(Object param, Annotation[] annotations) {
        HttpParam httpParam = AnnotationUtil.findHttpAnnotation(annotations, HttpParam.class);
        if (Objects.nonNull(httpParam) && !isBasicClass(param) && !(param instanceof Collection)) {
            //??????????????????
            return getNameValueParam(param, httpParam);
        }
        return null;
    }

    /**
     * ????????????
     */
    private List<NameValueParam> getNameValueParam(Object arg, HttpParam httpParam) {
        //?????????????????????,?????????????????????
        if (arg == null) {
            return Collections.emptyList();
        }

        Object o = JSONObject.toJSON(arg);

        if (!(o instanceof JSONObject)) {
            throw new ParamException("????????????,error class:" + arg.getClass().getName());
        }
        List<NameValueParam> nameValueParams = new ArrayList<>();

        JSONObject jsonObject = (JSONObject) o;
        for (Map.Entry<String, Object> entry : jsonObject.entrySet()) {
            String value = objectToString(entry.getValue());
            if (StringUtils.isBlank(value)){
                continue;
            }
            NameValueParam nameValueParam = new NameValueParam(entry.getKey(), value);
            nameValueParams.add(nameValueParam);
        }
        return nameValueParams;
    }

    private String objectToString(Object o) {
        if (Objects.isNull(o)) {
            return null;
        }
        if (o instanceof String) {
            return (String) o;
        }
        return JSON.toJSONString(o);
    }

    /**
     * ?????????????????????
     *
     * @param o
     * @return
     */
    protected boolean isBasicClass(Object o) {
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
}
