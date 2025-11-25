#!/usr/bin/env python3
"""
Script to create Dockerfile and Swagger config for all microservices
"""

services = [
    ("eureka-server", 8761, "Eureka Server"),
    ("api-gateway", 8080, "API Gateway"),
    ("user-service", 8001, "User Service"),
    ("hospital-service", 8002, "Hospital Service"),
    ("doctor-service", 8003, "Doctor Service"),
    ("accommodation-service", 8004, "Accommodation Service"),
    ("flight-service", 8005, "Flight Service"),
    ("car-rental-service", 8006, "Car Rental Service"),
    ("transfer-service", 8007, "Transfer Service"),
    ("package-service", 8008, "Package Service"),
    ("reservation-service", 8009, "Reservation Service"),
    ("payment-service", 8010, "Payment Service"),
    ("notification-service", 8011, "Notification Service"),
    ("medical-document-service", 8012, "Medical Document Service"),
    ("telemedicine-service", 8013, "Telemedicine Service"),
    ("patient-followup-service", 8014, "Patient Follow-up Service"),
    ("blog-service", 8015, "Blog Service"),
    ("faq-service", 8016, "FAQ Service"),
    ("favorite-service", 8017, "Favorite Service"),
    ("appointment-calendar-service", 8018, "Appointment Calendar Service"),
    ("contact-service", 8019, "Contact Service"),
    ("testimonial-service", 8020, "Testimonial Service"),
    ("gallery-service", 8021, "Gallery Service"),
    ("insurance-service", 8022, "Insurance Service"),
    ("visa-consultation-service", 8024, "Visa Consultation Service"),
    ("translation-service", 8025, "Translation Service"),
    ("currency-conversion-service", 8026, "Currency Conversion Service"),
    ("chat-service", 8027, "Chat Service"),
    ("promotion-service", 8028, "Promotion Service"),
]

dockerfile_template = """# Multi-stage build for {service_name}
FROM maven:3.9-eclipse-temurin-25 AS build

WORKDIR /app

# Copy pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build
COPY src ./src
RUN mvn clean package -DskipTests -B

# Runtime stage
FROM eclipse-temurin:25-jre-alpine

WORKDIR /app

# Copy jar from build stage
COPY --from=build /app/target/*.jar app.jar

# Add non-root user
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \\
  CMD wget --no-verbose --tries=1 --spider http://localhost:{port}/actuator/health || exit 1

EXPOSE {port}

ENTRYPOINT ["java", "-jar", "app.jar"]
"""

swagger_template = """package com.healthtourism.{package_name}.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {{
    
    @Bean
    public OpenAPI {service_var}OpenAPI() {{
        return new OpenAPI()
            .info(new Info()
                .title("{service_name} API")
                .description("{description} for Health Tourism Platform")
                .version("1.0.0")
                .contact(new Contact()
                    .name("Health Tourism Team")
                    .email("support@healthtourism.com")));
    }}
}}
"""

def get_package_name(service_name):
    """Convert service name to package name"""
    return service_name.replace("-", "")

def create_dockerfile(service_dir, service_name, port):
    """Create Dockerfile for a service"""
    import os
    dockerfile_path = os.path.join(service_dir, "Dockerfile")
    
    # Check if Dockerfile already exists
    if os.path.exists(dockerfile_path):
        print(f"Skipping {service_name} - Dockerfile already exists")
        return False
    
    content = dockerfile_template.format(
        service_name=service_name.replace("-", " ").title(),
        port=port
    )
    
    with open(dockerfile_path, 'w') as f:
        f.write(content)
    
    print(f"Created Dockerfile for {service_name}")
    return True

def create_swagger_config(service_dir, service_name):
    """Create Swagger config for a service"""
    import os
    
    package_name = get_package_name(service_name)
    service_var = package_name.replace("service", "")
    
    config_dir = os.path.join(service_dir, "src", "main", "java", "com", "healthtourism", package_name, "config")
    config_path = os.path.join(config_dir, "SwaggerConfig.java")
    
    # Check if SwaggerConfig already exists
    if os.path.exists(config_path):
        print(f"Skipping {service_name} - SwaggerConfig already exists")
        return False
    
    # Create directory if it doesn't exist
    os.makedirs(config_dir, exist_ok=True)
    
    description = service_name.replace("-", " ").title()
    
    content = swagger_template.format(
        package_name=package_name,
        service_var=service_var,
        service_name=service_name.replace("-", " ").title(),
        description=description
    )
    
    with open(config_path, 'w') as f:
        f.write(content)
    
    print(f"Created SwaggerConfig for {service_name}")
    return True

def main():
    import os
    import sys
    
    base_dir = os.path.dirname(os.path.abspath(__file__))
    
    created_count = 0
    
    for service_name, port, full_name in services:
        service_dir = os.path.join(base_dir, service_name)
        
        if not os.path.exists(service_dir):
            print(f"Warning: {service_dir} does not exist, skipping...")
            continue
        
        # Create Dockerfile
        if create_dockerfile(service_dir, service_name, port):
            created_count += 1
        
        # Create Swagger config
        if create_swagger_config(service_dir, service_name):
            created_count += 1
    
    print(f"\nCompleted! Created/updated {created_count} files.")

if __name__ == "__main__":
    main()

