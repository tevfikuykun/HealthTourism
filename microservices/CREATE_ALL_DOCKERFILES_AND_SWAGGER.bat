@echo off
REM This script creates Dockerfile and Swagger config for all services

echo Creating Dockerfiles and Swagger configs for all services...

REM Service list with port mappings
set services=eureka-server:8761,api-gateway:8080,user-service:8001,hospital-service:8002,doctor-service:8003,accommodation-service:8004,flight-service:8005,car-rental-service:8006,transfer-service:8007,package-service:8008,reservation-service:8009,payment-service:8010,notification-service:8011,medical-document-service:8012,telemedicine-service:8013,patient-followup-service:8014,blog-service:8015,faq-service:8016,favorite-service:8017,appointment-calendar-service:8018,contact-service:8019,testimonial-service:8020,gallery-service:8021,insurance-service:8022,auth-service:8023,visa-consultation-service:8024,translation-service:8025,currency-conversion-service:8026,chat-service:8027,promotion-service:8028

echo Done! Please check each service directory.

