package com.healthtourism.hospitalservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Hospital Service API")
                .version("1.0.0")
                .description("Health Tourism Platform - Hospital Management Service API Documentation")
                .contact(new Contact()
                    .name("Health Tourism Team")
                    .email("info@healthtourism.com")));
    }
}


