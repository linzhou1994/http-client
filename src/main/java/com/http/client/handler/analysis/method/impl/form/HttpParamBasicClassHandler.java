package com.http.client.handler.analysis.method.impl.form;

import com.biz.tool.annotations.AnnotationUtil;
import com.http.client.annotation.HttpParam;
import com.http.client.context.form.AnnotationNameValueParam;
import com.http.client.exception.ParamException;
import com.http.client.handler.analysis.method.AnalysisMethodParamHandler;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.math.BigDecimal;
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
 * @description : 基础类型表单参数处理器
 */
@Component
public class HttpParamBasicClassHandler implements AnalysisMethodParamHandler {
    @Override
    public Object analysisMethodParam(Object param, Annotation[] annotations) {
        HttpParam httpParam = AnnotationUtil.findHttpAnnotation(annotations, HttpParam.class);
        if (Objects.nonNull(httpParam) && isBasicClass(param)) {
            //处理表单参数
            return getNameValueParam(param, httpParam);
        }
        return null;
    }

    /**
     * 处理表单
     */
    private AnnotationNameValueParam getNameValueParam(Object arg, HttpParam httpParam) {
        //如果是表单参数,则当做表单处理
        String name = httpParam.value();
        if (StringUtils.isBlank(name)) {
            throw new ParamException("参数格式错误,没有发现表单参数对应的名称");
        }
        String value;
        if (arg == null) {
            value = null;
        } else {
            value = arg.toString();
        }
        return new AnnotationNameValueParam(httpParam, value);
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
