package com.healthtourism.livechatservice;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class LiveChatServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(LiveChatServiceApplication.class, args);
    }
}

