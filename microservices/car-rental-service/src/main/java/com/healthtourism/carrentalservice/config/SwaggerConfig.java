package com.healthtourism.carrentalservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI carRentalServiceOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Car Rental Service API")
                .description("API documentation for Health Tourism Car Rental Service")
                .version("1.0.0")
                .contact(new Contact()
                    .name("Health Tourism Team")
                    .email("support@healthtourism.com"))
                .license(new License()
                    .name("Apache 2.0")
                    .url("https://www.apache.org/licenses/LICENSE-2.0.html")));
    }
}
