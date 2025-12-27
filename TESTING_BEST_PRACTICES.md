# Testing Best Practices - ID Generation ve @Transactional

## ⚠️ Kritik: ID Generation ve @Transactional

### Problem

`@Transactional` annotation'ı kullanıldığında:
- ✅ Her test sonrası rollback yapılır (veritabanı temiz kalır)
- ❌ Ancak ID generation (Sequence/Identity) rollback'ten etkilenmez
- ❌ ID sequence'ler artmaya devam eder

**Örnek Senaryo:**
```java
@Transactional
class HospitalIntegrationTest {
    
    @Test
    void test1() {
        Hospital hospital = createHospital();
        // ID = 1 (rollback yapılır ama sequence artmaya devam eder)
    }
    
    @Test
    void test2() {
        Hospital hospital = createHospital();
        // ID = 2 (önceki test rollback olsa bile)
    }
    
    @Test
    void test3() {
        Hospital hospital = createHospital();
        // ID = 3 (önceki testler rollback olsa bile)
    }
}
```

---

## ✅ Çözüm: Dinamik ID Kontrolü

### ❌ Kötü Yaklaşım (Statik ID Kontrolü)

```java
@Test
void createHospital_ShouldReturnHospital() {
    HospitalDTO created = hospitalService.createHospital(hospital);
    
    // HATALI: Statik ID kontrolü
    assertThat(created.getId()).isEqualTo(1L); // Test sırasına bağlı, güvenilir değil!
}
```

**Sorunlar:**
- Test sırasına bağlı (test1 çalışırsa ID=1, test2 çalışırsa ID=2)
- Paralel test çalıştırmada sorun çıkarır
- Test isolation'ı bozar

---

### ✅ İyi Yaklaşım (Dinamik ID Kontrolü)

```java
@Test
void createHospital_ShouldReturnHospital() {
    // When
    HospitalDTO created = hospitalService.createHospital(hospital);
    
    // Then - Dinamik ID kontrolü
    assertThat(created).isNotNull();
    assertThat(created.getId()).isNotNull(); // ID null olmamalı
    assertThat(created.getId()).isPositive(); // ID pozitif olmalı
    
    // Retrieve ve dinamik ID ile karşılaştır
    HospitalDTO retrieved = hospitalService.getHospitalById(created.getId());
    assertThat(retrieved.getId()).isEqualTo(created.getId()); // Dinamik ID ile karşılaştır
    assertThat(retrieved.getName()).isEqualTo(created.getName());
}
```

**Avantajlar:**
- ✅ Test sırasından bağımsız
- ✅ Paralel test çalıştırmada sorun çıkarmaz
- ✅ Test isolation'ı korur
- ✅ Daha güvenilir testler

---

## 📋 Best Practices

### 1. ID Kontrolü Pattern'leri

**✅ Doğru:**
```java
// ID'nin null olmadığını kontrol et
assertThat(result.getId()).isNotNull();

// ID'nin pozitif olduğunu kontrol et
assertThat(result.getId()).isPositive();

// Dinamik ID ile karşılaştır
assertThat(retrieved.getId()).isEqualTo(created.getId());

// Liste içinde dinamik ID kontrolü
assertThat(results)
    .extracting(EntityDTO::getId)
    .containsExactlyInAnyOrder(created1.getId(), created2.getId());
```

**❌ Yanlış:**
```java
// Statik ID kontrolü - KULLANMA!
assertThat(result.getId()).isEqualTo(1L);
assertThat(result.getId()).isEqualTo(2L);

// Statik ID ile liste kontrolü - KULLANMA!
assertThat(results)
    .extracting(EntityDTO::getId)
    .containsExactly(1L, 2L);
```

---

### 2. @Transactional Kullanımı

