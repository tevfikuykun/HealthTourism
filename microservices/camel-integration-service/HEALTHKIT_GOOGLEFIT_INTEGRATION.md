# 🏥 HealthKit & Google Fit Integration via Camel

## ✅ Tamamlanan Özellikler

### 1. HealthKit/Google Fit Endpoint
- ✅ Camel route'ları ile Apple HealthKit API entegrasyonu
- ✅ Camel route'ları ile Google Fit API entegrasyonu
- ✅ OAuth2 token desteği
- ✅ Otomatik polling (her 5 dakikada bir)
- ✅ Manuel trigger endpoint'leri (REST API)

### 2. Validator Component
- ✅ Camel üzerinden tıbbi standartlara uygun schema validation
- ✅ Heart Rate: 40-220 BPM
- ✅ Blood Pressure: Systolic 70-250, Diastolic 40-150 mmHg
- ✅ Body Temperature: 35-42°C
- ✅ Oxygen Saturation: 70-100%
- ✅ Steps: >= 0
- ✅ Sleep Duration: 0-24 hours
- ✅ Validation error handling (Dead Letter Channel)

### 3. Audit Trail
- ✅ Her veri geçişinin immutable logging'i
- ✅ SHA-256 hash ile değiştirilemez kanıt
- ✅ Audit Service entegrasyonu
- ✅ Blockchain hash gönderimi (opsiyonel)
- ✅ Validation error audit trail

---

## 📋 Route Yapısı

### HealthKit Route
```
timer:healthkit-poll (her 5 dakikada)
  → HealthKit API'den veri çek
  → Schema Validation
  → Transform to IoT format
  → Audit Trail
  → IoT Monitoring Service
```

### Google Fit Route
```
timer:googlefit-poll (her 5 dakikada)
  → Google Fit API'den veri çek
  → Schema Validation
  → Transform to IoT format
  → Audit Trail
  → IoT Monitoring Service
```

---

## 🔧 Configuration

### application.properties
```properties
# HealthKit API
healthkit.api.url=https://api.apple.com/healthkit

# Google Fit API
googlefit.api.url=https://www.googleapis.com/fitness/v1

# IoT Monitoring Service
iot.monitoring.service.url=http://localhost:8032

# Audit Service
audit.service.url=http://localhost:8041

# Blockchain Service (optional)
blockchain.service.url=http://localhost:8035
```

---

## 🚀 Kullanım

### Manuel HealthKit Veri Çekme
```bash
POST /api/camel/healthkit/fetch
Content-Type: application/json

{
  "userId": "123",
  "token": "healthkit-oauth-token"
}
```

### Manuel Google Fit Veri Çekme
```bash
POST /api/camel/googlefit/fetch
Content-Type: application/json

{
  "userId": "123",
  "token": "googlefit-oauth-token"
}
```

---

## 📊 Validation Rules

| Field | Min | Max | Unit |
|-------|-----|-----|------|
| Heart Rate | 40 | 220 | BPM |
| Blood Pressure (Systolic) | 70 | 250 | mmHg |
| Blood Pressure (Diastolic) | 40 | 150 | mmHg |
| Body Temperature | 35 | 42 | °C |
| Oxygen Saturation | 70 | 100 | % |
| Steps | 0 | - | count |
| Sleep Duration | 0 | 24 | hours |

---

## 🔐 Audit Trail

Her veri geçişi için:
- ✅ Timestamp
- ✅ Route ID
- ✅ Source (HEALTHKIT/GOOGLEFIT)
- ✅ User ID
- ✅ Data hash (SHA-256)
- ✅ Validation status
- ✅ IP Address
- ✅ User Agent

---

## 🐛 Error Handling

### Validation Error
- Validation başarısız olursa → Dead Letter Channel
- Error queue'ya gönderilir (`health-data-validation-errors`)
- Audit trail'e loglanır

### API Error
- HealthKit/Google Fit API hatası → Retry (3 kez)
- Başarısız olursa → Error queue

---

## 📝 Sonraki Adımlar

1. ✅ OAuth2 token refresh mekanizması
2. ✅ Rate limiting
3. ✅ Data encryption (in-transit)
4. ✅ Webhook support (push-based)



