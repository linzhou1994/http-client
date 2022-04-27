package com.http.client.handler.analysis.method.impl.url;

import com.http.client.annotation.HttpUrl;
import com.http.client.context.url.HttpRequestUrl;
import com.http.client.handler.analysis.method.AnalysisMethodParamHandler;
import com.http.client.tool.annotations.AnnotationUtil;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
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
 * @date : 2021/12/12 15:34
 * @author: linzhou
 * @description : HttpUrlHandler
 */
@Component
public class HttpUrlHandler implements AnalysisMethodParamHandler {
    @Override
    public Object analysisMethodParam(Object param, Annotation[] annotations) throws Exception {
        if (param instanceof HttpRequestUrl){
            return param;
        }
        HttpUrl httpUrl = AnnotationUtil.findHttpAnnotation(annotations, HttpUrl.class);
        if (Objects.nonNull(httpUrl)){
            if (param instanceof String){
                return new HttpRequestUrl((String) param);
            }else {
                throw new IllegalArgumentException("@HttpUrl注解只能使用在String类型的方法参数上");
            }
        }
        return null;
    }
}
