package com.healthtourism.waitinglistservice;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class WaitingListServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(WaitingListServiceApplication.class, args);
    }
}