**Integration Testlerde:**
```java
@SpringBootTest
@ActiveProfiles("test")
@Transactional // Her test sonrası rollback
class HospitalIntegrationTest {
    
    @BeforeEach
    void setUp() {
        // Optional: Explicit cleanup
        hospitalRepository.deleteAll();
    }
    
    @Test
    void test() {
        // Test code - Rollback otomatik yapılır
    }
}
```

**Avantajlar:**
- ✅ Her test sonrası veritabanı temiz kalır
- ✅ Test isolation sağlanır
- ✅ Test execution hızlıdır (rollback, delete'den hızlı)

**Dikkat:**
- ⚠️ ID sequence'ler rollback'ten etkilenmez
- ⚠️ Bu yüzden dinamik ID kontrolü kullanılmalı

---

### 3. Test Data Setup

**✅ İyi: Her test için fresh data**
```java
@BeforeEach
void setUp() {
    hospitalRepository.deleteAll();
    // Her test için temiz başlangıç
}

@Test
void test() {
    Hospital hospital = createTestHospital();
    HospitalDTO created = hospitalService.createHospital(hospital);
    
    // Dinamik ID kontrolü
    assertThat(created.getId()).isNotNull();
}
```

**❌ Kötü: Shared test data**
```java
private static Hospital sharedHospital; // Shared state - KULLANMA!

@Test
void test() {
    // Shared data kullanımı test isolation'ı bozar
}
```

---

### 4. Assertion Best Practices

**✅ İyi: Comprehensive assertions**
```java
@Test
void createHospital_ShouldPersistCorrectly() {
    // When
    HospitalDTO created = hospitalService.createHospital(hospital);
    
    // Then - Kapsamlı kontroller
    assertThat(created).isNotNull();
    assertThat(created.getId()).isNotNull(); // Dinamik ID
    assertThat(created.getId()).isPositive();
    assertThat(created.getName()).isEqualTo(hospital.getName());
    assertThat(created.getCity()).isEqualTo(hospital.getCity());
    
    // Retrieve ve verify
    HospitalDTO retrieved = hospitalService.getHospitalById(created.getId());
    assertThat(retrieved).isNotNull();
    assertThat(retrieved.getId()).isEqualTo(created.getId()); // Dinamik ID
    assertThat(retrieved.getName()).isEqualTo(created.getName());
}
```

**❌ Kötü: Minimal assertions**
```java
@Test
void createHospital_ShouldPersistCorrectly() {
    HospitalDTO created = hospitalService.createHospital(hospital);
    
    // Yetersiz kontroller
    assertThat(created).isNotNull();
    // ID kontrolü yok, retrieve kontrolü yok
}
```

---

## 🔍 Örnek Test Senaryoları

### Senaryo 1: Create ve Retrieve

```java
@Test
@DisplayName("Create and retrieve hospital - Should persist with dynamic ID")
void createAndRetrieveHospital_ShouldPersistWithDynamicId() {
    // Given
    Hospital hospital = createTestHospital();
    
    // When
    HospitalDTO created = hospitalService.createHospital(hospital);
    
    // Then - Dinamik ID kontrolü
    assertThat(created).isNotNull();
    assertThat(created.getId()).isNotNull();
    assertThat(created.getId()).isPositive();
    
    // Retrieve and verify
    Long createdId = created.getId(); // ID'yi sakla
    HospitalDTO retrieved = hospitalService.getHospitalById(createdId);
    assertThat(retrieved.getId()).isEqualTo(createdId); // Dinamik ID ile karşılaştır
}
```

### Senaryo 2: List Filtering

```java
@Test
@DisplayName("Get hospitals by city - Should return filtered hospitals")
void getHospitalsByCity_ShouldReturnFilteredHospitals() {
    // Given
    Hospital hospital1 = createTestHospital("Hospital 1", "İstanbul");
    Hospital hospital2 = createTestHospital("Hospital 2", "İstanbul");
    Hospital hospital3 = createTestHospital("Hospital 3", "Ankara");
    
    HospitalDTO created1 = hospitalService.createHospital(hospital1);
    HospitalDTO created2 = hospitalService.createHospital(hospital2);
    HospitalDTO created3 = hospitalService.createHospital(hospital3);
    
    // When
    List<HospitalDTO> istanbulHospitals = hospitalService.getHospitalsByCity("İstanbul");
    
    // Then - Dinamik ID kontrolleri
    assertThat(istanbulHospitals).hasSize(2);
    assertThat(istanbulHospitals)
        .extracting(HospitalDTO::getId)
        .containsExactlyInAnyOrder(created1.getId(), created2.getId()) // Dinamik ID
        .doesNotContain(created3.getId()); // Ankara'da olduğu için dahil edilmemeli
}
```

---

## 📊 Test Execution Patterns

### Pattern 1: Single Entity Test

```java
@Test
void createEntity_ShouldReturnEntityWithDynamicId() {
    // Create
    EntityDTO created = service.create(entity);
    
    // Verify ID
    assertThat(created.getId()).isNotNull();
    assertThat(created.getId()).isPositive();
    
    // Retrieve and compare
    EntityDTO retrieved = service.getById(created.getId());
    assertThat(retrieved.getId()).isEqualTo(created.getId());
}
```

### Pattern 2: Multiple Entities Test

```java
@Test
void createMultipleEntities_ShouldReturnWithDynamicIds() {
    // Create multiple
    EntityDTO created1 = service.create(entity1);
    EntityDTO created2 = service.create(entity2);
    
    // Verify IDs are different
    assertThat(created1.getId()).isNotEqualTo(created2.getId());
    
    // Verify both are valid
    assertThat(created1.getId()).isNotNull().isPositive();
    assertThat(created2.getId()).isNotNull().isPositive();
}
```

### Pattern 3: Filtered List Test

```java
@Test
void getFilteredList_ShouldReturnCorrectEntities() {
    // Create entities
    EntityDTO created1 = service.create(entity1);
    EntityDTO created2 = service.create(entity2);
    EntityDTO created3 = service.create(entity3);
    
    // Filter
    List<EntityDTO> filtered = service.getFiltered(filter);
    
    // Verify with dynamic IDs
    assertThat(filtered)
        .extracting(EntityDTO::getId)
        .containsExactlyInAnyOrder(created1.getId(), created2.getId())
        .doesNotContain(created3.getId());
}
```

---

## 🎯 Özet

### ✅ Yapılması Gerekenler

1. **Dinamik ID Kontrolü:**
   - `assertThat(result.getId()).isNotNull()`
   - `assertThat(result.getId()).isPositive()`
   - `assertThat(retrieved.getId()).isEqualTo(created.getId())`

2. **@Transactional Kullanımı:**
   - Integration testlerde kullan
   - Her test sonrası rollback yapılır
   - Test isolation sağlanır

3. **Test Data Setup:**
   - Her test için fresh data
   - `@BeforeEach` ile cleanup
   - Shared state kullanma

### ❌ Yapılmaması Gerekenler

1. **Statik ID Kontrolü:**
   - `assertThat(result.getId()).isEqualTo(1L)` ❌
   - Test sırasına bağlı, güvenilir değil

2. **Shared Test Data:**
   - Static fields ile shared state ❌
   - Test isolation'ı bozar

3. **Minimal Assertions:**
   - Sadece null check ❌
   - Yetersiz test coverage

---

## 📚 Referanslar

- **JUnit 5 Documentation:** https://junit.org/junit5/
- **AssertJ Documentation:** https://assertj.github.io/doc/
- **Spring Boot Testing:** https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.testing

---

## ✅ Test Checklist

Integration testlerde:
- [x] Dinamik ID kontrolü kullanılıyor
- [x] @Transactional annotation'ı var
- [x] @BeforeEach ile cleanup yapılıyor
- [x] Statik ID kontrolü yok
- [x] Comprehensive assertions var
- [x] Test isolation sağlanıyor

