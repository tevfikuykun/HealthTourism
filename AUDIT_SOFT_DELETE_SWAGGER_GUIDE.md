# Audit Logging, Soft Delete ve Swagger Entegrasyonu Kılavuzu

## 1. Swagger/OpenAPI Entegrasyonu ✅

### Özellikler
- ✅ JWT Bearer token authentication desteği
- ✅ Swagger UI'da "Authorize" butonu ile token girişi
- ✅ Tüm endpoint'lerin görsel dokümantasyonu
- ✅ Request/Response örnekleri

### Kullanım

1. **Swagger UI'ya Erişim:**
   ```
   http://localhost:8080/swagger-ui.html
   ```

2. **API Docs JSON:**
   ```
   http://localhost:8080/api-docs
   ```

3. **JWT Token ile Yetkilendirme:**
   - Önce `/api/v1/auth/login` veya `/api/v1/auth/register` ile token alın
   - Swagger UI'da sağ üstteki **"Authorize"** butonuna tıklayın
   - Token'ı **"Bearer {token}"** formatında girin (örnek: `Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...`)
   - Artık korumalı endpoint'leri test edebilirsiniz

### Yapılandırma
- **Dosya:** `SwaggerConfig.java`
- **JWT Security:** Bearer token şeması otomatik olarak eklenir
- **Server URLs:** Development ve Production server'ları yapılandırılabilir

---

## 2. JPA Auditing (Audit Logging) ✅

### Özellikler
- ✅ **createdDate:** Oluşturulma tarihi (otomatik)
- ✅ **createdBy:** Oluşturan kullanıcı email'i (Spring Security'den)
- ✅ **lastModifiedDate:** Son güncelleme tarihi (otomatik)
- ✅ **lastModifiedBy:** Son güncelleyen kullanıcı email'i (Spring Security'den)

### Nasıl Kullanılır?

#### 1. Entity'yi BaseEntity'den Extend Edin

```java
@Entity
@Table(name = "hospitals")
public class Hospital extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    // ... diğer alanlar
    
    // Audit field'ları BaseEntity'den otomatik gelir:
    // - createdDate
    // - createdBy
    // - lastModifiedDate
    // - lastModifiedBy
}
```

#### 2. Veritabanı Migration (Opsiyonel)

Eğer mevcut tablolara audit field'ları eklemek istiyorsanız:

```sql
ALTER TABLE hospitals 
ADD COLUMN created_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
ADD COLUMN created_by VARCHAR(255) NOT NULL DEFAULT 'SYSTEM',
ADD COLUMN last_modified_date DATETIME,
ADD COLUMN last_modified_by VARCHAR(255);
```

#### 3. Kullanım Örnekleri

**Oluşturma:**
```java
Hospital hospital = new Hospital();
hospital.setName("Acıbadem Hastanesi");
hospitalRepository.save(hospital);
// createdDate ve createdBy otomatik olarak doldurulur
```

**Güncelleme:**
```java
hospital.setName("Acıbadem Hastanesi - Kadıköy");
hospitalRepository.save(hospital);
// lastModifiedDate ve lastModifiedBy otomatik olarak güncellenir
```

### Yapılandırma
- **Dosya:** `JpaAuditingConfig.java`
- **Auditor Provider:** Spring Security context'inden kullanıcı email'ini alır
- **Anonymous User:** Authentication yoksa "SYSTEM" kullanılır

---

## 3. Soft Delete ✅

### Özellikler
- ✅ **deleted:** Silinme durumu flag'i (false = aktif, true = silinmiş)
- ✅ **deletedAt:** Silinme tarihi
- ✅ **deletedBy:** Silen kullanıcı email'i
- ✅ **Restore:** Silinmiş kayıtları geri getirme
- ✅ **Otomatik Filtreleme:** Sadece aktif kayıtlar sorgulanır

### Nasıl Kullanılır?

#### 1. Entity'yi BaseEntity'den Extend Edin

```java
@Entity
@Table(name = "hospitals")
public class Hospital extends BaseEntity {
    // ... alanlar
}
```

#### 2. Repository'yi SoftDeleteRepository'den Extend Edin

```java
public interface HospitalRepository extends JpaRepository<Hospital, Long>, 
                                           SoftDeleteRepository<Hospital, Long> {
    
    // Mevcut sorgularınız
    List<Hospital> findByCity(String city);
    
    // Soft delete metodları otomatik gelir:
    // - findAllActive()
    // - findByIdActive(id)
    // - findAllDeleted()
    // - softDeleteById(id, deletedAt, deletedBy)
    // - restoreById(id)
}
```

#### 3. Veritabanı Migration

```sql
ALTER TABLE hospitals 
ADD COLUMN deleted BOOLEAN NOT NULL DEFAULT FALSE,
ADD COLUMN deleted_at DATETIME,
ADD COLUMN deleted_by VARCHAR(255);
```

#### 4. Kullanım Örnekleri

