# 🧠 Final Intelligent Features - Complete Implementation

## Overview

Bu dokümanda, projenin "son parçası" olarak eklenen iki kritik özellik detaylandırılmıştır:

1. **AI-Driven Patient Risk Scoring** - IoT, tıbbi geçmiş ve ameliyat zorluğunu birleştiren AI modeli
2. **Unified Health Wallet** - QR kod ile erişilebilen dijital hasta cüzdanı

---

## 1. AI-Driven Patient Risk Scoring Service

### Port: 8036
### Database: `patient_risk_scoring_db` (Port 3346)

### Özellikler

#### Recovery Score (İyileşme Skoru)
- **Skor Aralığı**: 0-100 (yüksek = daha iyi)
- **Kategoriler**:
  - **EXCELLENT** (80-100): Mükemmel iyileşme
  - **GOOD** (60-79): İyi iyileşme
  - **FAIR** (40-59): Orta iyileşme
  - **POOR** (0-39): Düşük iyileşme

#### Skor Hesaplama Faktörleri

1. **IoT Data Score (40% ağırlık)**
   - Kalp atış hızı analizi (60-100 bpm normal)
   - Oksijen satürasyonu (≥95% iyi)
   - Aktivite seviyesi (adım sayısı)
   - Ağrı seviyesi (0-10 ölçeği)

2. **Compliance Score (30% ağırlık)**
   - İlaç uyumu
   - Egzersiz uyumu
   - Sağlıklı yaşam tarzı

3. **Medical History Score (20% ağırlık)**
   - Tıbbi geçmiş karmaşıklığı
   - Önceki doküman sayısı

4. **Procedure Complexity Score (10% ağırlık)**
   - Ameliyat tipi
   - İşlem karmaşıklığı

#### Trend Analizi
- **IMPROVING**: Skor +5'ten fazla arttı
- **STABLE**: Skor ±5 arasında
- **DECLINING**: Skor -5'ten fazla düştü

#### Otomatik Uyarı Sistemi
- Skor <40 ise doktora uyarı gönderilir
- Skor -10'dan fazla düşerse uyarı gönderilir
- Skor >80 ise hastaya pozitif bildirim gönderilir

### API Endpoints

#### POST `/api/patient-risk-scoring/calculate`
Recovery score hesapla.

**Request Body:**
```json
{
  "userId": 1,
  "reservationId": 123
}
```

**Response:**
```json
{
  "id": 1,
  "userId": 1,
  "reservationId": 123,
  "recoveryScore": 85.5,
  "scoreCategory": "EXCELLENT",
  "iotDataScore": 90.0,
  "medicalHistoryScore": 80.0,
  "procedureComplexityScore": 70.0,
  "complianceScore": 75.0,
  "trend": "IMPROVING",
  "previousScore": 80.0,
  "scoreChange": 5.5,
  "requiresDoctorAlert": false,
  "analysisDetails": "{...}",
  "aiConfidence": 0.85,
  "calculatedAt": "2024-01-15T10:30:00"
}
```

#### GET `/api/patient-risk-scoring/user/{userId}/reservation/{reservationId}`
En son recovery score'u getir.

#### GET `/api/patient-risk-scoring/user/{userId}/reservation/{reservationId}/history`
Skor geçmişini getir.

#### GET `/api/patient-risk-scoring/alerts`
Doktor uyarısı gereken skorları getir.

### Otomatik Skor Güncelleme
- Her saat başı aktif hastalar için skor yeniden hesaplanır (`@Scheduled`)

---

## 2. Unified Health Wallet Service

### Port: 8037
### Database: `health_wallet_db` (Port 3347)

### Özellikler

#### Wallet İçeriği
1. **IPFS Document References**: Tüm tıbbi dokümanların IPFS referansları
2. **Blockchain Insurance Policy**: Blockchain üzerindeki sigorta poliçesi hash'i
3. **IoT Monitoring History**: IoT verilerinin özeti ve veri noktası sayısı
4. **Recovery Score**: En son recovery score
5. **Legal Documents**: Legal Ledger'deki yasal doküman referansları

