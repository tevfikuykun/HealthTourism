package com.healthtourism.testimonialservice;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
@SpringBootApplication
@EnableEurekaClient
public class TestimonialServiceApplication {
    public static void main(String[] args) { SpringApplication.run(TestimonialServiceApplication.class, args); }
}

