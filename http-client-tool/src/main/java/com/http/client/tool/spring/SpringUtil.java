package com.http.client.tool.spring;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

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
 * //         佛祖保佑           永无BUG           永不修改             //
 * //          佛曰:                                                  //
 * //                 写字楼里写字间，写字间里程序员;                     //
 * //                 程序人员写程序，又拿程序换酒钱.                     //
 * //                 酒醒只在网上坐，酒醉还来网下眠;                     //
 * //                 酒醉酒醒日复日，网上网下年复年.                     //
 * //                 但愿老死电脑间，不愿鞠躬老板前;                     //
 * //                 奔驰宝马贵者趣，公交自行程序员.                     //
 * //                 别人笑我忒疯癫，我笑自己命太贱;                     //
 * //                 不见满街漂亮妹，哪个归得程序员?                     //
 * ////////////////////////////////////////////////////////////////////
 *
 * @date : 2021/12/12 15:07
 * @author: linzhou
 * @description : SpringUtil
 */
@Component
public class SpringUtil implements ApplicationContextAware {
    private static ApplicationContext applicationContext;

    /**
     * 获取指定类型的bean集合
     *
     * @param tClass
     * @param <T>
     * @return
     */
    public static <T> List<T> getBeanList(Class<T> tClass) {
        Map<String, T> beansOfType = applicationContext.getBeansOfType(tClass);
        List<T> rlt = new ArrayList<>(beansOfType.size());
        rlt.addAll(beansOfType.values());
        rlt.sort(Comparator.comparingInt(SpringUtil::getOrder));
        return rlt;
    }

    /**
     * 获取指定类型的bean
     *
     * @param tClass
     * @param <T>
     * @return
     */
    public static <T> T getBean(Class<T> tClass) {
        return applicationContext.getBean(tClass);
    }


    public static int getOrder(Object o) {
        Order order = AnnotationUtils.findAnnotation(o.getClass(), Order.class);
        if (order != null) {
            return order.value();
        }
        return 0;
    }

    /**
     * 获取controller类及方法上的指定注解,方法上的优先
     *
     * @param c
     * @param <T>
     * @return
     */
    public static <T extends Annotation> T getRequestAnnotation(Class<T> c) {
        Object handler = getHandler();
        if (Objects.isNull(handler)) {
            return null;
        }
        T annotation = null;
        if (handler instanceof HandlerMethod) {
            final HandlerMethod handlerMethod = (HandlerMethod) handler;
            final Class<?> clazz = handlerMethod.getBeanType();
            final Method method = handlerMethod.getMethod();
            annotation = method.getAnnotation(c);
            if (Objects.isNull(annotation)) {
                annotation = clazz.getAnnotation(c);
            }
        }
        return annotation;
    }

    public static Object getHandler() {
        DispatcherServlet dispatcherServlet = applicationContext.getBean(DispatcherServlet.class);
        List<HandlerMapping> handlerMappings = dispatcherServlet.getHandlerMappings();
        HttpServletRequest request = getHttpServletRequest();
        if (handlerMappings != null) {
            for (HandlerMapping mapping : handlerMappings) {
                try {
                    HandlerExecutionChain handler = mapping.getHandler(request);
                    if (handler != null) {
                        return handler.getHandler();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * 获取request
     *
     * @return
     */
    public static HttpServletRequest getHttpServletRequest() {
        ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return sra.getRequest();
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringUtil.applicationContext = applicationContext;
    }
}
