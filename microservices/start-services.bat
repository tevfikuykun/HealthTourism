@echo off
echo Starting Health Tourism Microservices...

echo Starting Eureka Server...
start "Eureka Server" cmd /k "cd eureka-server && mvn spring-boot:run"
timeout /t 10

echo Starting API Gateway...
start "API Gateway" cmd /k "cd api-gateway && mvn spring-boot:run"
timeout /t 5

echo Starting all microservices...
start "User Service" cmd /k "cd user-service && mvn spring-boot:run"
start "Hospital Service" cmd /k "cd hospital-service && mvn spring-boot:run"
start "Doctor Service" cmd /k "cd doctor-service && mvn spring-boot:run"
start "Accommodation Service" cmd /k "cd accommodation-service && mvn spring-boot:run"
start "Flight Service" cmd /k "cd flight-service && mvn spring-boot:run"
start "Car Rental Service" cmd /k "cd car-rental-service && mvn spring-boot:run"
start "Transfer Service" cmd /k "cd transfer-service && mvn spring-boot:run"
start "Package Service" cmd /k "cd package-service && mvn spring-boot:run"
start "Reservation Service" cmd /k "cd reservation-service && mvn spring-boot:run"
start "Payment Service" cmd /k "cd payment-service && mvn spring-boot:run"
start "Notification Service" cmd /k "cd notification-service && mvn spring-boot:run"
start "Medical Document Service" cmd /k "cd medical-document-service && mvn spring-boot:run"
start "Telemedicine Service" cmd /k "cd telemedicine-service && mvn spring-boot:run"
start "Patient Follow-up Service" cmd /k "cd patient-followup-service && mvn spring-boot:run"
start "Blog Service" cmd /k "cd blog-service && mvn spring-boot:run"
start "FAQ Service" cmd /k "cd faq-service && mvn spring-boot:run"
start "Favorite Service" cmd /k "cd favorite-service && mvn spring-boot:run"
start "Appointment Calendar Service" cmd /k "cd appointment-calendar-service && mvn spring-boot:run"
start "Contact Service" cmd /k "cd contact-service && mvn spring-boot:run"
start "Testimonial Service" cmd /k "cd testimonial-service && mvn spring-boot:run"
start "Gallery Service" cmd /k "cd gallery-service && mvn spring-boot:run"
start "Insurance Service" cmd /k "cd insurance-service && mvn spring-boot:run"
start "Auth Service" cmd /k "cd auth-service && mvn spring-boot:run"
start "Monitoring Service" cmd /k "cd monitoring-service && mvn spring-boot:run"
start "Logging Service" cmd /k "cd logging-service && mvn spring-boot:run"
start "File Storage Service" cmd /k "cd file-storage-service && mvn spring-boot:run"
start "Config Server" cmd /k "cd config-server && mvn spring-boot:run"
start "Admin Service" cmd /k "cd admin-service && mvn spring-boot:run"
start "Search Service" cmd /k "cd search-service && mvn spring-boot:run"
start "Integration Service" cmd /k "cd integration-service && mvn spring-boot:run"

echo.
echo ========================================
echo All services are starting!
echo ========================================
echo Eureka Dashboard: http://localhost:8761
echo API Gateway: http://localhost:8080
echo Config Server: http://localhost:8888
echo Frontend: http://localhost:3000
echo Search Service: http://localhost:8031
echo Integration Service: http://localhost:8030
echo RabbitMQ Management: http://localhost:15672 (admin/admin)
echo Redis: localhost:6379
echo Grafana: http://localhost:3001 (admin/admin)
echo Kibana: http://localhost:5601
echo Kafka UI: http://localhost:8081
echo Keycloak: http://localhost:8180 (admin/admin)
echo Vault: http://localhost:8200
echo.
echo Total: 33 microservices
echo.
echo Database Migration:
echo - PostgreSQL: Core services (user, hospital, doctor, reservation, payment)
echo - MongoDB: Document services (medical-document, blog, gallery)
echo - Elasticsearch: Search service
echo.

pause

