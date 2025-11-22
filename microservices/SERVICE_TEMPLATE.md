# Microservice Template

Bu template, yeni bir microservice oluştururken kullanılabilir.

## Yapı

```
service-name/
├── pom.xml
├── src/
│   └── main/
│       ├── java/com/healthtourism/servicename/
│       │   ├── ServiceNameApplication.java
│       │   ├── entity/
│       │   ├── repository/
│       │   ├── service/
│       │   ├── controller/
│       │   └── dto/
│       └── resources/
│           └── application.properties
```

## Adımlar

1. **pom.xml oluştur** - User Service'deki gibi
2. **Application class oluştur** - @SpringBootApplication ve @EnableEurekaClient
3. **application.properties** - Port, Eureka URL, Database
4. **Entity'leri oluştur**
5. **Repository'leri oluştur**
6. **DTO'ları oluştur**
7. **Service'leri oluştur**
8. **Controller'ları oluştur**

## Port Numaraları

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

## Veritabanı Portları

MySQL portları docker-compose.yml'de tanımlıdır.

