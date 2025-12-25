# Entity Configuration Guide

## Genel Bakış

Bu kılavuz, Entity Framework'teki `IEntityTypeConfiguration<T>` benzeri bir yaklaşımla JPA entity'lerini yapılandırma konusunda rehberlik sağlar.

## 1. Fluent API Benzeri Yapılandırma

### Entity Configuration Interface

Her entity için ayrı configuration class'ları oluşturarak, entity tanımlarını temiz tutabilir ve yapılandırmayı ayrı sınıflarda yönetebilirsiniz.

### Örnek: Patient Entity Configuration

Patient entity'si için index'ler ve kısıtlamalar `@Table` annotation'ı ile tanımlanmıştır:

```java
@Entity
@Table(
    name = "patients",
    indexes = {
        @Index(name = "idx_patients_email", columnList = "email", unique = true),
        @Index(name = "idx_patients_national_id", columnList = "national_id"),
        @Index(name = "idx_patients_passport_number", columnList = "passport_number"),
        @Index(name = "idx_patients_phone", columnList = "phone"),
        @Index(name = "idx_patients_email_national_id", columnList = "email,national_id")
    }
)
public class Patient extends BaseEntity {
    // Entity fields
}
```

### Configuration Class Kullanımı

Her entity için bir configuration class'ı oluşturabilirsiniz:

```java
@Component
public class PatientConfiguration {
    
    /**
     * Get configuration documentation for Patient entity
     */
    public String getConfigurationInfo() {
        return """
            Patient Entity Configuration:
            - Unique index on email: idx_patients_email
            - Index on national_id: idx_patients_national_id
            - Index on passport_number: idx_patients_passport_number
            - Index on phone: idx_patients_phone
            - Composite index on email and national_id: idx_patients_email_national_id
            """;
    }
}
```

## 2. Indexing (Veritabanı Seviyesinde Index Tanımlama)

### Index Türleri

#### Tekil Index (Single Column Index)

```java
@Table(
    name = "users",
    indexes = {
        @Index(name = "idx_users_email", columnList = "email", unique = true)
    }
)
```

#### Çoklu Index (Composite Index)

```java
@Table(
    name = "users",
    indexes = {
        @Index(name = "idx_users_email_phone", columnList = "email,phone")
    }
)
```

### Önerilen Index'ler

#### User Entity için Index'ler

- **Email**: Unique index (authentication ve lookup için kritik)
- **Phone**: Index (telefon bazlı aramalar için)
- **National ID (TC No)**: Index (Türk kimlik numarası aramaları için)
- **Passport Number**: Index (pasaport numarası aramaları için)

```java
@Entity
@Table(
    name = "users",
    indexes = {
        @Index(name = "idx_users_email", columnList = "email", unique = true),
        @Index(name = "idx_users_phone", columnList = "phone"),
        @Index(name = "idx_users_national_id", columnList = "national_id"),
        @Index(name = "idx_users_passport_number", columnList = "passport_number"),
        @Index(name = "idx_users_email_phone", columnList = "email,phone")
    }
)
public class User extends BaseEntity {
    // ...
}
```

#### Patient Entity için Index'ler

- **Email**: Unique index
- **National ID**: Index (TC No aramaları için)
- **Passport Number**: Index (pasaport aramaları için)
- **Phone**: Index (telefon aramaları için)

## 3. Seed Data (Başlangıç Verisi)

### Seed Data Entity'leri

Seed data entity'leri `com.healthtourism.jpa.entity.seed` paketinde tanımlanmıştır:

- **Country**: Ülkeler (ISO kodları, telefon kodları, vb.)
- **TreatmentBranch**: Tedavi branşları (Kardiyoloji, Ortopedi, vb.)
- **Role**: Kullanıcı rolleri (USER, ADMIN, DOCTOR, vb.)

### DataInitializer Kullanımı

`DataInitializer` sınıfı, uygulama başlangıcında otomatik olarak seed data'yı yükler:

```java
@Component
@Order(1)
public class DataInitializer implements CommandLineRunner {
    
    @Autowired
    private CountryRepository countryRepository;
    
    @Autowired
    private TreatmentBranchRepository treatmentBranchRepository;
    
    @Autowired
    private RoleRepository roleRepository;
    
    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (countryRepository.countActive() == 0) {
            seedCountries();
        }
        // ...
    }
}
```

### Seed Data İçeriği

