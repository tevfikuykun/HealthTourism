# ERR_CONNECTION_REFUSED - Hızlı Çözüm

## Sorun
Vite dev server çalışmıyor. `ERR_CONNECTION_REFUSED` hatası alıyorsunuz.

## Çözüm (30 saniye)

```bash
# Terminal'de şu komutu çalıştırın:
cd microservices/frontend
npm run dev
```

Dev server başladıktan sonra browser'da `http://localhost:3000` adresine gidin.

## Eğer Port Kullanılıyorsa

```bash
# Windows - Port'u kullanan process'i bul
netstat -ano | findstr :3000

# Mac/Linux
lsof -i :3000
```

Process'i sonlandırın veya farklı port kullanın.

---

**Not:** Bu hata, dev server'ın çalışmadığını gösterir. Server'ı başlatmanız yeterlidir.

