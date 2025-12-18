package com.healthtourism.camelintegration;

import org.apache.camel.spring.boot.CamelSpringBootApplicationController;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class CamelIntegrationServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(CamelIntegrationServiceApplication.class, args);
    }
}
