# Tüm Servisler - Tam Yapı

Bu dosya, tüm microservice'lerin tam yapısını içerir. Her servis için aynı pattern kullanılır:

## Tamamlanan Servisler

✅ **Eureka Server** (8761) - Service Discovery
✅ **API Gateway** (8080) - Tüm isteklerin geçtiği gateway
✅ **User Service** (8001) - Kullanıcı yönetimi
✅ **Hospital Service** (8002) - Hastane yönetimi
✅ **Doctor Service** (8003) - Doktor yönetimi
✅ **Accommodation Service** (8004) - Konaklama
✅ **Flight Service** (8005) - Uçak bileti
✅ **Payment Service** (8010) - Ödeme işlemleri
✅ **Notification Service** (8011) - Bildirim servisi

## Kalan Servisler (Template ile Oluşturulabilir)

Aşağıdaki servisler için `SERVICE_TEMPLATE.md` dosyasındaki pattern kullanılabilir:

### Temel Servisler
- **Car Rental Service** (8006) - Araç kiralama
- **Transfer Service** (8007) - Transfer hizmetleri
- **Package Service** (8008) - Paket turlar
- **Reservation Service** (8009) - Rezervasyon yönetimi

### Yeni Servisler
- **Medical Document Service** (8012) - Tıbbi belge yönetimi
- **Telemedicine Service** (8013) - Online konsültasyon
- **Patient Follow-up Service** (8014) - Hasta takip
- **Blog Service** (8015) - Blog/Haberler
- **FAQ Service** (8016) - SSS
- **Favorite Service** (8017) - Favoriler
- **Appointment Calendar Service** (8018) - Randevu takvimi
- **Contact Service** (8019) - İletişim
- **Testimonial Service** (8020) - Hasta hikayeleri
- **Gallery Service** (8021) - Fotoğraf galerisi
- **Insurance Service** (8022) - Sigorta hizmetleri

## Her Servis İçin Gerekli Dosyalar

1. `pom.xml` - Maven dependencies
2. `Application.java` - @SpringBootApplication, @EnableEurekaClient
3. `application.properties` - Port, Eureka URL, Database
4. `Entity` - JPA entity classes
5. `Repository` - JPA repositories
6. `DTO` - Data Transfer Objects
7. `Service` - Business logic
8. `Controller` - REST endpoints

## Hızlı Oluşturma

Her servis için User Service veya Payment Service'i template olarak kullanabilirsiniz. Sadece:
- Port numarasını değiştirin
- Veritabanı portunu değiştirin
- Entity'leri oluşturun
- Repository, Service, Controller'ı oluşturun

## API Gateway Route Ekleme

Yeni servis ekledikten sonra `api-gateway/src/main/resources/application.properties` dosyasına route ekleyin:

```properties
spring.cloud.gateway.routes[X].id=service-name
spring.cloud.gateway.routes[X].uri=lb://service-name
spring.cloud.gateway.routes[X].predicates[0]=Path=/api/service-path/**
```

## Docker Compose

Yeni servis için veritabanı eklemek için `docker-compose.yml` dosyasına ekleyin:

```yaml
mysql-service-name:
  image: mysql:8.0
  container_name: mysql-service-name
  environment:
    MYSQL_ROOT_PASSWORD: root
    MYSQL_DATABASE: service_db
  ports:
    - "PORT:3306"
  networks:
    - health-tourism-network
```

