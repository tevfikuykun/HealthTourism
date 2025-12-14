# API Dokümantasyonu

Bu dosya, Health Tourism platformunun tüm API endpoint'lerini dokümante eder.

## Base URL

```
http://localhost:8080/api
```

## Authentication

Çoğu endpoint için JWT token gereklidir. Token'ı header'da gönderin:

```
Authorization: Bearer <token>
```

## API Servisleri

### Auth Service

#### POST /auth/register
Kullanıcı kaydı

**Request Body:**
```json
{
  "email": "user@example.com",
  "password": "password123",
  "firstName": "Ahmet",
  "lastName": "Yılmaz",
  "phone": "+905551234567"
}
```

#### POST /auth/login
Kullanıcı girişi

**Request Body:**
```json
{
  "email": "user@example.com",
  "password": "password123"
}
```

**Response:**
```json
{
  "token": "jwt_token_here",
  "refreshToken": "refresh_token_here",
  "user": { ... }
}
```

#### POST /auth/logout
Çıkış yapma

#### POST /auth/refresh
Token yenileme

#### POST /auth/forgot-password
Şifre sıfırlama isteği

#### POST /auth/reset-password
Şifre sıfırlama

---

### User Service

#### GET /users
Tüm kullanıcıları listele

#### GET /users/{id}
Kullanıcı detayı

#### GET /users/profile
Kullanıcı profili (authenticated)

#### PUT /users/profile
Profil güncelleme

---

### Hospital Service

#### GET /hospitals
Hastaneleri listele

**Query Parameters:**
- `city`: Şehir filtresi
- `specialty`: Uzmanlık filtresi
- `page`: Sayfa numarası
- `size`: Sayfa boyutu

#### GET /hospitals/{id}
Hastane detayı

#### GET /hospitals/search?q={query}
Hastane arama

---

### Doctor Service

#### GET /doctors
Doktorları listele

#### GET /doctors/{id}
Doktor detayı

#### GET /doctors/hospital/{hospitalId}
Hastaneye göre doktorlar

---

### Reservation Service

#### POST /reservations
Rezervasyon oluştur

**Request Body:**
```json
{
  "hospitalId": 1,
  "doctorId": 1,
  "date": "2024-12-01",
  "time": "10:00",
  "serviceType": "consultation"
}
```

#### GET /reservations
Rezervasyonları listele

#### GET /reservations/{id}
Rezervasyon detayı

#### PUT /reservations/{id}
Rezervasyon güncelle

#### DELETE /reservations/{id}
Rezervasyon iptal et

---

### Payment Service

#### POST /payments
Ödeme işlemi

**Request Body:**
```json
{
  "reservationId": 1,
  "amount": 1000.00,
  "paymentMethod": "credit_card",
  "cardNumber": "1234567890123456"
}
```

#### GET /payments/user/{userId}
Kullanıcı ödemeleri

---

### Analytics Service

#### GET /analytics/revenue
Gelir analizi

**Query Parameters:**
- `period`: monthly, weekly, daily

#### GET /analytics/users
Kullanıcı analizi

#### GET /analytics/reservations
Rezervasyon analizi

#### GET /analytics/services
Hizmet analizi

---

### Health Records Service

#### GET /health-records
Sağlık kayıtlarını listele

#### POST /health-records
Sağlık kaydı oluştur

#### GET /health-records/{id}
Sağlık kaydı detayı

#### PUT /health-records/{id}
Sağlık kaydı güncelle

#### DELETE /health-records/{id}
Sağlık kaydı sil

---

### Comparison Service

#### POST /comparison/compare
Karşılaştırma yap

**Request Body:**
```json
{
  "items": [1, 2, 3],
  "type": "hospital"
}
```

#### GET /comparison/{type}
Karşılaştırma sonuçları

---

### Admin Service

#### GET /admin/users
Kullanıcıları yönet (Admin only)

#### GET /admin/reservations
Rezervasyonları yönet (Admin only)

#### GET /admin/financial
Finansal raporlar (Admin only)

#### GET /admin/reports
Raporlar (Admin only)

#### GET /admin/settings
Sistem ayarları (Admin only)

#### PUT /admin/settings
Sistem ayarlarını güncelle (Admin only)

---

## Error Responses

Tüm hatalar aşağıdaki formatta döner:

```json
{
  "statusCode": 400,
  "message": "Error message",
  "timestamp": "2024-01-01T00:00:00Z"
}
```

### Status Codes

- `200`: Success
- `201`: Created
- `400`: Bad Request
- `401`: Unauthorized
- `403`: Forbidden
- `404`: Not Found
- `500`: Internal Server Error

---

## Rate Limiting

API rate limiting uygulanmaktadır:
- **Authenticated users**: 100 requests/minute
- **Unauthenticated users**: 20 requests/minute

---

## WebSocket

Real-time bildirimler için WebSocket endpoint:

```
ws://localhost:8080/ws
```

---

## Swagger UI

API dokümantasyonu için Swagger UI kullanılabilir:

```
http://localhost:8080/swagger-ui.html
```

