package com.healthtourism.chaosengineeringservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class ChaosEngineeringServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ChaosEngineeringServiceApplication.class, args);
    }
}
