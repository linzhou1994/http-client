package com.http.client.tool.spring.http;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.URLEncoder;

/**
 * http响应工具
 *
 * @author
 */
public class ResponseUtil {

    public static void downloadFile(HttpServletResponse response, String filePath, String fileName) throws IOException {
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        try {
            response.setHeader("Content-Disposition", "attachment;fileName=" + URLEncoder.encode(fileName, "UTF-8"));

            Resource resource = new ClassPathResource(filePath);

            bis = new BufferedInputStream(resource.getInputStream());
            bos = new BufferedOutputStream(response.getOutputStream());

            int len = 0;
            byte[] tmp = new byte[1024];
            while ((len = bis.read(tmp)) != -1) {
                bos.write(tmp, 0, len);
            }
            bos.flush();
            bos.close();
            bis.close();
        } finally {
            if (bis != null) {
                bis.close();
            }
            if (bos != null) {
                bos.close();
            }
        }

    }

}
