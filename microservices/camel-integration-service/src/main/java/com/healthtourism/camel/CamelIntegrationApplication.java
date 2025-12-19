package com.healthtourism.camel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class CamelIntegrationApplication {
    public static void main(String[] args) {
        SpringApplication.run(CamelIntegrationApplication.class, args);
    }
}

