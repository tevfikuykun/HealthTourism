package com.healthtourism.securityauditservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class SecurityAuditServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(SecurityAuditServiceApplication.class, args);
    }
}
