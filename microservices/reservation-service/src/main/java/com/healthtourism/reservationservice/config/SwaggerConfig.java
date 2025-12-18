package com.healthtourism.reservationservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger/OpenAPI Configuration for Reservation Service
 */
@Configuration
public class SwaggerConfig {
    
    @Bean
    public OpenAPI reservationServiceOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Reservation Service API")
                .description("API documentation for Health Tourism Reservation Service. " +
                    "This service handles reservation creation, conflict checking, " +
                    "automatic price calculation, and reservation management.")
                .version("1.0.0")
                .contact(new Contact()
                    .name("Health Tourism Team")
                    .email("support@healthtourism.com")
                    .url("https://www.healthtourism.com"))
                .license(new License()
                    .name("Apache 2.0")
                    .url("https://www.apache.org/licenses/LICENSE-2.0.html")));
    }
}
