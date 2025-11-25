package com.healthtourism.reservationservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    
    @Bean
    public OpenAPI reservationServiceOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Reservation Service API")
                .description("Reservation management service for Health Tourism Platform")
                .version("1.0.0")
                .contact(new Contact()
                    .name("Health Tourism Team")
                    .email("support@healthtourism.com")));
    }
}

