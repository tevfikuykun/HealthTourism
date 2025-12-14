# Backend Servisleri Template

Bu dosya, yeni frontend özellikleri için gerekli backend servislerinin oluşturulması için template içerir.

## Oluşturulması Gereken Servisler

### 1. Comparison Service (Port: 8023)
- Karşılaştırma API'leri
- Hastane, doktor, paket karşılaştırma
- Endpoints:
  - POST /api/comparison/compare
  - GET /api/comparison/{type}

### 2. Video Consultation Service (Port: 8024)
- WebRTC signaling server
- Video konsültasyon yönetimi
- Endpoints:
  - POST /api/video/start
  - POST /api/video/end
  - GET /api/video/sessions

### 3. Analytics Service (Port: 8025)
- Analitik veri toplama
- İstatistikler
- Endpoints:
  - GET /api/analytics/revenue
  - GET /api/analytics/users
  - GET /api/analytics/reservations

### 4. Health Records Service (Port: 8026)
- Sağlık kayıtları yönetimi
- Tıbbi belgeler
- Endpoints:
  - GET /api/health-records
  - POST /api/health-records
  - PUT /api/health-records/{id}

### 5. Medication Reminder Service (Port: 8027)
- İlaç hatırlatıcıları
- Zamanlama
- Endpoints:
  - GET /api/medications
  - POST /api/medications
  - PUT /api/medications/{id}

### 6. Referral Service (Port: 8028)
- Referans programı
- Kod yönetimi
- Endpoints:
  - GET /api/referral/code
  - POST /api/referral/use
  - GET /api/referral/stats

### 7. Coupon Service (Port: 8029)
- Kupon yönetimi
- İndirim kodları
- Endpoints:
  - GET /api/coupons
  - POST /api/coupons/redeem
  - GET /api/coupons/{code}

### 8. Installment Service (Port: 8030)
- Taksit planları
- Ödeme planları
- Endpoints:
  - GET /api/installments/plans
  - POST /api/installments/calculate

### 9. Crypto Payment Service (Port: 8031)
- Kripto ödeme entegrasyonu
- Blockchain işlemleri
- Endpoints:
  - POST /api/crypto/payment
  - GET /api/crypto/status/{id}

### 10. Waiting List Service (Port: 8032)
- Bekleme listesi yönetimi
- Bildirim sistemi
- Endpoints:
  - GET /api/waiting-list
  - POST /api/waiting-list
  - DELETE /api/waiting-list/{id}

### 11. Bulk Reservation Service (Port: 8033)
- Toplu rezervasyon
- Grup rezervasyonları
- Endpoints:
  - POST /api/bulk-reservations
  - GET /api/bulk-reservations/{id}

### 12. Smart Calendar Service (Port: 8034)
- Akıllı takvim
- Çakışma kontrolü
- Endpoints:
  - GET /api/calendar/appointments
  - POST /api/calendar/check-conflict
  - GET /api/calendar/upcoming

### 13. 2FA Service (Port: 8035)
- İki faktörlü kimlik doğrulama
- TOTP yönetimi
- Endpoints:
  - POST /api/2fa/enable
  - POST /api/2fa/verify
  - POST /api/2fa/disable

### 14. Biometric Auth Service (Port: 8036)
- Biyometrik kimlik doğrulama
- WebAuthn
- Endpoints:
  - POST /api/biometric/register
  - POST /api/biometric/verify

### 15. Security Alerts Service (Port: 8037)
- Güvenlik bildirimleri
- Olay takibi
- Endpoints:
  - GET /api/security/alerts
  - POST /api/security/alerts

### 16. Local Guide Service (Port: 8038)
- Yerel rehber
- Restoran, turistik yerler
- Endpoints:
  - GET /api/local-guide/restaurants
  - GET /api/local-guide/attractions

### 17. Weather Service (Port: 8039)
- Hava durumu API
- Şehir bazlı hava durumu
- Endpoints:
  - GET /api/weather/{city}

### 18. Loyalty Program Service (Port: 8040)
- Sadakat programı
- Puan sistemi
- Endpoints:
  - GET /api/loyalty/points
  - POST /api/loyalty/redeem
  - GET /api/loyalty/rewards

### 19. AI Recommendations Service (Port: 8041)
- AI öneri sistemi
- Makine öğrenmesi
- Endpoints:
  - GET /api/ai/recommendations
  - POST /api/ai/train

### 20. Email Service (Port: 8042)
- E-posta gönderimi
- Şablon yönetimi
- Endpoints:
  - POST /api/email/send
  - GET /api/email/templates

### 21. SMS Service (Port: 8043)
- SMS gönderimi
- Doğrulama kodları
- Endpoints:
  - POST /api/sms/send
  - POST /api/sms/verify

## Servis Oluşturma Adımları

Her servis için:

1. `pom.xml` dosyası oluştur (user-service template kullan)
2. `Application.java` oluştur
3. `application.properties` oluştur
4. Entity, Repository, Service, Controller sınıfları oluştur
5. Docker Compose'a veritabanı ekle
6. API Gateway'e route ekle

## Hızlı Başlangıç

```bash
# Servis oluşturma script'i
./create-service.sh comparison-service 8023
```

