# Hızlı Kurulum - Tüm Servisler

Tüm servisler için aynı pattern kullanılıyor. Her servis için:

1. pom.xml (User Service'deki gibi)
2. Application class (@SpringBootApplication, @EnableEurekaClient)
3. application.properties (port, eureka, database)
4. Entity, Repository, DTO, Service, Controller

## Servis Listesi ve Portları

| Servis | Port | DB Port | Veritabanı |
|--------|------|---------|------------|
| User Service | 8001 | 3307 | user_db |
| Hospital Service | 8002 | 3308 | hospital_db |
| Doctor Service | 8003 | 3309 | doctor_db |
| Accommodation Service | 8004 | 3310 | accommodation_db |
| Flight Service | 8005 | 3311 | flight_db |
| Car Rental Service | 8006 | 3312 | car_rental_db |
| Transfer Service | 8007 | 3313 | transfer_db |
| Package Service | 8008 | 3314 | package_db |
| Reservation Service | 8009 | 3315 | reservation_db |
| Payment Service | 8010 | 3316 | payment_db |
| Notification Service | 8011 | 3317 | notification_db |
| Medical Document Service | 8012 | 3318 | medical_document_db |
| Telemedicine Service | 8013 | 3319 | telemedicine_db |
| Patient Follow-up Service | 8014 | 3320 | patient_followup_db |
| Blog Service | 8015 | 3321 | blog_db |
| FAQ Service | 8016 | 3322 | faq_db |
| Favorite Service | 8017 | 3323 | favorite_db |
| Appointment Calendar Service | 8018 | 3324 | appointment_calendar_db |
| Contact Service | 8019 | 3325 | contact_db |
| Testimonial Service | 8020 | 3326 | testimonial_db |
| Gallery Service | 8021 | 3327 | gallery_db |
| Insurance Service | 8022 | 3328 | insurance_db |

## Template Kullanımı

Her servis için `SERVICE_TEMPLATE.md` dosyasındaki pattern'i takip edin. User Service, Payment Service ve Notification Service örnek olarak tamamlanmıştır.

