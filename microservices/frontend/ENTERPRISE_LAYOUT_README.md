# EnterpriseLayout - Kurumsal Layout Yapısı

Bu dokümantasyon, yeni oluşturulan EnterpriseLayout bileşenlerinin kullanımını açıklar.

## 📁 Dosya Yapısı

```
src/
├── layouts/
│   └── EnterpriseLayout.jsx          # Ana layout bileşeni
└── components/
    └── layout/
        ├── Navigation.jsx             # Mega Menu navigasyon
        ├── UserMenu.jsx               # Kullanıcı menü bileşeni
        └── NotificationDrawer.jsx     # Bildirim paneli
```

## 🎯 Özellikler

### 1. EnterpriseLayout.jsx
Ana layout bileşeni. Şu özelliklere sahiptir:

- **Glassmorphism Sticky Header**: Scroll olduğunda yarı saydam efektle küçülen header
- **Responsive Design**: Tüm ekran boyutlarında mükemmel görünüm
- **Framer Motion Animasyonları**: Sayfa geçişlerinde akıcı animasyonlar

### 2. Navigation.jsx
Mega Menu ile zengin navigasyon:

- **Tedaviler Menüsü**: Estetik, Diş, Sağlık Kontrolü, Wellness kategorileri
- **Hastaneler Menüsü**: Şehir, Uzmanlık, Özel Hizmetler kategorileri
- **Destek Menüsü**: Seyahat, Yardım, Kaynaklar kategorileri
- Hover ile açılan zengin içerikli menüler
- İkonlu kategori gösterimi

### 3. UserMenu.jsx
Kullanıcı profil menüsü:

- Avatar gösterimi
- Dashboard, Profil, Rezervasyonlar, Favoriler, Ödemeler linkleri
- Ayarlar ve Çıkış Yap seçenekleri
- Kullanıcı bilgileri header'da

### 4. NotificationDrawer.jsx
Bildirim yönetim paneli:

- Backend'den bildirim çekme
- Okunmamış bildirim sayacı
- Bildirimleri okundu olarak işaretleme
- Tüm bildirimleri okundu olarak işaretleme
- Bildirim türlerine göre ikon ve renk gösterimi
- Bildirim tıklamalarında ilgili sayfaya yönlendirme

### 5. Footer.jsx (Güncellendi)
Newsletter kayıt formu eklendi:

- E-posta ile newsletter kaydı
- Başarılı kayıt bildirimi
- Local storage ile geçici kayıt (backend entegrasyonu için hazır)

## 🚀 Kullanım

### EnterpriseLayout'u Uygulamaya Entegre Etme

1. **App.jsx'te Header'ı EnterpriseLayout ile değiştirin:**

```jsx
import EnterpriseLayout from './layouts/EnterpriseLayout';

// AppContent içinde:
<EnterpriseLayout>
  <Routes>
    {/* Routes */}
  </Routes>
</EnterpriseLayout>
```

2. **Veya belirli sayfalar için kullanın:**

```jsx
import EnterpriseLayout from '../layouts/EnterpriseLayout';

function SomePage() {
  return (
    <EnterpriseLayout>
      <Container>
        {/* Page content */}
      </Container>
    </EnterpriseLayout>
  );
}
```

## 🔧 Yapılandırma

### NotificationDrawer Backend Entegrasyonu

NotificationDrawer, `notificationService.getByUser()` kullanarak bildirimleri çeker. Backend API'nin şu formatta veri döndürmesi gerekir:

```json
{
  "data": [
    {
      "id": 1,
      "title": "Bildirim Başlığı",
      "message": "Bildirim içeriği",
      "type": "INFO",
      "priority": "HIGH",
      "read": false,
      "link": "/reservations",
      "createdAt": "2024-01-01T00:00:00Z"
    }
  ]
}
```

### Footer Newsletter Backend Entegrasyonu

Footer'daki newsletter formu şu anda local storage kullanıyor. Backend entegrasyonu için:

```jsx
// Footer.jsx içinde handleNewsletterSubmit fonksiyonunda:
await api.post('/newsletter/subscribe', { email: newsletterEmail });
```

## 📝 Çeviri Anahtarları

Yeni çeviri anahtarları eklemeniz gerekebilir:

```json
{
  "nav": {
    "treatments": "Tedaviler",
    "support": "Destek"
  },
  "treatments": {
    "cosmetic": "Estetik ve Güzellik",
    "dental": "Diş Hekimliği",
    "health": "Sağlık Kontrolü",
    "wellness": "Wellness & Spa"
  },
  "footer": {
    "newsletter": "Bülten",
    "newsletterDesc": "Yeni hizmetler ve kampanyalardan haberdar olun",
    "emailPlaceholder": "E-posta adresiniz",
    "subscribed": "Kayıt başarılı!",
    "invalidEmail": "Lütfen geçerli bir e-posta adresi girin",
    "newsletterSuccess": "Bülten kaydınız başarıyla oluşturuldu!",
    "newsletterError": "Bir hata oluştu. Lütfen tekrar deneyin."
  }
}
```

## 🎨 Özelleştirme

### Header Stilini Değiştirme

EnterpriseLayout.jsx içinde `isScrolled` durumuna göre stil ayarları yapılabilir:

```jsx
backgroundColor: isScrolled ? 'rgba(255, 255, 255, 0.8)' : 'transparent',
backdropFilter: isScrolled ? 'blur(12px)' : 'none',
```

### Mega Menu İçeriğini Özelleştirme

Navigation.jsx içindeki `treatmentCategories`, `hospitalCategories` ve `supportCategories` array'lerini düzenleyin.

## 📱 Responsive Breakpoints

- **xs**: < 600px (Mobil)
- **sm**: 600px - 960px (Tablet)
- **md**: 960px - 1280px (Küçük Desktop)
- **lg**: >= 1280px (Desktop - Mega Menu görünür)

## 🔐 Güvenlik

- UserMenu ve NotificationDrawer sadece authenticated kullanıcılar için görünür
- NotificationDrawer `isAuthenticated` kontrolü yapar
- UserMenu `user` ve `onLogout` prop'larını bekler

## 🐛 Bilinen Sorunlar

- NotificationDrawer backend 500 hatası verirse sessizce başarısız olur (zaten handle ediliyor)
- Newsletter formu şu anda local storage kullanıyor, backend entegrasyonu gerekli

## 📚 İlgili Dosyalar

- `src/hooks/useAuth.js` - Authentication hook
- `src/services/api.js` - API servisleri
- `src/i18n.js` - Çeviri yapılandırması



