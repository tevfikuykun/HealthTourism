package com.healthtourism.whatsappservice;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class WhatsAppServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(WhatsAppServiceApplication.class, args);
    }
}

