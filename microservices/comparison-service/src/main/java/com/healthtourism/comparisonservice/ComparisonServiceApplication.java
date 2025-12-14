package com.healthtourism.comparisonservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class ComparisonServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ComparisonServiceApplication.class, args);
    }
}

