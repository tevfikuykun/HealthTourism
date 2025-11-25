package com.healthtourism.translationservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class TranslationServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(TranslationServiceApplication.class, args);
    }
}

