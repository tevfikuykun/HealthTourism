# 🐳 Docker Setup Yardımı

## ⚠️ Docker Compose Takılıyorsa

Docker Compose bazen ilk çalıştırmada 2-5 dakika sürebilir çünkü:
- MySQL image'ları indiriliyor (ilk defa)
- Container'lar oluşturuluyor
- Veritabanları initialize ediliyor

## 🔍 Durum Kontrolü

### Container'ları Kontrol Et
```powershell
docker ps
```

### Tüm Container'ları Gör (durdurulmuş dahil)
```powershell
docker ps -a
```

### Container Loglarını Gör
```powershell
cd microservices
docker-compose logs
```

### Belirli Container Logu
```powershell
docker-compose logs mysql-user
```

## 🚀 Manuel Başlatma

### Sadece Docker Container'ları Başlat
```powershell
cd microservices
docker-compose up -d
```

### Detaylı Log ile Başlat (hata görmek için)
```powershell
cd microservices
docker-compose up
```
(Press Ctrl+C to stop, then run with -d flag)

### Container'ları Yeniden Başlat
```powershell
cd microservices
docker-compose restart
```

## ⏱️ Beklenen Süreler

- **İlk çalıştırma:** 3-5 dakika (image indirme)
- **Sonraki çalıştırmalar:** 30-60 saniye
- **Container hazır olma:** 30-60 saniye (MySQL initialize)

## 🔧 Sorun Giderme

### Docker Desktop çalışmıyor
1. Docker Desktop'ı açın
2. "Docker is running" yazısını bekleyin

### Port zaten kullanımda
```powershell
# Port'u kullanan process'i bul
netstat -ano | findstr :3307

# Process ID'yi öğrenip sonlandırın
taskkill /PID <process_id> /F
```

### Container'lar durmuyor
```powershell
# Tüm container'ları durdur
docker-compose down

# Zorla durdur
docker-compose kill
```

### Temiz başlangıç
```powershell
# Tüm container'ları ve volume'ları sil
docker-compose down -v

# Tekrar başlat
docker-compose up -d
```

## ✅ Başarılı Başlatma Kontrolü

Container'lar başladıktan sonra şunları kontrol edin:

```powershell
# Container listesi
docker ps

# MySQL bağlantı testi (örnek)
docker exec -it mysql-user mysql -uroot -proot -e "SHOW DATABASES;"
```

## 📊 Beklenen Container'lar

- mysql-user (port 3307)
- mysql-hospital (port 3308)
- mysql-doctor (port 3309)
- mysql-accommodation (port 3310)
- mysql-auth (port 3329)
- redis (port 6379)
- rabbitmq (port 5672, 15672)
- zipkin (port 9411)

**Toplam:** ~25+ container

## 💡 İpucu

Eğer Docker Compose uzun süre takılıyorsa:

1. **Ctrl+C** ile durdurun
2. Container'ları kontrol edin: `docker ps`
3. Eğer bazıları çalışıyorsa, sadece eksikleri başlatın:
```powershell
docker-compose up -d mysql-user mysql-hospital
```

4. Ya da sırayla başlatın (daha güvenli):
```powershell
docker-compose up -d mysql-user
timeout /t 10
docker-compose up -d mysql-hospital
timeout /t 10
# ... devam
```

---

**Hızlı Çözüm:** `microservices/start-docker-only.bat` dosyasını çalıştırın!

