package com.healthtourism.jpa.config.swagger;

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
 * OpenAPI/Swagger Configuration
 * 
 * Provides comprehensive API documentation with:
 * - API information (title, version, description)
 * - Contact information
 * - Server URLs (local, staging, production)
 * - Security scheme (JWT Bearer token)
 * - License information
 */
@Configuration
public class OpenApiConfig {
    
    @Value("${server.port:8080}")
    private String serverPort;
    
    @Value("${spring.application.name:health-tourism-service}")
    private String applicationName;
    
    @Value("${springdoc.api-docs.path:/api-docs}")
    private String apiDocsPath;
    
    @Value("${springdoc.swagger-ui.path:/swagger-ui.html}")
    private String swaggerUiPath;
    
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Health Tourism API - " + applicationName)
                .version("1.0.0")
                .description("""
                    Health Tourism Platform API Documentation
                    
                    This API provides endpoints for managing health tourism services including:
                    - Patient management
                    - Appointment scheduling
                    - Hospital and doctor information
                    - Reservation management
                    
                    All endpoints require authentication using JWT Bearer token.
                    """)
                .contact(new Contact()
                    .name("Health Tourism Development Team")
                    .email("api@healthtourism.com")
                    .url("https://www.healthtourism.com"))
                .license(new License()
                    .name("Apache 2.0")
                    .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
            .servers(List.of(
                new Server()
                    .url("http://localhost:" + serverPort)
                    .description("Local Development Server"),
                new Server()
                    .url("https://api-dev.healthtourism.com")
                    .description("Development Server"),
                new Server()
                    .url("https://api-staging.healthtourism.com")
                    .description("Staging Server"),
                new Server()
                    .url("https://api.healthtourism.com")
                    .description("Production Server")
            ))
            .components(new Components()
                .addSecuritySchemes("bearer-jwt", new SecurityScheme()
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")
                    .in(SecurityScheme.In.HEADER)
                    .name("Authorization")
                    .description("JWT token obtained from /api/auth/login endpoint")))
            .addSecurityItem(new SecurityRequirement().addList("bearer-jwt"));
    }
}

