package com.http.client.handler.analysis.method.impl.body;

import com.http.client.annotation.HttpFile;
import com.http.client.bo.FileParam;
import com.http.client.context.body.file.FileBody;
import com.http.client.context.body.file.UploadFile;
import com.http.client.context.body.file.UploadMultipartFile;
import com.http.client.exception.ParamException;
import com.http.client.handler.analysis.method.AnalysisMethodParamHandler;
import com.http.client.tool.annotations.AnnotationUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
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
 * @date : 2021/12/12 15:28
 * @author: linzhou
 * @description : 文件参数处理器
 */
@Component
public class HttpFileHandler implements AnalysisMethodParamHandler {
    @Override
    public Object analysisMethodParam(Object param, Annotation[] annotations) throws IOException {
        HttpFile httpFile = AnnotationUtil.findHttpAnnotation(annotations, HttpFile.class);
        if (Objects.nonNull(httpFile)){
            return getUploadFile(param,httpFile);
        }
        return null;
    }
    /**
     * 处理文件上传
     *
     * @param arg
     * @param httpFile
     * @return
     */
    private FileBody getUploadFile(Object arg, HttpFile httpFile) throws IOException {
        FileBody uploadFile;
        //保存需要上传的文件信息
        if (arg instanceof MultipartFile) {
            uploadFile = new UploadMultipartFile(httpFile, (MultipartFile) arg);
        } else if (arg instanceof FileParam) {
            FileParam fileParam = (FileParam) arg;
            uploadFile = new UploadMultipartFile(httpFile, fileParam.getFile(), fileParam.getParam());
        } else if (arg instanceof File) {
            uploadFile = new UploadFile(httpFile, (File) arg);
        } else {
            throw new ParamException("参数格式错误,上传文件应为MultipartFile或者file类型,当前类型:" + arg.getClass().getName());
        }
        return uploadFile;
    }
}
