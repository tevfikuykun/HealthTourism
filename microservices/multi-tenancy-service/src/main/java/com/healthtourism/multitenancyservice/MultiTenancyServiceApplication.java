package com.healthtourism.multitenancyservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class MultiTenancyServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(MultiTenancyServiceApplication.class, args);
    }
}
