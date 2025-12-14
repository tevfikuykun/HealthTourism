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
start "Comparison Service" cmd /k "cd comparison-service && mvn spring-boot:run"
start "Analytics Service" cmd /k "cd analytics-service && mvn spring-boot:run"
start "Health Records Service" cmd /k "cd health-records-service && mvn spring-boot:run"
start "Medication Service" cmd /k "cd medication-service && mvn spring-boot:run"
start "Referral Service" cmd /k "cd referral-service && mvn spring-boot:run"
start "Coupon Service" cmd /k "cd coupon-service && mvn spring-boot:run"
start "Installment Service" cmd /k "cd installment-service && mvn spring-boot:run"
start "Crypto Payment Service" cmd /k "cd crypto-payment-service && mvn spring-boot:run"
start "Waiting List Service" cmd /k "cd waiting-list-service && mvn spring-boot:run"
start "Bulk Reservation Service" cmd /k "cd bulk-reservation-service && mvn spring-boot:run"
start "Calendar Service" cmd /k "cd calendar-service && mvn spring-boot:run"
start "Two Factor Service" cmd /k "cd two-factor-service && mvn spring-boot:run"
start "Biometric Service" cmd /k "cd biometric-service && mvn spring-boot:run"
start "Security Alerts Service" cmd /k "cd security-alerts-service && mvn spring-boot:run"
start "Local Guide Service" cmd /k "cd local-guide-service && mvn spring-boot:run"
start "Weather Service" cmd /k "cd weather-service && mvn spring-boot:run"
start "Loyalty Service" cmd /k "cd loyalty-service && mvn spring-boot:run"
start "AI Recommendation Service" cmd /k "cd ai-recommendation-service && mvn spring-boot:run"
start "Video Consultation Service" cmd /k "cd video-consultation-service && mvn spring-boot:run"
start "Forum Service" cmd /k "cd forum-service && mvn spring-boot:run"
start "Invoice Service" cmd /k "cd invoice-service && mvn spring-boot:run"
start "GDPR Service" cmd /k "cd gdpr-service && mvn spring-boot:run"
start "Currency Service" cmd /k "cd currency-service && mvn spring-boot:run"
start "Tax Service" cmd /k "cd tax-service && mvn spring-boot:run"

echo.
echo ========================================
echo All services are starting!
echo ========================================
echo Eureka Dashboard: http://localhost:8761
echo API Gateway: http://localhost:8080
echo Config Server: http://localhost:8888
echo Frontend: http://localhost:3000
echo RabbitMQ Management: http://localhost:15672 (admin/admin)
echo Redis: localhost:6379
echo.
echo Total: 58 microservices
echo.
echo Database Migration:
echo - PostgreSQL: Core services (user, hospital, doctor, reservation, payment)
echo - MongoDB: Document services (medical-document, blog, gallery)
echo - Elasticsearch: Search service
echo.

pause

