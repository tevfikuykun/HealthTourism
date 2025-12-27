package com.example.HealthTourism.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Swagger/OpenAPI Configuration
 * API dokümantasyonu için Swagger UI yapılandırması
 * 
 * JWT Security Entegrasyonu:
 * - Bearer token authentication desteği
 * - Swagger UI'da "Authorize" butonu ile token girişi
 * 
 * Kullanım:
 * - Swagger UI: http://localhost:8080/swagger-ui.html
 * - API Docs JSON: http://localhost:8080/api-docs
 */
@Configuration
public class SwaggerConfig {
    
    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";
        
        return new OpenAPI()
                .info(new Info()
                        .title("Health Tourism API")
                        .version("v1.0.0")
                        .description("Sağlık Turizmi Yönetim Sistemi API Dokümantasyonu\n\n" +
                                "Bu API, sağlık turizmi platformu için geliştirilmiştir.\n" +
                                "Hastane, doktor, konaklama, uçuş ve transfer hizmetlerinin yönetimini sağlar.\n\n" +
                                "**Güvenlik:**\n" +
                                "- JWT token ile kimlik doğrulama\n" +
                                "- Role-based access control (RBAC)\n" +
                                "- Tüm endpoint'ler için detaylı yetkilendirme\n\n" +
                                "**Kullanım:**\n" +
                                "1. `/api/v1/auth/register` veya `/api/v1/auth/login` ile token alın\n" +
                                "2. Sağ üstteki \"Authorize\" butonuna tıklayın\n" +
                                "3. Token'ı \"Bearer {token}\" formatında girin\n" +
                                "4. Artık korumalı endpoint'leri test edebilirsiniz")
                        .contact(new Contact()
                                .name("Health Tourism Team")
                                .email("support@healthtourism.com")
                                .url("https://www.healthtourism.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Development Server"),
                        new Server()
                                .url("https://api.healthtourism.com")
                                .description("Production Server")
                ))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("JWT token ile kimlik doğrulama. " +
                                                "Token'ı 'Bearer {token}' formatında girin.")));
    }
}

