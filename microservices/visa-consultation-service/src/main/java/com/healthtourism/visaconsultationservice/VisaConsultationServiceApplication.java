package com.healthtourism.visaconsultationservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class VisaConsultationServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(VisaConsultationServiceApplication.class, args);
    }
}

