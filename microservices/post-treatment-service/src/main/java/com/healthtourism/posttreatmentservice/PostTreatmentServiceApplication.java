package com.healthtourism.posttreatmentservice;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class PostTreatmentServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(PostTreatmentServiceApplication.class, args);
    }
}

