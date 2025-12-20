package com.healthtourism.migration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class DataMigrationApplication {
    public static void main(String[] args) {
        SpringApplication.run(DataMigrationApplication.class, args);
    }
}



