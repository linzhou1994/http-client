package com.http.client.client;

import com.http.client.annotation.HttpClient;
import com.http.client.annotation.HttpParam;
import com.http.client.bo.HttpUrl;
import com.http.client.enums.HttpRequestMethod;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@HttpClient(method = HttpRequestMethod.POST)
public interface DownloadFileClient {

    MultipartFile downloadMultipartFile(HttpUrl httpUrl,@HttpParam("param")String param);

    File downloadFile(HttpUrl httpUrl,@HttpParam("param")String param);

}
