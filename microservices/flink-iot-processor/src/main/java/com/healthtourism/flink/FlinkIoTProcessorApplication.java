package com.healthtourism.flink;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class FlinkIoTProcessorApplication {
    public static void main(String[] args) {
        SpringApplication.run(FlinkIoTProcessorApplication.class, args);
    }
}






