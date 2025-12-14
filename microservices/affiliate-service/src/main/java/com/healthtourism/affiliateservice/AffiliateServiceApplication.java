package com.healthtourism.affiliateservice;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class AffiliateServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(AffiliateServiceApplication.class, args);
    }
}

