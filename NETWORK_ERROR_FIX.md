# Network Error Toast Spam Fix

## Problem
Sürekli "Network error. Please check your connection." toast bildirimleri gösteriliyordu. Bu, backend servislerinin çalışmadığı durumlarda sürekli API çağrıları yapılması ve her başarısız çağrıda toast gösterilmesi nedeniyle oluşuyordu.

## Çözümler

### 1. Toast Throttling (api.js)
- Aynı hata mesajı 5 saniye içinde tekrar gösterilmiyor
- Network hataları için warning toast kullanılıyor (error yerine)
- Backend erişilebilirliği takip ediliyor
- Toast cache mekanizması eklendi

### 2. React Query Retry Optimizasyonu (App.jsx)
- Network hatalarında retry yapılmıyor
- Diğer hatalarda maksimum 1 retry
- Window focus ve reconnect'te otomatik refetch kapatıldı
- Stale time 5 dakika olarak ayarlandı

### 3. Polling Query Optimizasyonu
- Network hatalarında polling otomatik durduruluyor
- `createPollingQueryConfig` utility fonksiyonu eklendi
- NotificationBell component'i güncellendi

## Kullanım

### Yeni Polling Query'ler İçin
```javascript
import { createPollingQueryConfig } from '../utils/queryConfig';

const { data } = useQuery({
  queryKey: ['my-data'],
  queryFn: () => myService.getData(),
  ...createPollingQueryConfig(30000), // 30 saniyede bir, network hatasında durur
});
```

### API Çağrılarında Toast'u Bastırmak İçin
```javascript
api.get('/endpoint', {
  suppressErrorToast: true // Bu çağrıda toast gösterilmez
});
```

## Sonuç
- Network hatalarında toast spam'i önlendi
- Backend çalışmadığında gereksiz retry'lar durduruldu
- Kullanıcı deneyimi iyileştirildi
- Performans optimizasyonu sağlandı






