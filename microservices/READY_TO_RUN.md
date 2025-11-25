# ✅ Program Çalıştırmaya Hazır!

## Tamamlanan İşlemler

### 1. ✅ Event Sourcing Implementasyonu
- Reservation Service için event store ve event sourcing pattern tamamlandı
- Payment Service için event store ve event sourcing pattern tamamlandı
- Event'ler hem Kafka'ya hem de event store'a kaydediliyor

### 2. ✅ Apache Camel Integration
- Integration Service oluşturuldu ve yapılandırıldı
- External API entegrasyon route'ları eklendi

### 3. ✅ MySQL Database Schemas
- 19 MySQL veritabanı için SQL schema scriptleri oluşturuldu
- Tüm tablolar ve index'ler tanımlandı

### 4. ✅ Keycloak & Vault
- Yapılandırma dosyaları eklendi
- Docker compose'da container'lar tanımlı

### 5. ✅ API Gateway
- Integration Service route'u eklendi

### 6. ✅ Start Scripts
- Integration Service start script'lere eklendi

## Çalıştırma Adımları

### 1. Docker'ı Başlat
Docker Desktop'ı açın ve çalıştığınızdan emin olun.

### 2. Veritabanlarını Başlat
```powershell
cd microservices
docker-compose up -d
```

### 3. Servisleri Başlat
```powershell
# Windows
start-services.bat

# Linux/Mac
./start-services.sh
```

### 4. Frontend'i Başlat (Opsiyonel)
```powershell
cd frontend
npm install
npm run dev
```

## Önemli Notlar

1. **Docker çalışmıyorsa**: Docker Desktop'ı açın
2. **Port çakışması**: İlgili servisin port numarasını değiştirin
3. **Veritabanı hatası**: Container'ların çalıştığını kontrol edin (`docker ps`)

## Erişim URL'leri

- Eureka: http://localhost:8761
- API Gateway: http://localhost:8080
- Frontend: http://localhost:3000
- Swagger (Hospital): http://localhost:8002/swagger-ui.html

## Detaylı Bilgi

`STARTUP_GUIDE.md` dosyasına bakın.


