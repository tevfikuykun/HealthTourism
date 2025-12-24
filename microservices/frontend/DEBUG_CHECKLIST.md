# Debug Checklist - Boş Sayfa Sorunu

## Hızlı Kontroller:

1. **Browser Console Kontrolü:**
   - F12 tuşuna basın
   - Console tab'ına gidin
   - Kırmızı hatalar var mı? Varsa paylaşın

2. **Network Tab Kontrolü:**
   - Network tab'ına gidin
   - Sayfayı yenileyin (F5)
   - main.jsx, App.jsx gibi dosyalar yükleniyor mu?
   - 404 veya 500 hatası var mı?

3. **Vite Dev Server:**
   - Terminal'de hata var mı?
   - Dev server çalışıyor mu? (http://localhost:3000)

## Olası Sorunlar:

1. **Import Hatası:**
   - Blog.jsx veya BlogPost.jsx'te import hatası olabilir
   - `date-fns` yüklü mü kontrol edin

2. **i18n Hatası:**
   - i18n yüklenemiyor olabilir
   - Console'da i18n hataları var mı?

3. **React Render Hatası:**
   - ErrorBoundary hata yakalıyor mu?
   - Console'da React hataları var mı?

## Çözüm Adımları:

1. **Vite Cache Temizle:**
   ```powershell
   Remove-Item -Recurse -Force node_modules\.vite
   npm run dev
   ```

2. **Node Modules Yeniden Yükle:**
   ```powershell
   Remove-Item -Recurse -Force node_modules
   npm install
   npm run dev
   ```

3. **Browser Cache Temizle:**
   - Ctrl+Shift+Delete
   - Cache'i temizle
   - Sayfayı hard refresh yap (Ctrl+F5)

