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
cd comparison-service && mvn spring-boot:run > ../logs/comparison.log 2>&1 & cd ..
cd analytics-service && mvn spring-boot:run > ../logs/analytics.log 2>&1 & cd ..
cd health-records-service && mvn spring-boot:run > ../logs/health-records.log 2>&1 & cd ..
cd medication-service && mvn spring-boot:run > ../logs/medication.log 2>&1 & cd ..
cd referral-service && mvn spring-boot:run > ../logs/referral.log 2>&1 & cd ..
cd coupon-service && mvn spring-boot:run > ../logs/coupon.log 2>&1 & cd ..
cd installment-service && mvn spring-boot:run > ../logs/installment.log 2>&1 & cd ..
cd crypto-payment-service && mvn spring-boot:run > ../logs/crypto-payment.log 2>&1 & cd ..
cd waiting-list-service && mvn spring-boot:run > ../logs/waiting-list.log 2>&1 & cd ..
cd bulk-reservation-service && mvn spring-boot:run > ../logs/bulk-reservation.log 2>&1 & cd ..
cd calendar-service && mvn spring-boot:run > ../logs/calendar.log 2>&1 & cd ..
cd two-factor-service && mvn spring-boot:run > ../logs/two-factor.log 2>&1 & cd ..
cd biometric-service && mvn spring-boot:run > ../logs/biometric.log 2>&1 & cd ..
cd security-alerts-service && mvn spring-boot:run > ../logs/security-alerts.log 2>&1 & cd ..
cd local-guide-service && mvn spring-boot:run > ../logs/local-guide.log 2>&1 & cd ..
cd weather-service && mvn spring-boot:run > ../logs/weather.log 2>&1 & cd ..
cd loyalty-service && mvn spring-boot:run > ../logs/loyalty.log 2>&1 & cd ..
cd ai-recommendation-service && mvn spring-boot:run > ../logs/ai-recommendation.log 2>&1 & cd ..
cd video-consultation-service && mvn spring-boot:run > ../logs/video-consultation.log 2>&1 & cd ..
cd forum-service && mvn spring-boot:run > ../logs/forum.log 2>&1 & cd ..
cd invoice-service && mvn spring-boot:run > ../logs/invoice.log 2>&1 & cd ..
cd gdpr-service && mvn spring-boot:run > ../logs/gdpr.log 2>&1 & cd ..
cd currency-service && mvn spring-boot:run > ../logs/currency.log 2>&1 & cd ..
cd tax-service && mvn spring-boot:run > ../logs/tax.log 2>&1 & cd ..

echo ""
echo "========================================"
echo "All services are starting!"
echo "========================================"
echo "Eureka Dashboard: http://localhost:8761"
echo "API Gateway: http://localhost:8080"
echo "Config Server: http://localhost:8888"
echo "Frontend: http://localhost:3000"
echo "RabbitMQ Management: http://localhost:15672 (admin/admin)"
echo "Redis: localhost:6379"
echo ""
echo "Total: 58 microservices"
echo ""
echo "Database Migration:"
echo "- PostgreSQL: Core services (user, hospital, doctor, reservation, payment)"
echo "- MongoDB: Document services (medical-document, blog, gallery)"
echo "- Elasticsearch: Search service"
echo ""

