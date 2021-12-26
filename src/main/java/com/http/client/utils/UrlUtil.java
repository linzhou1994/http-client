package com.http.client.utils;

import com.http.client.context.form.Form;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import static com.http.client.constant.HttpClientConstant.*;

/**
 * url 工具类
 * @author linzhou
 */
public class UrlUtil {

    /**
     * 创建带参数的url
     *
     * @param url
     * @param nameValueParams
     * @return
     */
    public static String getParamUrl(String url, List<Form> nameValueParams) {

        if (nameValueParams == null || nameValueParams.isEmpty()) {
            return url;
        }
        if (!url.contains(URL_SPLIT_PARAM)) {
            url = url + URL_SPLIT_PARAM;
        }
        int lastIndex = url.length() - 1;
        if (url.lastIndexOf(URL_SPLIT_PARAM) != lastIndex && url.charAt(lastIndex) != URL_PARAM_SPLIT.charAt(0)) {
            //如果最后一位不是?说明url中已经带了参数,但是最后一位不是&,补一个
            url = url + URL_PARAM_SPLIT;
        }

        StringBuilder sb = new StringBuilder();
        for (Form nameValueParam : nameValueParams) {
            sb.append(nameValueParam.getName()).append("=").append(nameValueParam.getValue()).append(URL_PARAM_SPLIT);
        }

        return url + sb.toString();
    }


    /**
     * 拼接url
     *
     * @param url
     * @param path
     * @return
     */
    public static String splicingUrl(String url, String path) {
        String separate = HTTP_SPLIT;
        if (url.lastIndexOf(separate) != url.length() - 1 && path.indexOf(separate) != 0) {
            //如果url最后没有"/",并且path也没有,则添加一个"/"
            url += separate;
        } else if (url.lastIndexOf(separate) == url.length() - 1 && path.indexOf(separate) == 0) {
            //如果url最后有"/",并且path也有,则删除一个"/"
            url = url.substring(0, url.length() - 1);
        }
        url = url + path;
        try {
            //检测url格式
            new URL(url);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("url:格式错误" + url, e);
        }
        return url;
    }
}
