package com.healthtourism.localguideservice;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class LocalGuideServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(LocalGuideServiceApplication.class, args);
    }
}

