package com.healthtourism.dashboard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class IntegrationDashboardApplication {
    public static void main(String[] args) {
        SpringApplication.run(IntegrationDashboardApplication.class, args);
    }
}

