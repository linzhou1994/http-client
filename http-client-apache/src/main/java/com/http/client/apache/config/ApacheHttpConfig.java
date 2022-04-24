package com.http.client.apache.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;
import java.util.Optional;

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
 * @date : 2022/4/24 10:12
 * @author: linzhou
 * @description : ApacheHttpConfig
 */
@Component
@ConfigurationProperties(prefix = "httpclient.apache")
@Data
@Slf4j
public class ApacheHttpConfig {

    private int connectionTimeout = 300000;
    private int socketTimeout = 300000;
    private int connectionRequestTimeout = 300000;
    private int maxConnectionPerHost = 20;
    private int maxTotalConnections = 100;
    private String sslProtocol = "SSL";

    @Bean
    public CloseableHttpClient closeableHttpClient(){
        return initClient(this);
    }


    /**
     * 初始化httpClient
     *
     * @return
     */
    private static CloseableHttpClient initClient(ApacheHttpConfig apacheHttpConfig) {
        // 设置协议http和https对应的处理socket链接工厂的对象
        RegistryBuilder<ConnectionSocketFactory> socketFactoryBuilder = RegistryBuilder.create();
        socketFactoryBuilder.register("http", PlainConnectionSocketFactory.INSTANCE);
        // 设置协议http和https对应的处理socket链接工厂的对象
        Optional.ofNullable(createIgnoreVerifySSL(apacheHttpConfig))
                .ifPresent(sslContext -> socketFactoryBuilder.register("https",
                        new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE)));
        // 设置链接池
        PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(socketFactoryBuilder.build());
        connManager.setMaxTotal(apacheHttpConfig.getMaxTotalConnections());
        connManager.setDefaultMaxPerRoute(apacheHttpConfig.getMaxConnectionPerHost());
        //创建自定义的httpclient对象
        return HttpClients.custom().setConnectionManager(connManager)
                .setDefaultRequestConfig(RequestConfig.custom().setSocketTimeout(apacheHttpConfig.getSocketTimeout())
                        .setConnectTimeout(apacheHttpConfig.getConnectionTimeout()).setConnectionRequestTimeout(apacheHttpConfig.getConnectionRequestTimeout()).build()
                ).build();
    }

    /**
     * 创建ssl
     *
     * @return
     * @param apacheHttpConfig
     */
    private static SSLContext createIgnoreVerifySSL(ApacheHttpConfig apacheHttpConfig) {
        try {
            SSLContext ctx = SSLContext.getInstance(apacheHttpConfig.getSslProtocol());
            X509TrustManager tm = new X509TrustManager() {
                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                @Override
                public void checkClientTrusted(X509Certificate[] arg0, String arg1) {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] arg0, String arg1) {
                }
            };
            ctx.init(null, new TrustManager[]{tm}, null);
            return ctx;
        } catch (Exception e) {
            log.error("SSL创建失败:" + e);
        }
        return null;
    }
}
