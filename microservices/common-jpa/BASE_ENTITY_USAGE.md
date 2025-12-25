# BaseEntity Kullanım Kılavuzu

## Genel Bakış

`BaseEntity` tüm domain entity'leriniz için temel sınıftır. Aşağıdaki özellikleri sağlar:

1. **UUID (Sequential) Primary Key**: Güvenlik ve ölçeklenebilirlik için UUID kullanımı
2. **Audit Log**: CreatedAt, CreatedBy, UpdatedAt, UpdatedBy alanları
3. **Soft Delete**: isDeleted bayrağı ile fiziksel silme yerine işaretleme
4. **Optimistic Locking**: Version alanı ile eşzamanlı erişim kontrolü

## Entity Oluşturma

Tüm entity'leriniz `BaseEntity`'den türemelidir:

```java
package com.healthtourism.example.entity;

import com.healthtourism.jpa.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "patients")
@Data
@EqualsAndHashCode(callSuper = true)
public class Patient extends BaseEntity {
    
    @Column(nullable = false)
    private String firstName;
    
    @Column(nullable = false)
    private String lastName;
    
    @Column(unique = true, nullable = false)
    private String email;
    
    // BaseEntity'den otomatik olarak gelir:
    // - UUID id
    // - LocalDateTime createdAt
    // - String createdBy
    // - LocalDateTime updatedAt
    // - String updatedBy
    // - Boolean isDeleted
    // - Long version
}
```

## Repository Oluşturma

Repository'leriniz `BaseRepository`'den türemelidir:

```java
package com.healthtourism.example.repository;

import com.healthtourism.jpa.entity.BaseEntity;
import com.healthtourism.jpa.repository.BaseRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PatientRepository extends BaseRepository<Patient> {
    // BaseRepository'den otomatik olarak gelir:
    // - Optional<Patient> findByIdAndNotDeleted(UUID id)
    // - List<Patient> findAllActive()
    // - int softDeleteById(UUID id)
    // - int restoreById(UUID id)
    // - long countActive()
    // - boolean existsByIdAndNotDeleted(UUID id)
    
    // Özel sorgular ekleyebilirsiniz
    Optional<Patient> findByEmailAndNotDeleted(String email);
}
```

## Soft Delete Kullanımı

### Entity'yi Soft Delete Etme

```java
@Service
public class PatientService {
    
    @Autowired
    private PatientRepository patientRepository;
    
    public void deletePatient(UUID id) {
        // Soft delete - veri fiziksel olarak silinmez
        patientRepository.softDeleteById(id);
        
        // Veya entity üzerinden:
        Patient patient = patientRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException());
        patient.softDelete();
        patientRepository.save(patient);
    }
}
```

### Aktif Entity'leri Getirme

```java
// Tüm aktif (silinmemiş) entity'leri getir
List<Patient> activePatients = patientRepository.findAllActive();

// ID ile aktif entity getir
Optional<Patient> patient = patientRepository.findByIdAndNotDeleted(id);

// Aktif entity sayısı
long activeCount = patientRepository.countActive();
```

### Soft Delete'i Geri Alma

```java
public void restorePatient(UUID id) {
    patientRepository.restoreById(id);
    
    // Veya entity üzerinden:
    Patient patient = patientRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException());
    patient.restore();
    patientRepository.save(patient);
}
```

## JPA Specification ile Soft Delete Filtreleme

`BaseEntitySpecification` ile daha gelişmiş sorgular oluşturabilirsiniz:

```java
import com.healthtourism.jpa.specification.BaseEntitySpecification;

@Service
public class PatientService {
    
    @Autowired
    private PatientRepository patientRepository;
    
    public List<Patient> findActivePatientsCreatedBy(String userEmail) {
        Specification<Patient> spec = BaseEntitySpecification.<Patient>notDeleted()
            .and(BaseEntitySpecification.createdBy(userEmail));
        
        return patientRepository.findAll(spec);
    }
    
    public List<Patient> findPatientsCreatedBetween(
            LocalDateTime startDate, 
            LocalDateTime endDate) {
        Specification<Patient> spec = BaseEntitySpecification.<Patient>notDeleted()
            .and(BaseEntitySpecification.createdBetween(startDate, endDate));
        
        return patientRepository.findAll(spec);
    }
}
```

## Audit Log (CreatedBy/UpdatedBy)

`CreatedBy` ve `UpdatedBy` alanları otomatik olarak Spring Security context'inden doldurulur.

### Spring Security Entegrasyonu

`JpaAuditorAware` aşağıdaki sırayla kullanıcı bilgisini almaya çalışır:

