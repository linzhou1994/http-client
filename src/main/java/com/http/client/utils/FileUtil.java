package com.http.client.utils;

import com.http.client.bo.HttpClientResponse;
import com.squareup.okhttp.Response;
import org.apache.commons.lang3.StringUtils;
import org.springframework.mock.web.MockMultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

/**
 * @author linzhou
 * @ClassName FileUtil.java
 * @createTime 2021年12月08日 15:55:00
 * @Description
 */
public class FileUtil {

    public static File downFile(HttpClientResponse response) {
        return downFile(response, "httpClient/");
    }

    public static File downFile(HttpClientResponse response, String downPath) {
        String fileName = getFilePath(response, downPath);
        try {
            InputStream is = new ByteArrayInputStream(response.getBytes());
            FileOutputStream fos = new FileOutputStream(fileName);
            int len;
            byte[] bytes = new byte[4096];
            while ((len = is.read(bytes)) != -1) {
                fos.write(bytes, 0, len);
            }
            fos.flush();
            is.close();
            fos.close();
        } catch (Exception ex) {
            return null;
        }
        return new File(fileName);
    }

    /**
     * 文件类型转换
     *
     * @param file
     * @return
     * @throws IOException
     */
    public static MockMultipartFile getMockMultipartFile(File file) throws IOException {
        //如果是文件下载
        InputStream is = new FileInputStream(file);
        //创建文件
        return new MockMultipartFile(file.getName(), is);
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
        byte[] bytes = response.getBytes();
        InputStream inputStream = new ByteArrayInputStream(bytes);
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

    private static String getFilePath(HttpClientResponse response, String path) {
        if (StringUtils.isBlank(path)) {
            return getFileName(response);
        }
        StringBuilder stringBuilder = new StringBuilder(path);
        if (path.lastIndexOf("/") != path.length()) {
            stringBuilder.append("/");
        }
        return stringBuilder.append(getFileName(response)).toString();
    }

    /**
     * 解析文件头
     * Content-Disposition:attachment;filename=FileName.txt
     * Content-Disposition: attachment; filename*="UTF-8''%E6%9B%BF%E6%8D%A2%E5%AE%9E%E9%AA%8C%E6%8A%A5%E5%91%8A.pdf"
     */
    private static String getHeaderFileName(HttpClientResponse response) {
        String dispositionHeader = response.getHeader("Content-Disposition");
        if (StringUtils.isNotBlank(dispositionHeader)) {
            dispositionHeader.replace("attachment;filename=", "");
            dispositionHeader.replace("filename*=utf-8", "");
            String[] strings = dispositionHeader.split("; ");
            if (strings.length > 1) {
                dispositionHeader = strings[1].replace("filename=", "");
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
                .map(o -> o.substring(o.lastIndexOf("/") + 1))
                .orElse("HttpClientDownFile");

    }
}
