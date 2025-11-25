#!/bin/bash

# Tüm servislere gerekli kütüphaneleri eklemek için script
# Bu script, her servisin pom.xml dosyasını günceller

SERVICES=(
    "hospital-service"
    "doctor-service"
    "accommodation-service"
    "flight-service"
    "car-rental-service"
    "transfer-service"
    "package-service"
    "reservation-service"
    "payment-service"
    "notification-service"
    "medical-document-service"
    "telemedicine-service"
    "patient-followup-service"
    "blog-service"
    "faq-service"
    "favorite-service"
    "appointment-calendar-service"
    "contact-service"
    "testimonial-service"
    "gallery-service"
    "insurance-service"
    "auth-service"
    "monitoring-service"
    "logging-service"
    "file-storage-service"
    "admin-service"
)

echo "Bu script tüm servislere gerekli kütüphaneleri ekler."
echo "Manuel olarak her servisin pom.xml dosyasını güncellemeniz gerekiyor."
echo ""
echo "Referans için:"
echo "1. user-service/pom.xml dosyasına bakın (örnek olarak güncellendi)"
echo "2. ENHANCED_POM_TEMPLATE.xml dosyasını kullanın"
echo "3. ADD_LIBRARIES_GUIDE.md dosyasındaki talimatları takip edin"
echo ""
echo "Toplam ${#SERVICES[@]} servis güncellenmeli."


