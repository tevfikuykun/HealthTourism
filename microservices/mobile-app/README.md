# 📱 Health Tourism Mobile App

## 🚀 Kurulum

### Prerequisites
- Node.js 18+
- React Native CLI
- Android Studio (Android için)
- Xcode (iOS için)

### Kurulum Adımları

```bash
# Dependencies yükle
npm install

# iOS için (sadece Mac)
cd ios && pod install && cd ..

# Android için
# Android Studio'yu aç ve emulator başlat

# Uygulamayı çalıştır
npm run android  # veya npm run ios
```

## 📱 Özellikler

- ✅ Hastane listesi ve detayları
- ✅ Doktor listesi ve profilleri
- ✅ Rezervasyon yönetimi
- ✅ Kullanıcı profili
- ✅ Giriş ve kayıt
- ✅ Biyometrik kimlik doğrulama (yakında)

## 🔧 Konfigürasyon

API URL'ini değiştirmek için:
```javascript
// src/screens/HospitalsScreen.js
const API_URL = 'https://your-api-url.com/api';
```

## 📦 Build

### Android
```bash
cd android
./gradlew assembleRelease
```

### iOS
```bash
cd ios
xcodebuild -workspace HealthTourism.xcworkspace -scheme HealthTourism -configuration Release
```

---

**Tarih**: 2024  
**Durum**: React Native App Hazır ✅

