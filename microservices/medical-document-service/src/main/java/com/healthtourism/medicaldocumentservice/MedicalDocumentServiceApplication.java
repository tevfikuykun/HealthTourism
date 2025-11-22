package com.healthtourism.medicaldocumentservice;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
@SpringBootApplication
@EnableEurekaClient
public class MedicalDocumentServiceApplication {
    public static void main(String[] args) { SpringApplication.run(MedicalDocumentServiceApplication.class, args); }
}

