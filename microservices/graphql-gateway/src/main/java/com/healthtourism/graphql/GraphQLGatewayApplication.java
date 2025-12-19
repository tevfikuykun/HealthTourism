package com.healthtourism.graphql;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class GraphQLGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(GraphQLGatewayApplication.class, args);
    }
}

