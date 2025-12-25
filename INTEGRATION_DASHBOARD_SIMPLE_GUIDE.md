# 🔌 Integration Dashboard - Basit Versiyon

## ⚡ Hızlı Çözüm

Maven kurulumu zaman alıyorsa, **basit HTML dashboard** kullanabilirsiniz!

## 🚀 Kullanım

### 1. HTML Dosyasını Aç

`integration-dashboard-simple.html` dosyasını tarayıcıda açın:

```
file:///D:/HealthTourism/integration-dashboard-simple.html
```

Veya çift tıklayarak açın.

### 2. Otomatik Kontrol

Dashboard otomatik olarak:
- Her 10 saniyede entegrasyonları kontrol eder
- UP/DOWN/UNKNOWN durumlarını gösterir
- İstatistikleri günceller

## 📊 Özellikler

- ✅ 30 entegrasyon listesi
- ✅ Kategorilere göre gruplama
- ✅ Gerçek zamanlı durum (10 saniyede bir)
- ✅ İstatistikler (UP/DOWN/UNKNOWN)
- ✅ Renk kodlu durumlar

## ⚠️ Not

Bu basit versiyon sadece HTTP endpoint'leri kontrol eder. Port kontrolü için backend servisi gerekir.

## 🔧 Backend Servisi İçin

Maven kurulumu tamamlandıktan sonra:

```bash
cd microservices/integration-dashboard
mvn spring-boot:run
```

Sonra:
```
http://localhost:3002
```

---

**Şimdilik basit HTML dashboard'u kullanabilirsiniz!** 🚀








