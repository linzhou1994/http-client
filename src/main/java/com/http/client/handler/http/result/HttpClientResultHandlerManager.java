package com.http.client.handler.http.result;

import com.biz.tool.spring.SpringUtil;
import com.http.client.response.HttpClientResponse;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
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
 * @date : 2021/12/12 15:49
 * @author: linzhou
 * @description : HttpClientResultHandlerManager
 */
public class HttpClientResultHandlerManager {

    private static List<HttpClientResultHandler> httpClientResultHandlers;

    public static Object getReturnObject(HttpClientResponse response) throws Exception {

        List<HttpClientResultHandler> httpClientResultHandlers = getAnalysisMethodParamHandlers();
        Class<?> returnType = response.getContext().getMethod().getReturnType();
        for (HttpClientResultHandler httpClientResultHandler : httpClientResultHandlers) {
            Object rlt = httpClientResultHandler.getReturnObject(response,returnType);
            if (Objects.nonNull(rlt)){
                return rlt;
            }
        }
        return null;
    }

    private static List<HttpClientResultHandler> getAnalysisMethodParamHandlers(){
        if (CollectionUtils.isEmpty(httpClientResultHandlers)){
            httpClientResultHandlers = SpringUtil.getBeanList(HttpClientResultHandler.class);
        }
        return httpClientResultHandlers;
    }
}
