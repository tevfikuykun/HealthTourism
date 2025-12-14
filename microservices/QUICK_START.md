# 🚀 Quick Start Guide

## 1. Prerequisites

- Java 25
- Maven 3.9+
- Node.js 18+
- Docker & Docker Compose
- MySQL 8.0

## 2. Setup

### Database'leri Başlat

```bash
cd microservices
docker-compose up -d
```

### Servisleri Başlat

**Windows:**
```bash
start-services.bat
```

**Linux/Mac:**
```bash
chmod +x start-services.sh
./start-services.sh
```

### Frontend'i Başlat

```bash
cd microservices/frontend
npm install
npm run dev
```

## 3. Verify

- Eureka: http://localhost:8761
- API Gateway: http://localhost:8080
- Frontend: http://localhost:3000

## 4. Test

```bash
# Health check
curl http://localhost:8001/actuator/health

# API test
curl http://localhost:8080/api/users
```

## 5. Troubleshooting

- Servisler başlamıyorsa: Port'ları kontrol et
- Database bağlantı hatası: Docker container'ları kontrol et
- Frontend hatası: API Gateway'i kontrol et
