package com.http.client.response;

import com.http.client.context.HttpRequestContext;
import com.http.client.context.header.HttpHeader;

import java.io.IOException;
import java.io.InputStream;

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
 * @date : 2021/12/29 21:02
 * @author: linzhou
 * @description : HttpClientResponse
 */
public interface HttpClientResponse {

    int getCode();

    /**
     * 请求是否成功
     *
     * @return
     */
    boolean isSuccessful();

    /**
     * 获取请求内容
     *
     * @return
     * @throws IOException
     */
    String result() throws IOException;

    /**
     * 获取指定ResponseHeard
     *
     * @param name
     * @return
     */
    String getResponseHeard(String name);

    /**
     * 获取所有的ResponseHeard
     *
     * @return
     */
    HttpHeader getResponseHeard();

    /**
     * 获取请求地址
     *
     * @return
     */
    String getHttpUrl();

    /**
     * 获取请求上下文
     *
     * @return
     */
    HttpRequestContext getContext();

    void setContext(HttpRequestContext context);

    /**
     * 获取返回流
     *
     * @return
     */
    InputStream getInputStream();


}
