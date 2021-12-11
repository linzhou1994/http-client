package com.http.client;


import com.http.client.bo.HttpUrl;
import com.http.client.client.DownloadFileClient;
import com.http.client.utils.FileUtil;
import lombok.extern.log4j.Log4j2;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;

@Log4j2
public class HttpClientTest extends BaseTest {

    @Autowired
    private HttpTestClient httpTestClient;
    @Autowired
    private DownloadFileClient httpDownloadClient;

    /**
     * post 请求
     */
    @Test
    public void postTest() {
        LoginParam p = new LoginParam();
        p.setPassword("12345678");
        p.setName("httpClient");
        String rlt = httpTestClient.postLogin(p);
        log.info("rlt1:" + rlt);
    }

    /**
     * post 无参数请求
     */
    @Test
    public void postTest2() {
        String rlt = httpTestClient.post();
        log.info("rlt1:" + rlt);
    }

    /**
     * get 请求
     */
    @Test
    public void getTest() {
        String rlt = httpTestClient.getLogin("httpClient", "12345678");
        log.info("rlt1:" + rlt);
    }

    /**
     * 文件上传
     *
     * @throws FileNotFoundException
     */
    @Test
    public void uploadFile() throws FileNotFoundException {
        File file = FileUtil.getFile("file/fileTest.txt");
        String rlt = httpTestClient.uploadFile(file, "uploadFile");
        log.info("rlt1:" + rlt);
    }

    /**
     * 文件上传
     *
     * @throws IOException
     */
    @Test
    public void uploadMultipartFile() throws IOException {
        File file = FileUtil.getFile("file/fileTest2.txt");
        String rlt = httpTestClient.uploadMultipartFile(FileUtil.getMockMultipartFile(file), "uploadMultipartFile");
        log.info("rlt1:" + rlt);
    }

    /**
     * 文件下载
     */
    @Test
    public void downloadFileTest() {
        HttpUrl httpUrl = new HttpUrl("http://127.0.0.1:8080/file/downloadFile");
        File file = httpDownloadClient.downloadFile(httpUrl, "123");
    }

    /**
     * 文件下载
     * @throws IOException
     */
    @Test
    public void downloadMultipartFileTest() throws IOException {
        HttpUrl httpUrl = new HttpUrl("http://127.0.0.1:8080/file/downloadFile");
        MultipartFile file = httpDownloadClient.downloadMultipartFile(httpUrl, "123");
        String s = new String(getByte(file.getInputStream()), StandardCharsets.UTF_8);
        log.info("downloadMultipartFileTest test:" + s);
    }

    public byte[] getByte(InputStream inputStream) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int n = 0;
        while (-1 != (n = inputStream.read(buffer))) {
            output.write(buffer, 0, n);
        }
        return output.toByteArray();
    }

}