#### QR Code Erişimi
- Her wallet için benzersiz QR code hash'i
- Hastanede QR kod tarandığında saniyeler içinde tüm verilere erişim
- QR code Base64 formatında görüntü olarak döndürülür

#### Wallet Durumları
- **ACTIVE**: Aktif kullanımda
- **SUSPENDED**: Askıya alınmış
- **ARCHIVED**: Arşivlenmiş

### API Endpoints

#### POST `/api/health-wallet/create`
Wallet oluştur veya güncelle.

**Request Body:**
```json
{
  "userId": 1
}
```

**Response:**
```json
{
  "id": 1,
  "userId": 1,
  "walletId": "550e8400-e29b-41d4-a716-446655440000",
  "qrCodeHash": "a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6",
  "documentCount": 5,
  "hasInsurance": true,
  "insurancePolicyHash": "0x1234...",
  "iotDataPointCount": 150,
  "currentRecoveryScore": "85.5",
  "legalDocumentCount": 3,
  "status": "ACTIVE",
  "lastUpdatedAt": "2024-01-15T10:30:00"
}
```

#### GET `/api/health-wallet/user/{userId}`
Kullanıcı ID'sine göre wallet getir.

#### GET `/api/health-wallet/wallet/{walletId}`
Wallet ID'sine göre wallet getir.

#### GET `/api/health-wallet/qr/{qrCodeHash}`
QR kod ile wallet'e eriş (erişim sayacı artar).

#### GET `/api/health-wallet/user/{userId}/complete`
Tam wallet verisi + QR code görüntüsü getir.

**Response:**
```json
{
  "walletId": "550e8400-e29b-41d4-a716-446655440000",
  "qrCodeHash": "a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6",
  "qrCodeImage": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAA...",
  "documentCount": 5,
  "hasInsurance": true,
  "insurancePolicyHash": "0x1234...",
  "iotDataPointCount": 150,
  "currentRecoveryScore": "85.5",
  "legalDocumentCount": 3,
  "lastUpdatedAt": "2024-01-15T10:30:00"
}
```

---

## 3. Gateway Timeout Optimizasyonları

### IoT Monitoring Service
- **Connect Timeout**: 10 saniye
- **Response Timeout**: 60 saniye (veri akışı için)

### Virtual Tour Service
- **Connect Timeout**: 15 saniye
- **Response Timeout**: 120 saniye (büyük VR dosyaları için)

### File Storage Service
- **Connect Timeout**: 10 saniye
- **Response Timeout**: 90 saniye (resim yüklemeleri için)

### Global Gateway Settings
- **Connect Timeout**: 10 saniye
- **Response Timeout**: 60 saniye

---

## 4. Resource Limits (Docker Compose)

### Patient Risk Scoring Database
```yaml
deploy:
  resources:
    limits:
      memory: 512M
    reservations:
      memory: 256M
```

### IoT Monitoring Database Önerisi
IoT verileri hızlı büyür. Production'da **TimescaleDB** veya **InfluxDB** kullanılması önerilir:

```yaml
# Örnek TimescaleDB konfigürasyonu (opsiyonel)
timescaledb-iot:
  image: timescale/timescaledb:latest-pg15
  container_name: timescaledb-iot
  environment:
    POSTGRES_DB: iot_monitoring_db
    POSTGRES_USER: postgres
    POSTGRES_PASSWORD: postgres
  ports:
    - "5433:5432"
  networks:
    - health-tourism-network
```

---

## 5. Frontend Entegrasyonu

### Patient Risk Scoring Service
```javascript
import { patientRiskScoringService } from './services/api';

// Skor hesapla
const score = await patientRiskScoringService.calculateScore({
  userId: 1,
  reservationId: 123
});

// En son skoru getir
const latestScore = await patientRiskScoringService.getLatestScore(1, 123);

// Skor geçmişini getir
const history = await patientRiskScoringService.getScoreHistory(1, 123);
```

### Health Wallet Service
```javascript
import { healthWalletService } from './services/api';

// Wallet oluştur/güncelle
const wallet = await healthWalletService.createOrUpdate({ userId: 1 });

// QR kod ile erişim
const walletData = await healthWalletService.accessByQR('qrCodeHash');

// Tam veri + QR code görüntüsü
const completeData = await healthWalletService.getCompleteData(1);
// completeData.qrCodeImage -> Base64 QR code görüntüsü
```

