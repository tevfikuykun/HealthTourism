package com.healthtourism.videoconsultationservice;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class VideoConsultationServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(VideoConsultationServiceApplication.class, args);
    }
}

