#!/bin/bash

# Health Tourism Microservices Startup Script

echo "Starting Health Tourism Microservices..."

# Eureka Server
echo "Starting Eureka Server..."
cd eureka-server && mvn spring-boot:run > ../logs/eureka.log 2>&1 &
cd ..
sleep 10

# API Gateway
echo "Starting API Gateway..."
cd api-gateway && mvn spring-boot:run > ../logs/gateway.log 2>&1 &
cd ..
sleep 5

# All Microservices
echo "Starting all microservices..."
cd user-service && mvn spring-boot:run > ../logs/user.log 2>&1 & cd ..
cd hospital-service && mvn spring-boot:run > ../logs/hospital.log 2>&1 & cd ..
cd doctor-service && mvn spring-boot:run > ../logs/doctor.log 2>&1 & cd ..
cd accommodation-service && mvn spring-boot:run > ../logs/accommodation.log 2>&1 & cd ..
cd flight-service && mvn spring-boot:run > ../logs/flight.log 2>&1 & cd ..
cd car-rental-service && mvn spring-boot:run > ../logs/car-rental.log 2>&1 & cd ..
cd transfer-service && mvn spring-boot:run > ../logs/transfer.log 2>&1 & cd ..
cd package-service && mvn spring-boot:run > ../logs/package.log 2>&1 & cd ..
cd reservation-service && mvn spring-boot:run > ../logs/reservation.log 2>&1 & cd ..
cd payment-service && mvn spring-boot:run > ../logs/payment.log 2>&1 & cd ..
cd notification-service && mvn spring-boot:run > ../logs/notification.log 2>&1 & cd ..
cd medical-document-service && mvn spring-boot:run > ../logs/medical-document.log 2>&1 & cd ..
cd telemedicine-service && mvn spring-boot:run > ../logs/telemedicine.log 2>&1 & cd ..
cd patient-followup-service && mvn spring-boot:run > ../logs/patient-followup.log 2>&1 & cd ..
cd blog-service && mvn spring-boot:run > ../logs/blog.log 2>&1 & cd ..
cd faq-service && mvn spring-boot:run > ../logs/faq.log 2>&1 & cd ..
cd favorite-service && mvn spring-boot:run > ../logs/favorite.log 2>&1 & cd ..
cd appointment-calendar-service && mvn spring-boot:run > ../logs/appointment-calendar.log 2>&1 & cd ..
cd contact-service && mvn spring-boot:run > ../logs/contact.log 2>&1 & cd ..
cd testimonial-service && mvn spring-boot:run > ../logs/testimonial.log 2>&1 & cd ..
cd gallery-service && mvn spring-boot:run > ../logs/gallery.log 2>&1 & cd ..
cd insurance-service && mvn spring-boot:run > ../logs/insurance.log 2>&1 & cd ..
cd auth-service && mvn spring-boot:run > ../logs/auth.log 2>&1 & cd ..
cd monitoring-service && mvn spring-boot:run > ../logs/monitoring.log 2>&1 & cd ..
cd logging-service && mvn spring-boot:run > ../logs/logging.log 2>&1 & cd ..
cd file-storage-service && mvn spring-boot:run > ../logs/file-storage.log 2>&1 & cd ..
cd config-server && mvn spring-boot:run > ../logs/config-server.log 2>&1 & cd ..
cd admin-service && mvn spring-boot:run > ../logs/admin.log 2>&1 & cd ..
cd search-service && mvn spring-boot:run > ../logs/search.log 2>&1 & cd ..
cd integration-service && mvn spring-boot:run > ../logs/integration.log 2>&1 & cd ..

echo ""
echo "========================================"
echo "All services are starting!"
echo "========================================"
echo "Eureka Dashboard: http://localhost:8761"
echo "API Gateway: http://localhost:8080"
echo "Config Server: http://localhost:8888"
echo "Frontend: http://localhost:3000"
echo "Search Service: http://localhost:8031"
echo "Integration Service: http://localhost:8030"
echo "RabbitMQ Management: http://localhost:15672 (admin/admin)"
echo "Redis: localhost:6379"
echo "Grafana: http://localhost:3001 (admin/admin)"
echo "Kibana: http://localhost:5601"
echo "Kafka UI: http://localhost:8081"
echo "Keycloak: http://localhost:8180 (admin/admin)"
echo "Vault: http://localhost:8200"
echo ""
echo "Total: 33 microservices"
echo ""
echo "Database Migration:"
echo "- PostgreSQL: Core services (user, hospital, doctor, reservation, payment)"
echo "- MongoDB: Document services (medical-document, blog, gallery)"
echo "- Elasticsearch: Search service"
echo ""

