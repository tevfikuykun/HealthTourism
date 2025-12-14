package com.healthtourism.socialmediaservice;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class SocialMediaServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(SocialMediaServiceApplication.class, args);
    }
}

