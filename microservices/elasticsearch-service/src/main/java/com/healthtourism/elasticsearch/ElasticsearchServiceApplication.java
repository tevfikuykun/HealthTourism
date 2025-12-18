package com.healthtourism.elasticsearch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class ElasticsearchServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ElasticsearchServiceApplication.class, args);
    }
}
