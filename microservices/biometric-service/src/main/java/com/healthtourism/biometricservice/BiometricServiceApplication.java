package com.healthtourism.biometricservice;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class BiometricServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(BiometricServiceApplication.class, args);
    }
}

