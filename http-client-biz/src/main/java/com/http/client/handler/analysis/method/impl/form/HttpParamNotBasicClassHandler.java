package com.http.client.handler.analysis.method.impl.form;

import com.alibaba.fastjson.JSON;
import com.biz.tool.annotations.AnnotationUtil;
import com.http.client.annotation.HttpParam;
import com.http.client.context.form.NameValueParam;
import com.http.client.exception.ParamException;
import com.http.client.handler.analysis.method.AnalysisMethodParamHandler;
import com.http.client.utils.ReflectUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.math.BigDecimal;
import java.util.ArrayList;
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
 * //         佛祖保佑           永无BUG           永不修改              //
 * //          佛曰:                                                  //
 * //                 写字楼里写字间，写字间里程序员;                      //
 * //                 程序人员写程序，又拿程序换酒钱.                      //
 * //                 酒醒只在网上坐，酒醉还来网下眠;                      //
 * //                 酒醉酒醒日复日，网上网下年复年.                      //
 * //                 但愿老死电脑间，不愿鞠躬老板前;                      //
 * //                 奔驰宝马贵者趣，公交自行程序员.                      //
 * //                 别人笑我忒疯癫，我笑自己命太贱;                      //
 * //                 不见满街漂亮妹，哪个归得程序员?                      //
 * ////////////////////////////////////////////////////////////////////
 *
 * @date : 2021/12/12 15:24
 * @author: linzhou
 * @description : 非基础类型表单参数处理器
 */
@Component
public class HttpParamNotBasicClassHandler implements AnalysisMethodParamHandler {
    @Override
    public Object analysisMethodParam(Object param, Annotation[] annotations) {
        HttpParam httpParam = AnnotationUtil.findHttpAnnotation(annotations, HttpParam.class);
        if (Objects.nonNull(httpParam) && !isBasicClass(param) && !(param instanceof Collection)) {
            //处理表单参数
            return getNameValueParam(param, httpParam);
        }
        return null;
    }

    /**
     * 处理表单
     */
    private List<NameValueParam> getNameValueParam(Object arg, HttpParam httpParam) {
        //如果是表单参数,则当做表单处理
        if (arg == null) {
            return Collections.emptyList();
        }
        if ((arg instanceof Collection) || (arg instanceof Map)) {
            throw new ParamException("类型错误,error class:" + arg.getClass().getName());
        }

        List<ReflectUtil.ReflectField> allFieldAndValue = ReflectUtil.getAllFieldAndValue(arg);

        List<NameValueParam> nameValueParams = new ArrayList<>(allFieldAndValue.size());

        for (ReflectUtil.ReflectField reflectField : allFieldAndValue) {
            String value = objectToString(reflectField.getValue());
            if (StringUtils.isBlank(value)) {
                continue;
            }
            String key = reflectField.getField().getName();
            NameValueParam nameValueParam = new NameValueParam(key, value);
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
     * 是否是基础类型
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
