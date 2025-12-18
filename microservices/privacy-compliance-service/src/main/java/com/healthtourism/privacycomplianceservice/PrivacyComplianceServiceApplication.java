package com.healthtourism.privacycomplianceservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class PrivacyComplianceServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(PrivacyComplianceServiceApplication.class, args);
    }
}
