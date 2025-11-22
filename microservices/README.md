# Health Tourism Microservices Architecture

Bu proje, İstanbul Sağlık Turizmi platformunu microservice mimarisinde oluşturur.

## Mimari Yapı

```
health-tourism/
├── eureka-server/          # Service Discovery
├── api-gateway/            # API Gateway (Spring Cloud Gateway)
├── config-server/          # Configuration Server
├── user-service/           # Kullanıcı yönetimi
├── hospital-service/       # Hastane yönetimi
├── doctor-service/         # Doktor yönetimi
├── accommodation-service/  # Konaklama
├── flight-service/         # Uçak bileti
├── car-rental-service/     # Araç kiralama
├── transfer-service/       # Transfer hizmetleri
├── package-service/        # Paket turlar
├── reservation-service/    # Rezervasyon yönetimi
├── payment-service/        # Ödeme işlemleri
├── notification-service/   # Bildirim servisi
├── medical-document-service/ # Tıbbi belge yönetimi
├── telemedicine-service/   # Online konsültasyon
├── patient-followup-service/ # Hasta takip
├── blog-service/           # Blog/Haberler
├── faq-service/            # SSS
├── favorite-service/       # Favoriler
├── appointment-calendar-service/ # Randevu takvimi
├── contact-service/        # İletişim
├── testimonial-service/    # Hasta hikayeleri
├── gallery-service/        # Fotoğraf galerisi
├── insurance-service/      # Sigorta hizmetleri
└── frontend/               # React Frontend
```

## Servis Portları

- Eureka Server: 8761
- API Gateway: 8080
- Config Server: 8888
- User Service: 8001
- Hospital Service: 8002
- Doctor Service: 8003
- Accommodation Service: 8004
- Flight Service: 8005
- Car Rental Service: 8006
- Transfer Service: 8007
- Package Service: 8008
- Reservation Service: 8009
- Payment Service: 8010
- Notification Service: 8011
- Medical Document Service: 8012
- Telemedicine Service: 8013
- Patient Follow-up Service: 8014
- Blog Service: 8015
- FAQ Service: 8016
- Favorite Service: 8017
- Appointment Calendar Service: 8018
- Contact Service: 8019
- Testimonial Service: 8020
- Gallery Service: 8021
- Insurance Service: 8022

## Çalıştırma

### Docker ile (Önerilen)
```bash
docker-compose up -d
```

### Manuel
1. Eureka Server'ı başlat
2. Config Server'ı başlat
3. Diğer servisleri başlat
4. API Gateway'i başlat
5. Frontend'i başlat

## Veritabanları

Her servis kendi veritabanına sahiptir:
- user_db
- hospital_db
- doctor_db
- accommodation_db
- flight_db
- car_rental_db
- transfer_db
- package_db
- reservation_db
- payment_db
- notification_db
- medical_document_db
- telemedicine_db
- patient_followup_db
- blog_db
- faq_db
- favorite_db
- appointment_calendar_db
- contact_db
- testimonial_db
- gallery_db
- insurance_db

