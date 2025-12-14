package com.healthtourism.twofactorservice;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class TwoFactorServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(TwoFactorServiceApplication.class, args);
    }
}

