# ERR_NETWORK_CHANGED Hatası - Çözüm Rehberi

## Hata Açıklaması

`ERR_NETWORK_CHANGED` hatası, Vite dev server'ın Hot Module Replacement (HMR) WebSocket bağlantısının kesildiğini gösterir. Bu genellikle:

1. **Network bağlantısı değişikliği** (WiFi değişimi, VPN bağlantısı)
2. **Vite dev server'ın durması** veya restart edilmesi
3. **Port çakışması** (3000 portu başka bir uygulama tarafından kullanılıyor)
4. **Browser cache sorunları**

## Otomatik Çözümler (Yapılandırıldı)

Projeye aşağıdaki iyileştirmeler eklendi:

### 1. Vite Config İyileştirmeleri
- HMR reconnect ayarları
- Network değişikliklerinde otomatik yeniden bağlanma
- Port çakışması durumunda otomatik alternatif port

### 2. Network Error Handler
- `utils/networkErrorHandler.js` - Otomatik hata tespiti ve yeniden bağlanma
- Online/Offline event monitoring
- Vite HMR reconnect mekanizması

### 3. Error Boundary İyileştirmeleri
- Network hatalarını özel olarak işleme
- Kullanıcıya anlamlı hata mesajları

## Manuel Çözüm Adımları

### Adım 1: Dev Server'ı Yeniden Başlat

```bash
# Terminal'de Ctrl+C ile durdur
# Sonra tekrar başlat:
cd microservices/frontend
npm run dev
```

### Adım 2: Browser Cache'i Temizle

**Chrome/Edge:**
1. `Ctrl+Shift+Delete` (Windows) veya `Cmd+Shift+Delete` (Mac)
2. "Cached images and files" seçeneğini işaretle
3. "Clear data" tıkla

**Veya Hard Refresh:**
- `Ctrl+Shift+R` (Windows) veya `Cmd+Shift+R` (Mac)

### Adım 3: Port Kontrolü

3000 portu kullanılıyorsa:

```bash
# Windows
netstat -ano | findstr :3000

# Mac/Linux
lsof -i :3000
```

Eğer port kullanılıyorsa, o process'i sonlandırın veya Vite config'de farklı bir port kullanın.

### Adım 4: Node Modules ve Cache Temizle

```bash
cd microservices/frontend

# node_modules ve lock file'ı sil
rm -rf node_modules package-lock.json

# npm cache temizle
npm cache clean --force

# Yeniden kur
npm install

# Dev server'ı başlat
npm run dev
```

### Adım 5: Network Bağlantısını Kontrol Et

- WiFi bağlantınızı kontrol edin
- VPN kullanıyorsanız, geçici olarak kapatmayı deneyin
- Farklı bir network'e bağlanmayı deneyin

## Gelişmiş Çözümler

### Vite Dev Server'ı Farklı Port'ta Çalıştırma

`vite.config.js` dosyasında:

```js
server: {
  port: 3001, // Farklı port
  // ...
}
```

### HMR'i Devre Dışı Bırakma (Geçici)

`vite.config.js` dosyasında:

```js
server: {
  hmr: false, // HMR'i kapat
  // ...
}
```

**Not:** Bu durumda hot reload çalışmaz, her değişiklikte sayfayı manuel yenilemeniz gerekir.

## Önleme

1. **Stable Network:** Development sırasında stabil bir network bağlantısı kullanın
2. **VPN:** Gerekmedikçe VPN'i kapatın
3. **Port Management:** 3000 portunu başka uygulamalar için kullanmayın
4. **Regular Restarts:** Uzun development session'larında dev server'ı periyodik olarak yeniden başlatın

## Hata Devam Ederse

Eğer hata devam ederse:

1. **Browser Console'u kontrol edin:** `F12` > Console tab
2. **Network tab'ı kontrol edin:** Hangi request'lerin başarısız olduğunu görün
3. **Vite terminal output'unu kontrol edin:** Hata mesajlarını inceleyin

## İletişim

Sorun devam ederse, aşağıdaki bilgileri toplayın:

- Browser ve versiyonu
- İşletim sistemi
- Network durumu (online/offline)
- Vite terminal output
- Browser console hataları

---

**Son Güncelleme:** Bu çözümler projeye otomatik olarak entegre edilmiştir. Çoğu durumda hata otomatik olarak çözülecektir.