#### Countries (Ülkeler)

51 ülke otomatik olarak yüklenir:
- ABD, Türkiye, İngiltere, Almanya, Fransa, vb.
- ISO kodları, telefon kodları, yerel isimler

#### Treatment Branches (Tedavi Branşları)

25 tedavi branşı otomatik olarak yüklenir:
- Cardiology (Kardiyoloji)
- Orthopedics (Ortopedi)
- Oncology (Onkoloji)
- Neurology (Nöroloji)
- vb.

#### Roles (Roller)

8 rol otomatik olarak yüklenir:
- USER, ADMIN, DOCTOR, NURSE, PATIENT, STAFF, MODERATOR, SUPER_ADMIN

### Seed Data'ya Erişim

```java
@Service
public class CountryService {
    
    @Autowired
    private CountryRepository countryRepository;
    
    public List<Country> getAllActiveCountries() {
        return countryRepository.findAllActive();
    }
    
    public Optional<Country> findByCode(String code) {
        return countryRepository.findByCode(code);
    }
}
```

## 4. Best Practices

### 1. Index Stratejisi

- **Unique Index**: Email, username gibi benzersiz alanlar için
- **Non-Unique Index**: Sık aranan ama benzersiz olmayan alanlar için (TC No, Pasaport No)
- **Composite Index**: Birden fazla alanın birlikte arandığı durumlarda

### 2. Index Naming Convention

```
idx_{table_name}_{column_name(s)}
```

Örnekler:
- `idx_users_email`
- `idx_patients_national_id`
- `idx_users_email_phone` (composite)

### 3. Seed Data Yönetimi

- Seed data **idempotent** olmalı (birden fazla çalıştırıldığında sorun yaratmamalı)
- Seed data sadece tablo boşsa yüklenmeli
- Seed data güncellemeleri migration script'leri ile yapılmalı

### 4. Entity Configuration Organizasyonu

- Her entity için ayrı configuration class'ı oluşturun
- Configuration class'larını `config.entity` paketinde tutun
- Configuration class'larına `@Component` annotation'ı ekleyin

## 5. Örnekler

### Tam Örnek: User Entity Configuration

```java
// Entity
@Entity
@Table(
    name = "users",
    indexes = {
        @Index(name = "idx_users_email", columnList = "email", unique = true),
        @Index(name = "idx_users_phone", columnList = "phone"),
        @Index(name = "idx_users_national_id", columnList = "national_id"),
        @Index(name = "idx_users_passport_number", columnList = "passport_number")
    }
)
public class User extends BaseEntity {
    @Column(nullable = false, unique = true)
    private String email;
    
    @Column(name = "national_id")
    private String nationalId;
    
    @Column(name = "passport_number")
    private String passportNumber;
    
    // ...
}

// Configuration
@Component
public class UserConfiguration {
    public String getConfigurationInfo() {
        return "User Entity Configuration with indexes on email, phone, national_id, passport_number";
    }
}
```

## 6. Migration ve Güncelleme

### Yeni Index Ekleme

1. Entity'deki `@Table` annotation'ına yeni index ekleyin
2. Migration script oluşturun (Flyway veya Liquibase)
3. Configuration class'ını güncelleyin

### Seed Data Güncelleme

1. `DataInitializer`'daki seed metodunu güncelleyin
2. Mevcut veriyi güncellemek için ayrı bir migration script oluşturun
3. Test edin

## 7. Performans İpuçları

- **Index sayısını dengeli tutun**: Çok fazla index yazma performansını düşürür
- **Composite index'leri dikkatli kullanın**: Sadece gerçekten gerekli olduğunda
- **Unique index'leri zorunlu alanlarda kullanın**: Email, username gibi
- **Nullable alanlarda index kullanımını değerlendirin**: NULL değerler index'te yer kaplar

## 8. Troubleshooting

### Index Oluşmuyor

- Hibernate DDL auto mode'u kontrol edin (`spring.jpa.hibernate.ddl-auto`)
- Migration script'lerin çalıştığını doğrulayın
- Veritabanı loglarını kontrol edin

### Seed Data Yüklenmiyor

- `DataInitializer`'ın `@Component` annotation'ına sahip olduğunu kontrol edin
- `@Order` annotation'ı ile diğer initializer'lardan önce çalıştığını doğrulayın
- Transaction'ın başarılı olduğunu kontrol edin

