package com.healthtourism.paymentservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    
    @Bean
    public OpenAPI paymentServiceOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Payment Service API")
                .description("Payment processing and management service for Health Tourism Platform")
                .version("1.0.0")
                .contact(new Contact()
                    .name("Health Tourism Team")
                    .email("support@healthtourism.com")));
    }
}