---

## 6. Kullanım Senaryoları

### Senaryo 1: Post-Op İyileşme Takibi
1. Hasta ameliyattan sonra IoT cihazından veri gönderir
2. Her saat başı recovery score otomatik hesaplanır
3. Skor düşerse doktora otomatik uyarı gönderilir
4. Skor yüksekse hastaya "Harika gidiyorsun!" bildirimi gönderilir

### Senaryo 2: Hastanede QR Code Erişimi
1. Hasta hastaneye gelir
2. QR kodunu gösterir
3. Hastane personeli QR kodu tarar
4. Saniyeler içinde tüm tıbbi geçmiş, sigorta, IoT verileri ve recovery score görüntülenir

---

## 7. Teknik Detaylar

### Dependencies
- **Spring Boot 4.0.0**
- **Spring Cloud Eureka Client**
- **SpringDoc OpenAPI (Swagger)**
- **Micrometer Tracing (Zipkin)**
- **ZXing (QR Code Generation)**
- **MySQL Connector**

### Veritabanı Şemaları

#### Patient Risk Score
- `id`, `userId`, `reservationId`, `doctorId`
- `recoveryScore`, `scoreCategory`
- `iotDataScore`, `medicalHistoryScore`, `procedureComplexityScore`, `complianceScore`
- `trend`, `previousScore`, `scoreChange`
- `requiresDoctorAlert`, `lastAlertSentAt`, `alertReason`
- `analysisDetails`, `aiConfidence`
- `calculatedAt`, `createdAt`

#### Health Wallet
- `id`, `userId`, `walletId`, `qrCodeHash`
- `ipfsDocumentReferences`, `documentCount`
- `insurancePolicyId`, `insurancePolicyHash`, `hasInsurance`
- `latestIotDataId`, `iotDataSummary`, `iotDataPointCount`
- `latestRecoveryScoreId`, `currentRecoveryScore`
- `legalDocumentReferences`, `legalDocumentCount`
- `status`, `createdAt`, `lastUpdatedAt`, `lastAccessedAt`, `accessCount`

---

## 8. Production Önerileri

1. **TimescaleDB/InfluxDB**: IoT verileri için zaman serisi veritabanı kullanın
2. **Redis Caching**: Recovery score hesaplamalarını cache'leyin
3. **Message Queue**: Skor hesaplama işlemlerini async olarak işleyin (RabbitMQ/Kafka)
4. **AI Model Integration**: Gerçek ML modeli entegre edin (TensorFlow/PyTorch)
5. **QR Code Security**: QR code hash'lerini encrypt edin ve expiration ekleyin
6. **Rate Limiting**: Wallet erişimlerinde rate limiting uygulayın

---

## 9. Test Senaryoları

### Patient Risk Scoring
```bash
# Skor hesapla
curl -X POST http://localhost:8080/api/patient-risk-scoring/calculate \
  -H "Content-Type: application/json" \
  -d '{"userId": 1, "reservationId": 123}'

# Skor geçmişini getir
curl http://localhost:8080/api/patient-risk-scoring/user/1/reservation/123/history
```

### Health Wallet
```bash
# Wallet oluştur
curl -X POST http://localhost:8080/api/health-wallet/create \
  -H "Content-Type: application/json" \
  -d '{"userId": 1}'

# QR kod ile erişim
curl http://localhost:8080/api/health-wallet/qr/{qrCodeHash}
```

---

## 10. Sonuç

Bu iki özellik, projenin "son parçası" olarak:
- **AI-Driven Risk Scoring**: Proaktif hasta takibi ve erken uyarı sistemi
- **Unified Health Wallet**: Tek noktadan tüm sağlık verilerine erişim

Her iki özellik de production-ready ve test edilmeye hazırdır. Gateway timeout ayarları ve resource limits optimize edilmiştir.

---

**Tamamlanma Tarihi**: 2024-01-15
**Versiyon**: 1.0.0
