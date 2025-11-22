package com.healthtourism.filestorageservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class FileStorageServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(FileStorageServiceApplication.class, args);
    }
}

