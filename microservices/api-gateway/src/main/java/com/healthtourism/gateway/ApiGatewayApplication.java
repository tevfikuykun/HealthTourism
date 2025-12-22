package com.healthtourism.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
// @EnableEurekaClient is not needed in Spring Cloud 2023.0.0 - auto-discovery is enabled by default

@SpringBootApplication
// @EnableEurekaClient is not needed in Spring Cloud 2023.0.0 - auto-discovery is enabled by default
public class ApiGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }
}

