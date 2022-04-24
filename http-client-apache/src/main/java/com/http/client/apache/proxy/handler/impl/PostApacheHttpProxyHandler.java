package com.http.client.apache.proxy.handler.impl;

import com.http.client.apache.proxy.handler.ApacheHttpProxyHandler;
import com.http.client.context.HttpRequestContext;
import com.http.client.context.body.Body;
import com.http.client.context.body.file.FileBody;
import com.http.client.context.form.Form;
import com.http.client.enums.HttpRequestMethod;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
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
 * @date : 2022/4/24 10:03
 * @author: linzhou
 * @description : PostApacheHttpProxyHandler
 */
@Component
public class PostApacheHttpProxyHandler implements ApacheHttpProxyHandler {

    @Override
    public List<HttpRequestMethod> getRequestMethods() {
        return Collections.singletonList(HttpRequestMethod.POST);
    }

    @Override
    public HttpRequestBase getRequest(HttpRequestContext context) throws Throwable {
        return getHttpPost(context);
    }


    /**
     * 创建post请求
     *
     * @param context
     * @return
     * @throws UnsupportedEncodingException
     */
    private HttpRequestBase getHttpPost(HttpRequestContext context) throws Throwable {
        String url = context.getHttpUrl();
        HttpPost httpPost = new HttpPost(url);

        HttpEntity entity = getHttpEntity(context);
        if (entity != null) {
            //设置entity
            httpPost.setEntity(entity);
        }
        return httpPost;
    }


    private HttpEntity getHttpEntity(HttpRequestContext context) throws IOException {

        Body body = context.getBody();
        if (Objects.nonNull(body) && StringUtils.isNotBlank(body.getBody())) {
            //设置body
            String mimeType = body.mimeType();
            String charset = body.charset();
            ContentType contentType = ContentType.create(mimeType, charset);
            return new StringEntity(body.getBody(), ContentType.APPLICATION_JSON);
        }
        List<FileBody> uploadFiles = context.getUploadFiles();
        List<Form> nameValueParams = context.getNameValueParams();
        if (CollectionUtils.isEmpty(uploadFiles)) {

            List<NameValuePair> pairs = new ArrayList<>(nameValueParams.size());
            for (Form nameValueParam : nameValueParams) {
                String value = nameValueParam.getValue();
                if (value != null) {
                    pairs.add(new BasicNameValuePair(nameValueParam.getName(), value));
                }
            }
            return new UrlEncodedFormEntity(pairs, StandardCharsets.UTF_8);
        }
        //设置文件上传
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        //加上此行代码解决返回中文乱码问题
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        if (CollectionUtils.isNotEmpty(uploadFiles)) {
            FileBody fileBody = uploadFiles.get(0);

            ByteArrayBody byteArrayBody = new ByteArrayBody(fileBody.getFileBytes(), fileBody.getFileName());
            String value = StringUtils.isBlank(fileBody.getName()) ? "src/test/resources/file" : fileBody.getName();
            builder.addPart(value, byteArrayBody);

        }

        if (CollectionUtils.isNotEmpty(nameValueParams)) {
            for (Form nameValueParam : nameValueParams) {
                String mimeType = nameValueParam.mimeType();
                String charset = nameValueParam.charset();
                ContentType contentType = ContentType.create(mimeType, charset);
                builder.addTextBody(nameValueParam.getName(), nameValueParam.getValue(), contentType);
            }
        }
        return builder.build();
    }

}
