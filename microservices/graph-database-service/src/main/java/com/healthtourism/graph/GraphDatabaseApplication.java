package com.healthtourism.graph;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;

@SpringBootApplication
@EnableEurekaClient
@EnableNeo4jRepositories
public class GraphDatabaseApplication {
    public static void main(String[] args) {
        SpringApplication.run(GraphDatabaseApplication.class, args);
    }
}


