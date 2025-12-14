package com.healthtourism.referralservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class ReferralServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ReferralServiceApplication.class, args);
    }
}

