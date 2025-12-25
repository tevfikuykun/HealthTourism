package com.healthtourism.reservationservice.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Swagger/OpenAPI Configuration
 * 
 * Configures API documentation using OpenAPI 3.0 (Swagger).
 * Includes JWT authentication support.
 */
@Configuration
public class SwaggerConfig {
    
    @Value("${server.port:8080}")
    private String serverPort;
    
    @Value("${swagger.enabled:true}")
    private boolean swaggerEnabled;
    
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Health Tourism Reservation API")
                        .version("v1")
                        .description("Professional reservation management API for health tourism platform")
                        .contact(new Contact()
                                .name("Health Tourism Team")
                                .email("support@healthtourism.com")
                                .url("https://www.healthtourism.com"))
                        .license(new License()
                                .name("Proprietary")
                                .url("https://www.healthtourism.com/license")))
                .servers(List.of(
                        new Server().url("http://localhost:" + serverPort).description("Local Development"),
                        new Server().url("https://api.healthtourism.com").description("Production"),
                        new Server().url("https://staging-api.healthtourism.com").description("Staging")
                ))
                .components(new Components()
                        .addSecuritySchemes("bearer-jwt", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("JWT Authentication. Enter your JWT token (without 'Bearer' prefix)")))
                .addSecurityItem(new SecurityRequirement().addList("bearer-jwt"));
    }
}
