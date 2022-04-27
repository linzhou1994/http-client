package com.http.client.interceptor.impl.explain;

import com.http.client.context.HttpRequestContext;
import com.http.client.tool.spring.SpringUtil;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
 * @date : 2022/4/17 20:34
 * @author: linzhou
 * @description : ExplainManager
 */

public class ExplainManager {

    private static Map<Class<? extends Annotation>, Explain> explainMap;


    public static void explain(HttpRequestContext context, Annotation annotation) {

        Explain explain = getExplainMap().get(annotation.getClass());
        if (explain != null) {
            explain.explain(context, annotation);
        }
    }

    private static Map<Class<? extends Annotation>, Explain> getExplainMap() {
        if (explainMap == null) {
            synchronized (ExplainManager.class) {
                if (explainMap == null) {
                    List<Explain> explainList = SpringUtil.getBeanList(Explain.class);
                    explainMap = new HashMap<>();
                    for (Explain explain : explainList) {
                        explainMap.put(explain.getAnnotation(), explain);
                    }
                }
            }
        }
        return explainMap;
    }

}
