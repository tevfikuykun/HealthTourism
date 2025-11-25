# MySQL Database Schemas

Bu dizin, tüm microservice'ler için MySQL veritabanı şemalarını içerir.

## Veritabanları

1. **accommodation_db** - Konaklama servisi
2. **car_rental_db** - Araç kiralama servisi
3. **flight_db** - Uçak bileti servisi
4. **transfer_db** - Transfer servisi
5. **package_db** - Paket tur servisi
6. **notification_db** - Bildirim servisi
7. **telemedicine_db** - Telemedicine servisi
8. **patient_followup_db** - Hasta takip servisi
9. **faq_db** - SSS servisi
10. **favorite_db** - Favoriler servisi
11. **appointment_calendar_db** - Randevu takvimi servisi
12. **contact_db** - İletişim servisi
13. **testimonial_db** - Hasta hikayeleri servisi
14. **insurance_db** - Sigorta servisi
15. **auth_db** - Kimlik doğrulama servisi
16. **monitoring_db** - İzleme servisi
17. **logging_db** - Loglama servisi
18. **file_storage_db** - Dosya depolama servisi
19. **admin_db** - Admin servisi

## Kullanım

### Docker Compose ile

Docker Compose dosyası (`docker-compose.yml`) tüm veritabanlarını otomatik olarak oluşturur. Ancak şemaları manuel olarak oluşturmak isterseniz:

```bash
# Tüm veritabanlarını oluştur
mysql -u root -proot -h localhost -P 3307 < accommodation_db.sql
mysql -u root -proot -h localhost -P 3308 < car_rental_db.sql
# ... diğer veritabanları için benzer şekilde
```

### Tek Seferde Tümünü Oluşturma

```bash
# Her veritabanı için ayrı ayrı
for db in accommodation car_rental flight transfer package notification telemedicine patient_followup faq favorite appointment_calendar contact testimonial insurance auth monitoring logging file_storage admin; do
  mysql -u root -proot -h localhost -P 3307 < ${db}_db.sql
done
```

## Notlar

- Tüm tablolar `utf8mb4` karakter seti ve `utf8mb4_unicode_ci` collation kullanır
- Tüm tablolarda uygun index'ler tanımlanmıştır
- Foreign key constraint'ler servisler arası bağımlılıkları azaltmak için kullanılmamıştır (microservice pattern)
- `spring.jpa.hibernate.ddl-auto=update` ayarı ile JPA otomatik olarak şemayı güncelleyebilir


