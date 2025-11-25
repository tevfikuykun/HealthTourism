package com.healthtourism.common.swagger;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Value("${swagger.server.url:http://localhost:8080}")
    private String serverUrl;

    @Bean
    public OpenAPI healthTourismOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Health Tourism API")
                        .description("Comprehensive Health Tourism Platform API Documentation")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Health Tourism Support")
                                .email("support@healthtourism.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
                .servers(List.of(
                        new Server().url(serverUrl).description("API Gateway Server"),
                        new Server().url("http://localhost:8080").description("Local Development Server")
                ));
    }
}

