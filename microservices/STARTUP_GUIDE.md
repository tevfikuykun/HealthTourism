# 🚀 Programı Çalıştırma Kılavuzu

## Ön Gereksinimler

1. **Docker Desktop** yüklü ve çalışıyor olmalı
2. **Java 25** yüklü olmalı
3. **Maven** yüklü olmalı
4. **Node.js** (Frontend için)

## Adım 1: Docker'ı Başlat

Docker Desktop uygulamasını açın ve çalıştığınızdan emin olun.

## Adım 2: Veritabanlarını Başlat

```bash
cd microservices
docker-compose up -d
```

Bu komut tüm MySQL veritabanlarını, Redis, RabbitMQ ve diğer infrastructure servislerini başlatacaktır.

## Adım 3: MySQL Schemas'ları Oluştur (Opsiyonel)

Eğer JPA'nın otomatik şema oluşturmasını kullanmak istemiyorsanız:

```bash
cd database-schemas

# Her veritabanı için ayrı ayrı (örnek)
mysql -u root -proot -h localhost -P 3307 < accommodation_db.sql
mysql -u root -proot -h localhost -P 3308 < car_rental_db.sql
# ... diğer veritabanları için benzer şekilde
```

**Not:** JPA `spring.jpa.hibernate.ddl-auto=update` ayarı ile otomatik olarak şemaları oluşturabilir.

## Adım 4: Servisleri Başlat

### Windows:
```bash
start-services.bat
```

### Linux/Mac:
```bash
chmod +x start-services.sh
./start-services.sh
```

## Adım 5: Frontend'i Başlat

```bash
cd frontend
npm install
npm run dev
```

## Erişim URL'leri

- **Eureka Dashboard**: http://localhost:8761
- **API Gateway**: http://localhost:8080
- **Frontend**: http://localhost:3000
- **Swagger UI** (Hospital Service): http://localhost:8002/swagger-ui.html
- **RabbitMQ Management**: http://localhost:15672 (admin/admin)
- **Grafana**: http://localhost:3001 (admin/admin)
- **Kibana**: http://localhost:5601
- **Kafka UI**: http://localhost:8081
- **Keycloak**: http://localhost:8180 (admin/admin)
- **Vault**: http://localhost:8200

## Sorun Giderme

### Docker çalışmıyor
- Docker Desktop'ı açın ve çalıştığınızdan emin olun
- `docker ps` komutu ile Docker'ın çalıştığını kontrol edin

### Port çakışması
- Eğer bir port zaten kullanılıyorsa, ilgili servisin `application.properties` dosyasındaki port numarasını değiştirin

### Veritabanı bağlantı hatası
- Docker container'larının çalıştığını kontrol edin: `docker ps`
- Container loglarını kontrol edin: `docker logs <container-name>`

### Servis başlamıyor
- Log dosyalarını kontrol edin: `microservices/logs/` klasörü
- Eureka Server'ın çalıştığından emin olun
- Port numaralarının doğru olduğundan emin olun

## Servis Listesi

Toplam **33 microservice**:

1. Eureka Server (8761)
2. API Gateway (8080)
3. Config Server (8888)
4. User Service (8001)
5. Hospital Service (8002)
6. Doctor Service (8003)
7. Accommodation Service (8004)
8. Flight Service (8005)
9. Car Rental Service (8006)
10. Transfer Service (8007)
11. Package Service (8008)
12. Reservation Service (8009)
13. Payment Service (8010)
14. Notification Service (8011)
15. Medical Document Service (8012)
16. Telemedicine Service (8013)
17. Patient Follow-up Service (8014)
18. Blog Service (8015)
19. FAQ Service (8016)
20. Favorite Service (8017)
21. Appointment Calendar Service (8018)
22. Contact Service (8019)
23. Testimonial Service (8020)
24. Gallery Service (8021)
25. Insurance Service (8022)
26. Auth Service (8023)
27. Monitoring Service (8024)
28. Logging Service (8025)
29. File Storage Service (8027)
30. Admin Service (8029)
31. Search Service (8031)
32. Integration Service (8030)
33. Kafka Service

## Veritabanı Yapısı

- **PostgreSQL**: Core services (user, hospital, doctor, reservation, payment)
- **MongoDB**: Document services (medical-document, blog, gallery)
- **Elasticsearch**: Search service
- **MySQL**: Diğer tüm servisler (19 veritabanı)

## Önemli Notlar

1. İlk başlatmada servislerin tamamen başlaması birkaç dakika sürebilir
2. Eureka Server'ın tamamen başlamasını bekleyin (yaklaşık 10-15 saniye)
3. Tüm servislerin Eureka'ya kaydolmasını bekleyin
4. Frontend'i servisler başladıktan sonra başlatın


