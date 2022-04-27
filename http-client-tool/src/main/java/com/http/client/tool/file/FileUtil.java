package com.http.client.tool.file;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
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
    private static final String DEFAULT_PATH = "src/main/resources/httpClient";

    public static byte[] getFileBytes(File file) throws IOException {
        FileInputStream fis = new FileInputStream(file);
        ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);

        byte[] b = new byte[1000];
        int n;
        while ((n = fis.read(b)) != -1) {
            bos.write(b, 0, n);

        }

        fis.close();
        bos.close();
        return bos.toByteArray();
    }

    public static File getFile(String path) throws FileNotFoundException {
        return ResourceUtils.getFile("classpath:" + path);
    }

    public static File downFile(InputStream is, String downPath, String fileName) {
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

        return downFile(file.getInputStream(), path, fileName);
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
        String fileName = file.getName();

        //创建文件
        return new MockMultipartFile(fileName,fileName,getContentType(fileName), is);
    }

    public static String getContentType(String fileName){
        Optional<MediaType> mediaType = MediaTypeFactory.getMediaType(fileName);
        return mediaType.orElse(MediaType.APPLICATION_OCTET_STREAM).toString();
    }


    private static String getFilePath(String path, String fileName) {
        if (StringUtils.isBlank(path)) {
            return fileName;
        }
        StringBuilder stringBuilder = new StringBuilder(path);
        if (path.lastIndexOf("/") != path.length()) {
            stringBuilder.append("/");
        }
        return stringBuilder.append(fileName).toString();
    }
}
