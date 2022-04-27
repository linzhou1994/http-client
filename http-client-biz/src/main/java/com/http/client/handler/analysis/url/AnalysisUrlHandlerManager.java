package com.http.client.handler.analysis.url;

import com.http.client.context.HttpRequestContext;
import com.http.client.tool.spring.SpringUtil;
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
 * @date : 2021/12/12 15:03
 * @author: linzhou
 * @description : AnalysisMethodParamHandlerManager
 */
public class AnalysisUrlHandlerManager {

    private static List<AnalysisUrlHandler> analysisUrlHandlers;

    public static String analysisUrl(HttpRequestContext context) throws Exception {

        List<AnalysisUrlHandler> analysisUrlHandlers = getAnalysisUrlHandlers();
        AnalysisUrlHandler.AnalysisUrlResult rlt = null;
        for (AnalysisUrlHandler analysisUrlHandler : analysisUrlHandlers) {
            rlt = analysisUrlHandler.analysisUrl(context, getUrl(rlt));
            if (Objects.nonNull(rlt) && rlt.isBreak()) {
                return rlt.getUrl();
            }
        }
        return getUrl(rlt);
    }

    private static String getUrl(AnalysisUrlHandler.AnalysisUrlResult rlt) {
        return Objects.isNull(rlt) ? null : rlt.getUrl();
    }

    private static List<AnalysisUrlHandler> getAnalysisUrlHandlers() {
        if (CollectionUtils.isEmpty(analysisUrlHandlers)) {
            analysisUrlHandlers = SpringUtil.getBeanList(AnalysisUrlHandler.class);
        }
        return analysisUrlHandlers;
    }

}
