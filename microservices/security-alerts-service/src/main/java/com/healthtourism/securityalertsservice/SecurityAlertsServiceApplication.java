package com.healthtourism.securityalertsservice;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class SecurityAlertsServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(SecurityAlertsServiceApplication.class, args);
    }
}

