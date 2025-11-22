#!/bin/bash
# Bu script tüm servislerin temel yapısını oluşturur
# Her servis için: pom.xml, Application class, application.properties, temel entity/repository/service/controller

echo "Creating all microservices..."

# Servis listesi ve portları
declare -A services=(
    ["accommodation-service"]="8004:3310"
    ["flight-service"]="8005:3311"
    ["car-rental-service"]="8006:3312"
    ["transfer-service"]="8007:3313"
    ["package-service"]="8008:3314"
    ["reservation-service"]="8009:3315"
    ["medical-document-service"]="8012:3318"
    ["telemedicine-service"]="8013:3319"
    ["patient-followup-service"]="8014:3320"
    ["blog-service"]="8015:3321"
    ["faq-service"]="8016:3322"
    ["favorite-service"]="8017:3323"
    ["appointment-calendar-service"]="8018:3324"
    ["contact-service"]="8019:3325"
    ["testimonial-service"]="8020:3326"
    ["gallery-service"]="8021:3327"
    ["insurance-service"]="8022:3328"
)

for service in "${!services[@]}"; do
    IFS=':' read -r port db_port <<< "${services[$service]}"
    service_name=$(echo $service | sed 's/-service//')
    service_package=$(echo $service_name | sed 's/-//g')
    
    echo "Creating $service..."
    
    mkdir -p "$service/src/main/java/com/healthtourism/${service_package}service"
    mkdir -p "$service/src/main/java/com/healthtourism/${service_package}service/entity"
    mkdir -p "$service/src/main/java/com/healthtourism/${service_package}service/repository"
    mkdir -p "$service/src/main/java/com/healthtourism/${service_package}service/service"
    mkdir -p "$service/src/main/java/com/healthtourism/${service_package}service/controller"
    mkdir -p "$service/src/main/java/com/healthtourism/${service_package}service/dto"
    mkdir -p "$service/src/main/resources"
done

echo "All service directories created!"

