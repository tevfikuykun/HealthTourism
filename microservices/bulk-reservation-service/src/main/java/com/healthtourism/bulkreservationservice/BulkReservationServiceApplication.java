package com.healthtourism.bulkreservationservice;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class BulkReservationServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(BulkReservationServiceApplication.class, args);
    }
}

