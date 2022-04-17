package com.http.client.interceptor.impl;

import com.http.client.annotation.HttpClient;
import com.http.client.context.HttpRequestContext;
import com.http.client.interceptor.HttpClientInterceptor;
import com.http.client.interceptor.impl.explain.ExplainManager;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;

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
 * @date : 2022/4/17 20:29
 * @author: linzhou
 * @description : AnnotationInterceptor 用于执行注释的拦截器
 */
@Component
public class AnnotationInterceptor implements HttpClientInterceptor {

    @Override
    public Object httpBefore(HttpRequestContext context) {

        Annotation[] annotations = context.getMethod().getAnnotations();
        Set<Class<?extends Annotation>> breakExplain = new HashSet<>();
        breakExplain.add(HttpClient.class);
        //执行方法上声明的注解
        for (Annotation annotation : context.getType().getAnnotations()) {
            Class<? extends Annotation> annotationClass = annotation.getClass();
            if (!(breakExplain.contains(annotationClass))) {
                ExplainManager.explain(context, annotation);
                //方法上执行过得，在接口上跳过
                breakExplain.add(annotationClass);
            }
        }
        //执行接口上声明的注解
        for (Annotation annotation : context.getType().getAnnotations()) {
            Class<? extends Annotation> annotationClass = annotation.getClass();
            if (!(breakExplain.contains(annotationClass))) {
                ExplainManager.explain(context, annotation);
            }
        }
        return null;
    }
}
