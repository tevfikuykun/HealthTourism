package com.healthtourism.aihealthcompanionservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class AIHealthCompanionServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(AIHealthCompanionServiceApplication.class, args);
    }
}
