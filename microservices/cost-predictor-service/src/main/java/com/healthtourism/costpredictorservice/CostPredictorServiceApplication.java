package com.healthtourism.costpredictorservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class CostPredictorServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(CostPredictorServiceApplication.class, args);
    }
}