**Tüm Aktif Kayıtları Getir:**
```java
List<Hospital> activeHospitals = hospitalRepository.findAllActive();
// Sadece deleted = false olan kayıtlar döner
```

**ID ile Aktif Kayıt Getir:**
```java
Optional<Hospital> hospital = hospitalRepository.findByIdActive(1L);
// Eğer kayıt silinmişse Optional.empty() döner
```

**Soft Delete:**
```java
// Manuel soft delete
Hospital hospital = hospitalRepository.findById(1L).orElseThrow();
hospital.softDelete("admin@example.com"); // Silen kullanıcı email'i
hospitalRepository.save(hospital);

// Veya repository metodu ile
String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
hospitalRepository.softDeleteById(1L, LocalDateTime.now(), currentUser);
```

**Restore (Geri Getir):**
```java
Hospital hospital = hospitalRepository.findByIdIncludingDeleted(1L).orElseThrow();
hospital.restore();
hospitalRepository.save(hospital);

// Veya repository metodu ile
hospitalRepository.restoreById(1L);
```

**Silinmiş Kayıtları Getir:**
```java
List<Hospital> deletedHospitals = hospitalRepository.findAllDeleted();
// Sadece deleted = true olan kayıtlar döner
```

**Tüm Kayıtları Getir (Silinmişler Dahil):**
```java
List<Hospital> allHospitals = hospitalRepository.findAll();
// Hem aktif hem silinmiş kayıtlar döner
```

### Servis Katmanında Kullanım

```java
@Service
@RequiredArgsConstructor
public class HospitalService {
    
    private final HospitalRepository hospitalRepository;
    
    public List<HospitalDTO> getAllActiveHospitals() {
        // Soft delete otomatik olarak filtrelenir
        return hospitalRepository.findAllActive()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
    
    public void deleteHospital(Long id, String deletedBy) {
        // Fiziksel silme yerine soft delete
        hospitalRepository.softDeleteById(id, LocalDateTime.now(), deletedBy);
    }
    
    public void restoreHospital(Long id) {
        hospitalRepository.restoreById(id);
    }
}
```

### Önemli Notlar

1. **Veri Bütünlüğü:** Sağlık turizmi sistemlerinde verilerin fiziksel olarak silinmesi veri bütünlüğünü bozar (tıbbi kayıtlar, ödemeler, vb.). Bu yüzden soft delete kullanılmalıdır.

2. **Performans:** Silinmiş kayıtlar sorgulardan otomatik olarak filtrelenir. Eğer silinmiş kayıtları da görmek istiyorsanız, `findByIdIncludingDeleted()` veya `findAll()` kullanın.

3. **Mevcut Entity'lere Ekleme:** Mevcut entity'leri BaseEntity'den extend ettiğinizde, veritabanı migration'ı çalıştırmayı unutmayın.

---

## 4. Örnek: Hospital Entity'sini Güncelleme

### Önceki Durum:
```java
@Entity
@Table(name = "hospitals")
public class Hospital {
    @Id
    private Long id;
    private String name;
    // ... diğer alanlar
}
```

### Yeni Durum:
```java
@Entity
@Table(name = "hospitals")
public class Hospital extends BaseEntity {
    @Id
    private Long id;
    private String name;
    // ... diğer alanlar
    
    // BaseEntity'den gelen field'lar:
    // - createdDate
    // - createdBy
    // - lastModifiedDate
    // - lastModifiedBy
    // - deleted
    // - deletedAt
    // - deletedBy
}
```

### Repository Güncelleme:
```java
public interface HospitalRepository extends JpaRepository<Hospital, Long>, 
                                           SoftDeleteRepository<Hospital, Long> {
    // Mevcut sorgularınız
}
```

---

## 5. Güvenlik Notları

1. **Audit Logging:** Tüm değişiklikler (kim, ne zaman, ne yaptı) otomatik olarak kaydedilir. Bu, HIPAA/KVKK uyumluluğu için kritiktir.

2. **Soft Delete:** Veriler fiziksel olarak silinmez, sadece işaretlenir. Bu sayede:
   - Veri bütünlüğü korunur
   - Geri getirme (restore) mümkündür
   - Audit trail korunur

3. **JWT Security:** Swagger UI'da token ile yetkilendirme yapılabilir. Production'da Swagger UI'ı devre dışı bırakmayı düşünün.

---

## 6. Gelecek İyileştirmeler

- [ ] **Audit Log Service:** Veritabanı değişikliklerini ayrı bir audit_log tablosuna kaydetme
- [ ] **Audit Log Query API:** "Kim, ne zaman, ne yaptı?" sorgusu için endpoint'ler
- [ ] **Bulk Soft Delete:** Toplu silme işlemleri
- [ ] **Permanent Delete:** Admin için kalıcı silme (belirli koşullarda)
- [ ] **Audit Log Retention:** Eski audit log'larını arşivleme

