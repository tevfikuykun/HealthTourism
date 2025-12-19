# 🔌 Integration Dashboard - Kullanım Kılavuzu

## 🎯 Amaç

**Integration Dashboard** tüm 30 entegrasyonun durumunu tek bir ekranda gösterir.

**URL:** `http://localhost:3002`

---

## 🚀 Başlatma

### 1. Dashboard Servisini Başlat

```bash
cd microservices/integration-dashboard
mvn spring-boot:run
```

### 2. Tarayıcıda Aç

```
http://localhost:3002
```

---

## 📊 Dashboard Özellikleri

### ✅ Gerçek Zamanlı Durum
- Her 10 saniyede otomatik güncelleme
- UP/DOWN/UNKNOWN durumları
- Response time gösterimi

### 📈 İstatistikler
- Toplam entegrasyon sayısı
- UP sayısı (yeşil)
- DOWN sayısı (kırmızı)
- UNKNOWN sayısı (sarı)

### 🗂️ Kategorilere Göre Gruplama
- Security & Cryptography
- Integration & Messaging
- Database & Storage
- Observability & Monitoring
- Architecture & Patterns
- AI & Machine Learning
- Infrastructure & Orchestration

---

## 🔍 Entegrasyon Durumları

### ✅ UP (Yeşil)
- Servis çalışıyor
- HTTP endpoint'e erişilebilir
- Response time gösterilir

### ❌ DOWN (Kırmızı)
- Servis çalışmıyor
- HTTP endpoint'e erişilemiyor
- Hata mesajı gösterilir

### ⚠️ UNKNOWN (Sarı)
- Port kontrolü yapılamadı
- HTTP endpoint yok
- Durum belirlenemedi

---

## 🎯 Şu Anki Durum

Port 3000'de bir servis çalışıyor (muhtemelen Grafana veya başka bir servis).

**Çözüm:**
1. Integration Dashboard'u farklı bir port'ta çalıştır (örn: 3001)
2. Veya mevcut servisi durdurup Dashboard'u 3000'de çalıştır

---

## 🔧 Port Değiştirme

Eğer port 3000 meşgulse, `application.properties` dosyasını düzenle:

```properties
# Farklı port kullan
server.port=3002
```

Sonra şu adresten eriş:
```
http://localhost:3002
```

---

## 📋 Kontrol Listesi

### Entegrasyonların Çalışması İçin:

1. ✅ **Docker Compose Başlat:**
   ```bash
   docker-compose up -d
   ```

2. ✅ **Integration Dashboard Başlat:**
   ```bash
   cd microservices/integration-dashboard
   mvn spring-boot:run
   ```

3. ✅ **Tarayıcıda Aç:**
   ```
   http://localhost:3002
   ```

---

## 🎯 API Endpoints

### Dashboard HTML
```
GET http://localhost:3002/
```

### JSON Status
```
GET http://localhost:3002/api/status
```

### Statistics
```
GET http://localhost:3002/api/statistics
```

---

## 📊 Örnek JSON Response

```json
{
  "total": 30,
  "up": 15,
  "down": 10,
  "unknown": 5
}
```

---

## ✅ Sonuç

**Integration Dashboard** ile:
- ✅ Tüm 30 entegrasyonu tek ekranda gör
- ✅ Gerçek zamanlı durum takibi
- ✅ Kategorilere göre gruplama
- ✅ İstatistikler ve metrikler

**Başlat ve gör!** 🚀

