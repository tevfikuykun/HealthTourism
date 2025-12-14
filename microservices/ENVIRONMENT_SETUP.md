# 🔧 Environment Setup - API Keys ve Konfigürasyon

## 📋 Gerekli API Key'ler

### 1. **Stripe Payment Gateway**

#### Kayıt
1. https://stripe.com adresine gidin
2. Hesap oluşturun
3. Dashboard'dan API keys alın

#### Konfigürasyon
```properties
# payment-service/src/main/resources/application.properties
stripe.secret.key=sk_live_your_secret_key_here
stripe.public.key=pk_live_your_public_key_here
```

#### Environment Variable
```bash
export STRIPE_SECRET_KEY=sk_live_your_secret_key_here
export STRIPE_PUBLIC_KEY=pk_live_your_public_key_here
```

---

### 2. **Twilio SMS Service**

#### Kayıt
1. https://www.twilio.com adresine gidin
2. Hesap oluşturun
3. Phone number satın alın
4. Console'dan credentials alın

#### Konfigürasyon
```properties
# notification-service/src/main/resources/application.properties
twilio.account.sid=ACxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
twilio.auth.token=your_auth_token_here
twilio.phone.number=+1234567890
```

#### Environment Variable
```bash
export TWILIO_ACCOUNT_SID=ACxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
export TWILIO_AUTH_TOKEN=your_auth_token_here
export TWILIO_PHONE_NUMBER=+1234567890
```

---

### 3. **SendGrid Email Service**

#### Kayıt
1. https://sendgrid.com adresine gidin
2. Hesap oluşturun
3. API key oluşturun
4. Sender verification yapın

#### Konfigürasyon
```properties
# notification-service/src/main/resources/application.properties
sendgrid.api.key=SG.xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
sendgrid.from.email=noreply@healthtourism.com
sendgrid.from.name=Health Tourism
```

#### Environment Variable
```bash
export SENDGRID_API_KEY=SG.xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
export SENDGRID_FROM_EMAIL=noreply@healthtourism.com
export SENDGRID_FROM_NAME=Health Tourism
```

---

## 🚀 Hızlı Başlangıç

### 1. Environment Variables Dosyası Oluştur

```bash
# .env dosyası oluştur
cp .env.example .env

# API key'leri ekle
nano .env
```

### 2. Docker Compose ile Başlat

```bash
docker-compose up -d
```

### 3. Servisleri Başlat

```bash
# Windows
start-services.bat

# Linux/Mac
./start-services.sh
```

---

## ✅ Test Etme

### Stripe Test
```bash
# Test payment intent oluştur
curl -X POST http://localhost:8080/api/payments/stripe/create \
  -H "Content-Type: application/json" \
  -d '{"amount": 1000, "currency": "usd"}'
```

### Twilio Test
```bash
# Test SMS gönder
curl -X POST http://localhost:8080/api/notifications/sms \
  -H "Content-Type: application/json" \
  -d '{"to": "+1234567890", "message": "Test message"}'
```

### SendGrid Test
```bash
# Test email gönder
curl -X POST http://localhost:8080/api/notifications/email \
  -H "Content-Type: application/json" \
  -d '{"to": "test@example.com", "subject": "Test", "body": "Test email"}'
```

---

## 🔒 Güvenlik Notları

1. **API Key'leri asla commit etmeyin**
2. **Production'da environment variables kullanın**
3. **API key'leri düzenli olarak rotate edin**
4. **Rate limiting aktif tutun**
5. **Monitoring ile anormal aktiviteleri takip edin**

---

**Tarih**: 2024  
**Durum**: Environment Setup Rehberi Hazır ✅

