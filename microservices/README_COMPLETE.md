# Health Tourism Microservices - Complete Architecture

## 🏗️ Mimari Yapı

Bu proje, İstanbul Sağlık Turizmi platformunu **microservice mimarisi** ile oluşturur.

## 📦 Servisler

### Altyapı Servisleri
1. **Eureka Server** (8761) - Service Discovery
2. **API Gateway** (8080) - Tüm isteklerin geçtiği gateway
3. **Config Server** (8888) - Merkezi yapılandırma (opsiyonel)

### İş Servisleri
4. **User Service** (8001) - Kullanıcı yönetimi
5. **Hospital Service** (8002) - Hastane yönetimi
6. **Doctor Service** (8003) - Doktor yönetimi
7. **Accommodation Service** (8004) - Konaklama
8. **Flight Service** (8005) - Uçak bileti
9. **Car Rental Service** (8006) - Araç kiralama
10. **Transfer Service** (8007) - Transfer hizmetleri
11. **Package Service** (8008) - Paket turlar
12. **Reservation Service** (8009) - Rezervasyon yönetimi
13. **Payment Service** (8010) - Ödeme işlemleri
14. **Notification Service** (8011) - Bildirim servisi
15. **Medical Document Service** (8012) - Tıbbi belge yönetimi
16. **Telemedicine Service** (8013) - Online konsültasyon
17. **Patient Follow-up Service** (8014) - Hasta takip
18. **Blog Service** (8015) - Blog/Haberler
19. **FAQ Service** (8016) - SSS
20. **Favorite Service** (8017) - Favoriler
21. **Appointment Calendar Service** (8018) - Randevu takvimi
22. **Contact Service** (8019) - İletişim
23. **Testimonial Service** (8020) - Hasta hikayeleri
24. **Gallery Service** (8021) - Fotoğraf galerisi
25. **Insurance Service** (8022) - Sigorta hizmetleri

### Frontend
26. **React Frontend** (3000) - Modern React uygulaması

## 🚀 Kurulum ve Çalıştırma

### 1. Docker ile (Önerilen)

```bash
# Veritabanlarını başlat
cd microservices
docker-compose up -d

# Servisleri başlat (Windows)
start-services.bat

# Servisleri başlat (Linux/Mac)
chmod +x start-services.sh
./start-services.sh
```

### 2. Manuel Çalıştırma

```bash
# 1. Eureka Server
cd microservices/eureka-server
mvn spring-boot:run

# 2. API Gateway
cd microservices/api-gateway
mvn spring-boot:run

# 3. Diğer servisler (her biri ayrı terminal)
cd microservices/user-service
mvn spring-boot:run

# 4. Frontend
cd microservices/frontend
npm install
npm run dev
```

## 📊 Erişim Noktaları

- **Eureka Dashboard**: http://localhost:8761
- **API Gateway**: http://localhost:8080
- **Frontend**: http://localhost:3000

## 🔌 API Endpoints

Tüm API istekleri API Gateway üzerinden yapılır:

```
http://localhost:8080/api/users
http://localhost:8080/api/hospitals
http://localhost:8080/api/doctors
http://localhost:8080/api/payments
http://localhost:8080/api/notifications
...
```

## 🗄️ Veritabanları

Her servis kendi veritabanına sahiptir. Docker Compose ile otomatik oluşturulur.

## 📝 Yeni Servis Ekleme

1. `SERVICE_TEMPLATE.md` dosyasını inceleyin
2. Yeni servis klasörü oluşturun
3. pom.xml, Application class, entity, repository, service, controller oluşturun
4. application.properties'te port ve veritabanı bilgilerini ayarlayın
5. API Gateway'e route ekleyin
6. docker-compose.yml'e veritabanı ekleyin

## 🔐 Güvenlik

- Her servis kendi veritabanına sahiptir (database per service)
- API Gateway üzerinden merkezi güvenlik kontrolü yapılabilir
- JWT token authentication eklenebilir

## 📈 Ölçeklenebilirlik

- Her servis bağımsız olarak ölçeklenebilir
- Load balancing için Eureka kullanılır
- Horizontal scaling mümkündür

## 🐳 Docker

Her servis için Dockerfile oluşturulabilir ve container olarak çalıştırılabilir.

## 📚 Teknolojiler

- **Backend**: Spring Boot 4.0, Spring Cloud
- **Frontend**: React 18, Material-UI
- **Database**: MySQL 8.0
- **Service Discovery**: Netflix Eureka
- **API Gateway**: Spring Cloud Gateway
- **Build Tool**: Maven

