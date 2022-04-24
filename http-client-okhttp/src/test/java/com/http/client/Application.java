package com.http.client;


import com.http.client.annotation.EnableHttpClient;
import com.http.client.okhttp.proxy.OkHttpProxy;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableHttpClient(defaultProxy = OkHttpProxy.class)
@ComponentScan(basePackages = {"com.http.controller"})
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
