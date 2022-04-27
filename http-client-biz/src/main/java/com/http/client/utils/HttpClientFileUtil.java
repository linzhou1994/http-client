package com.http.client.utils;

import com.http.client.constant.HttpClientConstant;
import com.http.client.response.BaseHttpClientResponse;
import com.http.client.response.HttpClientResponse;
import com.http.client.tool.file.FileUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.mock.web.MockMultipartFile;

import java.io.*;
import java.util.Optional;

/**
 * @author linzhou
 * @ClassName FileUtil.java
 * @createTime 2021年12月08日 15:55:00
 * @Description
 */
public class HttpClientFileUtil {


    public static File downFile(HttpClientResponse response, String downPath) {
        String fileName = getFileName(response);
       return FileUtil.downFile(response.getInputStream(),downPath,fileName);
    }



    /**
     * 获取文件
     *
     * @param response
     * @return
     * @throws IOException
     */
    public static MockMultipartFile getMockMultipartFile(HttpClientResponse response) throws IOException {
        //如果是文件下载
        InputStream inputStream = response.getInputStream();
        //创建文件
        return new MockMultipartFile(getFileName(response), inputStream);
    }

    /**
     * 获取文件名称
     */
    private static String getFileName(HttpClientResponse response) {
        //从header中获取文件名称
        return Optional.ofNullable(getHeaderFileName(response))
                //如果header中没有文件名称,则从url上获取
                .orElse(getUrlFileName(response));
    }

    private static String getFilePath( String path,String fileName) {
        if (StringUtils.isBlank(path)) {
            return fileName;
        }
        StringBuilder stringBuilder = new StringBuilder(path);
        if (path.lastIndexOf(HttpClientConstant.HTTP_SPLIT) != path.length()) {
            stringBuilder.append(HttpClientConstant.HTTP_SPLIT);
        }
        return stringBuilder.append(fileName).toString();
    }

    /**
     * 解析文件头
     * Content-Disposition:attachment;filename=FileName.txt
     * Content-Disposition: attachment; filename*="UTF-8''%E6%9B%BF%E6%8D%A2%E5%AE%9E%E9%AA%8C%E6%8A%A5%E5%91%8A.pdf"
     */
    private static String getHeaderFileName(HttpClientResponse response) {
        String dispositionHeader = response.getResponseHeard("Content-Disposition");
        if (StringUtils.isNotBlank(dispositionHeader)) {
            String[] strings = dispositionHeader.split(";");
            if (strings.length > 1) {
                dispositionHeader = strings[1].replace("filename=", "");
                dispositionHeader = dispositionHeader.replace("fileName=", "");
                dispositionHeader = dispositionHeader.replace("\"", "");
                return dispositionHeader;
            }
        }
        return null;
    }

    /**
     * 通过url获取文件名称
     *
     * @param response
     * @return
     */
    public static String getUrlFileName(HttpClientResponse response) {
        return Optional.ofNullable(response)
                .map(HttpClientResponse::getHttpUrl)
                .map(o -> o.substring(o.lastIndexOf(HttpClientConstant.HTTP_SPLIT) + 1))
                .orElse(HttpClientConstant.DEFAULT_HTTP_CLIENT_DOWN_FILE_NAME);

    }
}