1. JWT token'dan userId veya userEmail
2. UserDetails'den getUserId() veya getEmail()
3. Authentication.getName() (username/email)
4. "SYSTEM" (kimlik doğrulanmamış işlemler için)

### Özel AuditorAware

Eğer özel bir AuditorAware implementasyonu kullanmak isterseniz:

```java
@Configuration
@EnableJpaAuditing(auditorAwareRef = "customAuditorAware")
public class CustomJpaConfig {
    
    @Bean
    public AuditorAware<String> customAuditorAware() {
        return new CustomAuditorAware();
    }
}
```

## UUID Kullanımı

### Sequential UUID Avantajları

- **Güvenlik**: Integer ID'lere göre tahmin edilmesi zor
- **Ölçeklenebilirlik**: Dağıtık sistemlerde çakışma riski yok
- **Performans**: Time-based ordering sayesinde index performansı iyi
- **Entegrasyon**: Farklı sistemlerle entegrasyon kolay

### Entity ID Kullanımı

```java
// Entity oluşturma - ID otomatik oluşturulur
Patient patient = new Patient();
patient.setFirstName("John");
patient.setLastName("Doe");
patient = patientRepository.save(patient);

UUID patientId = patient.getId(); // Otomatik UUID atanır

// ID ile arama
Optional<Patient> found = patientRepository.findById(patientId);
```

## Migration Guide (AuditableEntity'den BaseEntity'ye)

Eski `AuditableEntity` kullanan entity'leri `BaseEntity`'ye geçirmek için:

### 1. Entity Güncellemesi

```java
// ÖNCE (Long ID ile)
@Entity
public class Patient extends AuditableEntity {
    // Long id otomatik geliyordu
}

// SONRA (UUID ID ile)
@Entity
public class Patient extends BaseEntity {
    // UUID id otomatik gelir, ayrıca isDeleted alanı da var
}
```

### 2. Repository Güncellemesi

```java
// ÖNCE
public interface PatientRepository extends JpaRepository<Patient, Long> {
}

// SONRA
public interface PatientRepository extends BaseRepository<Patient> {
    // BaseRepository zaten JpaRepository<Patient, UUID> extend eder
}
```

### 3. Service Güncellemesi

```java
// ÖNCE
public Patient findById(Long id) {
    return repository.findById(id).orElseThrow();
}

// SONRA
public Patient findById(UUID id) {
    return repository.findByIdAndNotDeleted(id).orElseThrow();
}
```

### 4. Database Migration

UUID kullanımına geçmek için veritabanı migration'ı gerekir:

```sql
-- PostgreSQL örneği
ALTER TABLE patients 
    DROP COLUMN id,
    ADD COLUMN id UUID PRIMARY KEY DEFAULT gen_random_uuid();

-- Mevcut Long ID'leri UUID'ye dönüştürmek için migration script gerekir
```

## Best Practices

1. **Asla fiziksel silme yapmayın**: Tüm silme işlemleri soft delete olmalı
2. **Her zaman aktif entity'leri sorgulayın**: `findAllActive()` veya `findByIdAndNotDeleted()` kullanın
3. **Audit log'ları koruyun**: CreatedBy/UpdatedBy alanları compliance için kritik
4. **UUID kullanımı**: Yeni entity'ler için mutlaka UUID kullanın
5. **Repository base kullanımı**: Tüm repository'ler BaseRepository'den türemelidir

## Örnekler

### Tam Örnek Entity + Repository + Service

```java
// Entity
@Entity
@Table(name = "appointments")
@Data
@EqualsAndHashCode(callSuper = true)
public class Appointment extends BaseEntity {
    @Column(nullable = false)
    private LocalDateTime appointmentDate;
    
    @Column(nullable = false)
    private UUID patientId;
    
    @Column(nullable = false)
    private UUID doctorId;
    
    @Enumerated(EnumType.STRING)
    private AppointmentStatus status;
}

// Repository
@Repository
public interface AppointmentRepository extends BaseRepository<Appointment> {
    List<Appointment> findByPatientIdAndNotDeleted(UUID patientId);
}

// Service
@Service
public class AppointmentService {
    @Autowired
    private AppointmentRepository appointmentRepository;
    
    public Appointment createAppointment(Appointment appointment) {
        // ID otomatik oluşturulur
        // createdAt, createdBy otomatik doldurulur
        return appointmentRepository.save(appointment);
    }
    
    public void cancelAppointment(UUID id) {
        // Soft delete
        appointmentRepository.softDeleteById(id);
    }
    
    public List<Appointment> getPatientAppointments(UUID patientId) {
        // Sadece aktif randevular
        return appointmentRepository.findByPatientIdAndNotDeleted(patientId);
    }
}
```

