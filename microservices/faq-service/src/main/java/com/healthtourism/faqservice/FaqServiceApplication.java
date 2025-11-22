package com.healthtourism.faqservice;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
@SpringBootApplication
@EnableEurekaClient
public class FaqServiceApplication {
    public static void main(String[] args) { SpringApplication.run(FaqServiceApplication.class, args); }
}

