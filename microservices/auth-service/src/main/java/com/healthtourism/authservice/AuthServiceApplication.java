package com.healthtourism.authservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
// @EnableEurekaClient is not needed in Spring Cloud 2023.0.0 - auto-discovery is enabled by default

@SpringBootApplication
// @EnableEurekaClient
public class AuthServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(AuthServiceApplication.class, args);
    }
}

