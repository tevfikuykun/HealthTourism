# 🚀 Hızlı Başlangıç Rehberi

## ⚡ Tek Komutla Başlatma

### Windows
```batch
START_PROJECT_COMPLETE.bat
```

Bu script otomatik olarak:
1. ✅ Docker container'ları başlatır (veritabanları, Redis, RabbitMQ)
2. ✅ Frontend dependencies kurar (gerekirse)
3. ✅ Eureka Server'ı başlatır
4. ✅ API Gateway'i başlatır
5. ✅ Core servisleri başlatır
6. ✅ Frontend'i başlatır
7. ✅ Tarayıcıda açar

## 📋 Manuel Kurulum (Adım Adım)

### 1. Gereksinimler Kontrolü

```powershell
# Docker kontrolü
docker --version

# Java kontrolü
java -version

# Maven kontrolü
mvn -version

# Node.js kontrolü
node --version
```

### 2. Docker Container'ları Başlat

```powershell
cd microservices
docker-compose up -d
```

Bu komut şunları başlatır:
- Tüm MySQL veritabanları
- Redis
- RabbitMQ
- Zipkin

### 3. Frontend Dependencies Kur

```powershell
cd microservices\frontend
npm install
```

### 4. Eureka Server Başlat

```powershell
cd microservices\eureka-server
mvn spring-boot:run
```

Yeni bir terminal açın ve bekleyin (15-20 saniye).

### 5. API Gateway Başlat

```powershell
cd microservices\api-gateway
mvn spring-boot:run
```

Yeni bir terminal açın ve bekleyin (10 saniye).

### 6. Core Servisleri Başlat

Her servis için yeni terminal:

```powershell
# Auth Service
cd microservices\auth-service
mvn spring-boot:run

# User Service
cd microservices\user-service
mvn spring-boot:run

# Hospital Service
cd microservices\hospital-service
mvn spring-boot:run

# Doctor Service
cd microservices\doctor-service
mvn spring-boot:run

# Payment Service
cd microservices\payment-service
mvn spring-boot:run
```

### 7. Frontend Başlat

```powershell
cd microservices\frontend
npm run dev
```

## 🌐 Erişim Noktaları

Servisler başladıktan sonra:

- **Frontend:** http://localhost:3000
- **API Gateway:** http://localhost:8080
- **Eureka Dashboard:** http://localhost:8761
- **Swagger UI (Auth):** http://localhost:8023/swagger-ui.html
- **RabbitMQ Management:** http://localhost:15672 (admin/admin)
- **Redis:** localhost:6379

## ✅ Servis Durumu Kontrolü

```powershell
# Docker container'ları kontrol et
docker ps

# Eureka'da kayıtlı servisleri gör
# Tarayıcıda: http://localhost:8761

# Servis health check
microservices\check-services-status.bat
```

## 🛑 Servisleri Durdurma

### Tüm Servisleri Durdur
1. Tüm command window'ları kapat
2. Docker container'ları durdur:
```powershell
cd microservices
docker-compose down
```

### Sadece Docker Container'ları Durdur
```powershell
cd microservices
docker-compose stop
```

## 🔧 Sorun Giderme

### Docker container'ları başlamıyor
```powershell
# Docker Desktop'ın çalıştığından emin ol
docker ps

# Container loglarını kontrol et
docker-compose logs
```

### Servisler Eureka'ya kayıt olmuyor
- Eureka Server'ın çalıştığından emin ol (port 8761)
- 15-20 saniye bekleyin (servisler kayıt olmak için zaman alır)
- Eureka Dashboard'u kontrol edin: http://localhost:8761

### Frontend başlamıyor
```powershell
# Dependencies'leri tekrar kur
cd microservices\frontend
rm -r node_modules
npm install
```

### Port zaten kullanımda
- Port'u kullanan process'i bulun:
```powershell
netstat -ano | findstr :8080
```
- Process'i sonlandırın veya application.properties'te port değiştirin

## 📊 Servis Başlatma Sırası

1. **Docker containers** (veritabanları, Redis, RabbitMQ)
2. **Eureka Server** (8761)
3. **API Gateway** (8080)
4. **Auth Service** (8023)
5. **User Service** (8001)
6. **Hospital Service** (8002)
7. **Doctor Service** (8003)
8. **Payment Service** (8010)
9. **Reservation Service** (8009)
10. **Frontend** (3000)

## ⏱️ Beklenen Başlatma Süreleri

- Docker containers: ~30 saniye
- Eureka Server: ~15 saniye
- API Gateway: ~10 saniye
- Her microservice: ~20-30 saniye
- Frontend: ~5 saniye

**Toplam:** ~2-3 dakika

## 🎯 İlk Test

Servisler başladıktan sonra:

1. Frontend'i aç: http://localhost:3000
2. Eureka Dashboard'u kontrol et: http://localhost:8761
3. API Gateway'i test et: http://localhost:8080/actuator/health
4. Auth Service Swagger: http://localhost:8023/swagger-ui.html

## 📝 Notlar

- İlk başlatmada Maven dependencies indirileceği için daha uzun sürebilir
- Servisler birbirine bağımlı olduğu için sırayla başlatılmalı
- Eureka Server mutlaka ilk başlatılmalı
- Tüm servislerin başlaması 2-3 dakika sürebilir

---

**Hızlı Başlatma:** `START_PROJECT_COMPLETE.bat` dosyasını çalıştırın!

