# 🚀 Hızlı Başlangıç Rehberi

## Adım 1: Docker Container'larını Başlat

```bash
cd microservices
docker-compose up -d
```

Bu komut tüm MySQL database'lerini başlatacak.

## Adım 2: Eureka Server'ı Başlat

Yeni bir terminal açın:
```bash
cd microservices/eureka-server
mvn spring-boot:run
```

Eureka Server başladıktan sonra: http://localhost:8761 adresinden kontrol edin.

## Adım 3: API Gateway'i Başlat

Yeni bir terminal açın:
```bash
cd microservices/api-gateway
mvn spring-boot:run
```

## Adım 4: Servisleri Başlat

### Seçenek 1: Otomatik (Tüm Servisler)
```bash
# Windows
cd microservices
start-services.bat

# Linux/Mac
cd microservices
chmod +x start-services.sh
./start-services.sh
```

### Seçenek 2: Manuel (İstediğiniz Servisler)

Her servis için yeni terminal açın:
```bash
cd microservices/{service-name}
mvn spring-boot:run
```

Örnek:
```bash
cd microservices/auth-service
mvn spring-boot:run
```

## Adım 5: Test Et

1. **Eureka Dashboard**: http://localhost:8761
   - Başlatılan tüm servisleri görebilirsiniz

2. **API Gateway**: http://localhost:8080
   - Tüm API istekleri bu adres üzerinden yapılır

3. **Test Request**:
```bash
curl http://localhost:8080/api/users
```

## ⚠️ Sorun Giderme

### Port Zaten Kullanılıyor Hatası
- Çalışan servisleri kontrol edin
- Port'u değiştirin veya servisi durdurun

### Database Bağlantı Hatası
- Docker container'larının çalıştığından emin olun
- `docker ps` ile kontrol edin

### Eureka'ya Bağlanamıyor
- Eureka Server'ın önce başlatıldığından emin olun
- application.properties'te Eureka URL'i kontrol edin

## 📝 Notlar

- İlk başlatmada Maven dependency'leri indirilecek (birkaç dakika sürebilir)
- Her servis bağımsız olarak başlatılabilir
- Eureka Server mutlaka önce başlatılmalı

---

**Başarılar! 🎉**

