package com.healthtourism.gamificationservice;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class GamificationServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(GamificationServiceApplication.class, args);
    }
}

