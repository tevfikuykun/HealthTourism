package com.healthtourism.virtualtourservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class VirtualTourServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(VirtualTourServiceApplication.class, args);
    }
}
