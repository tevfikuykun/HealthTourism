package com.healthtourism.eventsourcing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class EventSourcingApplication {
    public static void main(String[] args) {
        SpringApplication.run(EventSourcingApplication.class, args);
    }
}

