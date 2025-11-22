package com.healthtourism.patientfollowupservice;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
@SpringBootApplication
@EnableEurekaClient
public class PatientFollowupServiceApplication {
    public static void main(String[] args) { SpringApplication.run(PatientFollowupServiceApplication.class, args); }
}

