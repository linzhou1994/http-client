package com.http.client.client;

import com.http.client.annotation.HttpClient;
import com.http.client.annotation.HttpParam;
import com.http.client.context.header.HttpHeader;
import com.http.client.context.url.HttpRequestUrl;
import com.http.client.enums.HttpRequestMethod;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@HttpClient(method = HttpRequestMethod.POST)
public interface DownloadFileClient {


    /**
     * 下载文件
     *
     * @param httpUrl
     * @param param
     * @return
     */
    MultipartFile downloadMultipartFile(HttpRequestUrl httpUrl, HttpHeader header, @HttpParam Object param);

    /**
     * 下载文件
     * @param httpUrl
     * @param param
     * @return
     */
    File downloadFile(HttpRequestUrl httpUrl, HttpHeader header, @HttpParam Object param);

}
