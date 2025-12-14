package com.healthtourism.gdprservice;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class GDPRServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(GDPRServiceApplication.class, args);
    }
}

