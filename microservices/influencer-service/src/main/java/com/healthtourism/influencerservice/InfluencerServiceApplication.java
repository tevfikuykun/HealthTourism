package com.healthtourism.influencerservice;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class InfluencerServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(InfluencerServiceApplication.class, args);
    }
}

