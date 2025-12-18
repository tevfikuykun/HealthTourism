# ✅ Otomatik Hatırlatıcı Servisi Tamamlandı

## 🎯 Reminder Service (Port: 8041)

### Özellikler

**Otomatik Hatırlatma Sistemi:**
- ✅ Teklif bekleyen hastalara 2 gün sonra otomatik hatırlatma
- ✅ Teklif süresi dolmak üzere olanlara 1 gün önce hatırlatma
- ✅ Lead takibi için otomatik follow-up hatırlatmaları
- ✅ Scheduled job ile otomatik işleme (her 5 dakikada bir)
- ✅ Retry mekanizması (3 deneme)

**Reminder Types:**
- QUOTE_PENDING - Teklif bekliyor (2 gün sonra)
- QUOTE_EXPIRING - Teklif süresi doluyor (1 gün önce)
- LEAD_FOLLOW_UP - Lead takibi
- PAYMENT_REMINDER - Ödeme hatırlatması
- APPOINTMENT_REMINDER - Randevu hatırlatması
- CONSULTATION_REMINDER - Konsültasyon hatırlatması

**Notification Channels:**
- EMAIL - E-posta
- SMS - SMS
- PUSH - Push notification
- ALL - Tüm kanallar

**Reminder Statuses:**
- PENDING - Beklemede
- SENT - Gönderildi
- FAILED - Başarısız
- CANCELLED - İptal edildi

### Entegrasyonlar

**Quote Service Entegrasyonu:**
- Teklif gönderildiğinde otomatik reminder oluşturulur
- 2 gün sonra "Hala ilgileniyor musunuz?" mesajı gönderilir
- Teklif süresi dolmadan 1 gün önce hatırlatma gönderilir

**CRM Service Entegrasyonu:**
- Lead takibi için otomatik follow-up hatırlatmaları
- Lead durumuna göre özelleştirilmiş mesajlar

**Notification Service Entegrasyonu:**
- Email reminder gönderimi
- SMS reminder gönderimi
- Push notification gönderimi

### Endpoint'ler

- `POST /api/reminders/quote/{quoteId}` - Teklif için reminder oluştur
- `POST /api/reminders/quote-expiring/{quoteId}` - Süresi dolan teklif için reminder
- `POST /api/reminders/lead/{leadId}` - Lead takibi için reminder
- `POST /api/reminders/cancel/{reminderId}` - Reminder iptal et
- `GET /api/reminders/user/{userId}` - Kullanıcının reminder'ları
- `GET /api/reminders/type/{reminderType}` - Tip'e göre reminder'lar

### Scheduled Jobs

**processPendingReminders()** - Her 5 dakikada bir çalışır
- Pending reminder'ları kontrol eder
- Zamanı gelen reminder'ları gönderir
- Başarısız olanları retry eder

**checkAndCreateQuoteReminders()** - Her 1 saatte bir çalışır
- Yeni gönderilmiş teklifleri kontrol eder
- Otomatik reminder oluşturur

### Kullanım Örnekleri

```bash
# Teklif için reminder oluştur (otomatik)
# Quote service'den teklif gönderildiğinde otomatik oluşturulur

# Manuel reminder oluşturma
POST /api/reminders/quote/123?userId=1&userEmail=user@example.com&userPhone=+905551234567&quoteSentAt=2025-01-13T10:00:00

# Lead takibi için reminder
POST /api/reminders/lead/456?userId=1&userEmail=user@example.com&userPhone=+905551234567&daysLater=3

# Reminder iptal et
POST /api/reminders/cancel/789
```

### Mesaj Örnekleri

**QUOTE_PENDING:**
- "Hala ilgileniyor musunuz? Teklifinizi değerlendirmek için bekliyoruz."
- Subject: "Teklifinizi Değerlendirmeyi Unutmayın"

**QUOTE_EXPIRING:**
- "Teklifinizin süresi yakında dolacak. Hemen değerlendirin!"
- Subject: "Teklifinizin Süresi Dolmak Üzere"

**LEAD_FOLLOW_UP:**
- "Size nasıl yardımcı olabiliriz? Sorularınız için buradayız."
- Subject: "Size Nasıl Yardımcı Olabiliriz?"

### Retry Mekanizması

- İlk başarısızlıkta: 1 saat sonra tekrar dene
- İkinci başarısızlıkta: 1 saat sonra tekrar dene
- Üçüncü başarısızlıkta: FAILED olarak işaretle

### Test Coverage

- ✅ ReminderServiceTest (2 test)
  - createQuoteReminder test
  - createQuoteExpiringReminder test

### Configuration

```properties
# Reminder Configuration
reminder.quote.pending.days=2
reminder.quote.expiring.days=1
reminder.lead.followup.days=3

# Scheduling
scheduling.enabled=true
```

### API Gateway Route

- `/api/reminders/**` → reminder-service

---

**Durum:** ✅ TAMAMLANDI
**Port:** 8041
**Versiyon:** 1.0.0
