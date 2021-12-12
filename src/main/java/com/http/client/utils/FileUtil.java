package com.http.client.utils;

import com.http.client.response.HttpClientResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.Optional;

/**
 * @author linzhou
 * @ClassName FileUtil.java
 * @createTime 2021年12月08日 15:55:00
 * @Description
 */
public class FileUtil {
    private static final String DEFAULT_PATH = "src/main/resources/httpClient/down/file";

    public static File getFile(String path) throws FileNotFoundException {
       return ResourceUtils.getFile("classpath:"+path);
    }

    public static File downFile(HttpClientResponse response) {
        return downFile(response, DEFAULT_PATH);
    }

    public static File downFile(HttpClientResponse response, String downPath) {
        String fileName = getFileName(response);
       return downFile(response.getInputStream(),downPath,fileName);
    }

    public static File downFile(InputStream is, String downPath,String fileName) {
        String filePath = getFilePath(downPath, fileName);
        File file = new File(downPath);
        if (!file.isDirectory()) {
            //递归生成文件夹
            file.mkdirs();
        }
        try {
            FileOutputStream fos = new FileOutputStream(filePath);
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
        return new File(filePath);
    }

    /**
     * 文件类型转换
     *
     * @param file
     * @return
     * @throws IOException
     */
    public static File getFile(MultipartFile file, String path) throws IOException {
        if (StringUtils.isBlank(path)) {
            path = DEFAULT_PATH;
        }
        String fileName = file.getOriginalFilename();

        return downFile(file.getInputStream(),path,fileName);
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
        if (path.lastIndexOf("/") != path.length()) {
            stringBuilder.append("/");
        }
        return stringBuilder.append(fileName).toString();
    }

    /**
     * 解析文件头
     * Content-Disposition:attachment;filename=FileName.txt
     * Content-Disposition: attachment; filename*="UTF-8''%E6%9B%BF%E6%8D%A2%E5%AE%9E%E9%AA%8C%E6%8A%A5%E5%91%8A.pdf"
     */
    private static String getHeaderFileName(HttpClientResponse response) {
        String dispositionHeader = response.getHeader("Content-Disposition");
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
                .map(o -> o.substring(o.lastIndexOf("/") + 1))
                .orElse("HttpClientDownFile");

    }
}
