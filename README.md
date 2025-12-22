# İstanbul Sağlık Turizmi Platformu

İstanbul'a sağlık turizmi sağlayan kapsamlı bir web platformu. Hastaneler, doktorlar, konaklama imkanları ve rezervasyon yönetimi içerir.

## Proje durumu ve operasyon

- **Güncel durum**: `PROJECT_STATUS.md`
- **Runbooks (backup/secrets/monitoring)**: `RUNBOOKS.md`

## Özellikler

- 🏥 **Hastane Yönetimi**: İstanbul'daki hastanelerin listesi, detayları ve havalimanına mesafeleri
- 👨‍⚕️ **Doktor Profilleri**: Uzmanlık alanları, deneyim yılları, puanlamalar ve diller
- 🏨 **Konaklama İmkanları**: Hastanelere yakın oteller ve konaklama seçenekleri
- ✈️ **Uçak Bileti**: Uluslararası uçuş arama ve rezervasyon
- 🚗 **Araç Kiralama**: Günlük, haftalık ve aylık araç kiralama seçenekleri
- 🚐 **Transfer Hizmetleri**: Havaalanı-hastane, havaalanı-otel transfer hizmetleri
- 📦 **Paket Turlar**: Tüm hizmetleri içeren kapsamlı sağlık turizmi paketleri
- 📅 **Rezervasyon Sistemi**: Randevu, konaklama, uçak, araç ve transfer rezervasyonu
- ⭐ **Değerlendirme Sistemi**: Doktor puanlama ve yorum sistemi
- 🔍 **Arama ve Filtreleme**: Şehir, uzmanlık alanı, fiyat ve tip bazlı arama

## Teknolojiler

- **Backend**: Spring Boot 4.0.0, Java 25
- **Database**: MySQL
- **Frontend**: HTML5, CSS3, JavaScript (Vanilla)
- **Security**: Spring Security
- **ORM**: JPA/Hibernate

## Kurulum

### Gereksinimler

- Java 25
- Maven 3.6+
- MySQL 8.0+

### Adımlar

1. **Veritabanını oluşturun:**
   ```sql
   CREATE DATABASE health_tourism;
   ```

2. **application.properties dosyasını düzenleyin:**
   `src/main/resources/application.properties` dosyasında MySQL kullanıcı adı ve şifrenizi güncelleyin:
   ```properties
   spring.datasource.username=root
   spring.datasource.password=your_password
   ```

3. **Projeyi derleyin:**
   ```bash
   mvn clean install
   ```

4. **Uygulamayı çalıştırın:**
   ```bash
   mvn spring-boot:run
   ```

5. **Tarayıcıda açın:**
   ```
   http://localhost:8080
   ```

## API Endpoints

### Hastaneler
- `GET /api/hospitals` - Tüm aktif hastaneleri listele
- `GET /api/hospitals/{id}` - Hastane detayları
- `GET /api/hospitals/city/{city}` - Şehre göre hastaneler
- `GET /api/hospitals/district/{district}` - İlçeye göre hastaneler
- `GET /api/hospitals/near-airport?maxDistance={km}` - Havalimanına yakın hastaneler

### Doktorlar
- `GET /api/doctors/hospital/{hospitalId}` - Hastaneye göre doktorlar
- `GET /api/doctors/specialization/{specialization}` - Uzmanlık alanına göre doktorlar
- `GET /api/doctors/{id}` - Doktor detayları
- `GET /api/doctors/hospital/{hospitalId}/top-rated` - En yüksek puanlı doktorlar

### Konaklama
- `GET /api/accommodations/hospital/{hospitalId}` - Hastaneye göre konaklama
- `GET /api/accommodations/hospital/{hospitalId}/near` - Hastaneye yakın konaklama
- `GET /api/accommodations/price?maxPrice={price}` - Fiyat aralığına göre konaklama
- `GET /api/accommodations/{id}` - Konaklama detayları

### Rezervasyonlar
- `POST /api/reservations` - Yeni rezervasyon oluştur
- `GET /api/reservations/user/{userId}` - Kullanıcının rezervasyonları
- `GET /api/reservations/number/{reservationNumber}` - Rezervasyon numarasına göre arama
- `PUT /api/reservations/{id}/status?status={status}` - Rezervasyon durumu güncelle

### Değerlendirmeler
- `GET /api/reviews/doctor/{doctorId}` - Doktorun değerlendirmeleri
- `POST /api/reviews?userId={id}&doctorId={id}&rating={1-5}&comment={text}` - Yeni değerlendirme

### Uçak Bileti
- `GET /api/flights` - Tüm uçuşları listele
- `GET /api/flights/search?departureCity={city}&arrivalCity={city}` - Uçuş ara
- `GET /api/flights/class/{flightClass}` - Sınıfa göre uçuşlar
- `GET /api/flights/price?maxPrice={price}` - Fiyat aralığına göre uçuşlar
- `GET /api/flights/{id}` - Uçuş detayları

### Araç Kiralama
- `GET /api/car-rentals` - Tüm araç kiralama seçenekleri
- `GET /api/car-rentals/type/{carType}` - Araç tipine göre filtrele
- `GET /api/car-rentals/price?maxPrice={price}` - Fiyat aralığına göre filtrele
- `GET /api/car-rentals/{id}` - Araç kiralama detayları

### Transfer Hizmetleri
- `GET /api/transfers` - Tüm transfer hizmetleri
- `GET /api/transfers/type/{serviceType}` - Hizmet tipine göre filtrele
- `GET /api/transfers/price?maxPrice={price}` - Fiyat aralığına göre filtrele
- `GET /api/transfers/{id}` - Transfer hizmeti detayları

### Paket Turlar
- `GET /api/packages` - Tüm aktif paketler
- `GET /api/packages/hospital/{hospitalId}` - Hastaneye göre paketler
- `GET /api/packages/type/{packageType}` - Paket tipine göre filtrele
- `GET /api/packages/{id}` - Paket detayları

## Veritabanı Yapısı

### Ana Tablolar
- **hospitals**: Hastane bilgileri
- **doctors**: Doktor profilleri
- **accommodations**: Konaklama seçenekleri
- **car_rentals**: Araç kiralama seçenekleri
- **flight_bookings**: Uçak bileti seçenekleri
- **transfer_services**: Transfer hizmetleri
- **travel_packages**: Paket tur seçenekleri
- **users**: Kullanıcılar
- **reservations**: Sağlık rezervasyonları
- **car_rental_reservations**: Araç kiralama rezervasyonları
- **flight_reservations**: Uçak bileti rezervasyonları
- **transfer_reservations**: Transfer rezervasyonları
- **reviews**: Doktor değerlendirmeleri

## Kullanım

1. Ana sayfada hastaneleri görüntüleyin
2. Bir hastane seçerek doktorlarını ve konaklama seçeneklerini görüntüleyin
3. Doktor seçerek rezervasyon yapın
4. İsteğe bağlı olarak konaklama ekleyin
5. Rezervasyon numaranızı kaydedin

## Geliştirme

Proje yapısı:
```
src/
├── main/
│   ├── java/com/example/HealthTourism/
│   │   ├── entity/          # Veritabanı entity'leri
│   │   ├── repository/      # JPA Repository'ler
│   │   ├── service/         # Business logic
│   │   ├── controller/      # REST API endpoints
│   │   ├── dto/            # Data Transfer Objects
│   │   └── config/         # Yapılandırma sınıfları
│   └── resources/
│       ├── static/         # Frontend dosyaları
│       └── application.properties
```

## Lisans

Bu proje demo amaçlıdır.

## İletişim

Sorularınız için issue açabilirsiniz.

