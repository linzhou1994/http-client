package com.http.client;

import com.biz.tool.spring.SpringConfig;
import com.http.client.annotation.EnableHttpClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@EnableHttpClient
@ComponentScan(basePackages = {"com.http"})
@Import(SpringConfig.class)
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
