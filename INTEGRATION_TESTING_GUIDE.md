# Entegrasyon Testleri Kılavuzu

## 1. Test Yapısı ✅

### Test Tipleri

1. **Unit Tests (Controller/Service Tests):**
   - Mockito ile dependency'leri mock'lar
   - Hızlı çalışır
   - İzole testler

2. **Integration Tests:**
   - Gerçek database kullanır (H2 in-memory)
   - Spring Boot Test Context yükler
   - Daha yavaş ama daha gerçekçi

### Test Klasör Yapısı

```
src/test/java/com/example/HealthTourism/
├── controller/
│   └── HospitalControllerTest.java
├── service/
│   └── HospitalServiceTest.java
└── integration/
    └── HospitalIntegrationTest.java
```

---

## 2. Dependency'ler ✅

### pom.xml

```xml
<!-- Test Dependencies -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-core</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-junit-jupiter</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.assertj</groupId>
    <artifactId>assertj-core</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-test</artifactId>
    <scope>test</scope>
</dependency>
```

---

## 3. Controller Tests ✅

### Örnek: HospitalControllerTest

**Özellikler:**
- `@WebMvcTest` - Sadece web layer'ı yükler
- `MockMvc` - HTTP request/response testleri
- `@MockBean` - Service'leri mock'lar
- JSON assertion'ları

**Test Senaryoları:**
- GET endpoint'leri (200 OK, doğru JSON response)
- POST endpoint'leri (201 CREATED)
- Validation hataları (400 BAD_REQUEST)
- NotFound hataları (404 NOT_FOUND)

**Kullanım:**
```java
@WebMvcTest(HospitalController.class)
class HospitalControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private HospitalService hospitalService;
    
    @Test
    void getAllHospitals_ShouldReturnListOfHospitals() throws Exception {
        // Given
        when(hospitalService.getAllActiveHospitals()).thenReturn(hospitalList);
        
        // When & Then
        mockMvc.perform(get("/api/v1/hospitals"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }
}
```

---

## 4. Service Tests ✅

### Örnek: HospitalServiceTest

**Özellikler:**
- `@ExtendWith(MockitoExtension.class)` - Mockito extension
- `@Mock` - Repository'leri mock'lar
- `@InjectMocks` - Service'i inject eder
- Business logic testleri

**Test Senaryoları:**
- Başarılı senaryolar (happy path)
- Exception senaryoları (NotFoundException, IllegalArgumentException)
- Validation testleri
- Business logic testleri

**Kullanım:**
```java
@ExtendWith(MockitoExtension.class)
class HospitalServiceTest {
    
    @Mock
    private HospitalRepository hospitalRepository;
    
    @InjectMocks
    private HospitalService hospitalService;
    
    @Test
    void getHospitalById_ShouldReturnHospital_WhenExists() {
        // Given
        when(hospitalRepository.findById(1L)).thenReturn(Optional.of(hospital));
        
        // When
        HospitalDTO result = hospitalService.getHospitalById(1L);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }
}
```

---

## 5. Integration Tests ✅

### Örnek: HospitalIntegrationTest

**Özellikler:**
- `@SpringBootTest` - Full Spring context yükler
- `@ActiveProfiles("test")` - Test profile'ı kullanır
- `@Transactional` - Her test sonrası rollback
- H2 in-memory database

**Test Senaryoları:**
- Database operations (CRUD)
- Repository queries
- Service-Database entegrasyonu
- Transaction management

**Kullanım:**
```java
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class HospitalIntegrationTest {
    
    @Autowired
    private HospitalService hospitalService;
    
    @Autowired
    private HospitalRepository hospitalRepository;
    
    @Test
    void createAndRetrieveHospital_ShouldPersistAndRetrieveCorrectly() {
        // Given
        Hospital hospital = createTestHospital();
        
        // When
        HospitalDTO created = hospitalService.createHospital(hospital);
        
        // Then
        assertThat(created).isNotNull();
        HospitalDTO retrieved = hospitalService.getHospitalById(created.getId());
        assertThat(retrieved).isNotNull();
    }
}
```

---

## 6. Test Configuration ✅

### application-test.properties

**Test-specific configuration:**
```properties
# H2 In-Memory Database
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driver-class-name=org.h2.Driver

# JPA Configuration
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true

# Disable cache for testing
spring.cache.type=none

# Disable JWT for testing
jwt.enabled=false

# Disable rate limiting for testing
rate.limit.enabled=false
```

---

## 7. Test Best Practices ✅

### 1. Test Naming

**İyi:**
```java
@Test
@DisplayName("getHospitalById - Should return hospital when exists")
void getHospitalById_ShouldReturnHospital_WhenExists() {
    // Test code
}
```

**Kötü:**
```java
@Test
void test1() {
    // Test code
}
```

### 2. AAA Pattern (Arrange-Act-Assert)

```java
@Test
void getHospitalById_ShouldReturnHospital_WhenExists() {
    // Arrange (Given)
    when(hospitalRepository.findById(1L)).thenReturn(Optional.of(hospital));
    
    // Act (When)
    HospitalDTO result = hospitalService.getHospitalById(1L);
    
    // Assert (Then)
    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(1L);
}
```

### 3. Test Isolation

- Her test bağımsız çalışmalı
- `@BeforeEach` ile setup
- `@AfterEach` ile cleanup (integration testlerde `@Transactional` kullan)

### ⚠️ Önemli: ID Generation ve @Transactional

**Problem:**
- `@Transactional` kullanıldığında her test sonrası rollback yapılır
- Ancak ID generation (Sequence/Identity) rollback'ten etkilenmez
- ID sequence'ler artmaya devam eder

**Örnek:**
```java
// Test 1: ID = 1
// Test 2: ID = 2 (rollback olsa bile)
// Test 3: ID = 3 (rollback olsa bile)
```

**Çözüm: Dinamik ID Kontrolü**

**❌ Kötü (Statik ID Kontrolü):**
```java
@Test
void createHospital_ShouldReturnHospital() {
    HospitalDTO created = hospitalService.createHospital(hospital);
    
    // Statik ID kontrolü - HATALI!
    assertThat(created.getId()).isEqualTo(1L); // Test sırasına bağlı, güvenilir değil
}
```

**✅ İyi (Dinamik ID Kontrolü):**
```java
@Test
void createHospital_ShouldReturnHospital() {
    HospitalDTO created = hospitalService.createHospital(hospital);
    
    // Dinamik ID kontrolü - DOĞRU!
    assertThat(created.getId()).isNotNull(); // ID null olmamalı
    assertThat(created.getId()).isPositive(); // ID pozitif olmalı
    
    // Retrieve ve karşılaştır
    HospitalDTO retrieved = hospitalService.getHospitalById(created.getId());
    assertThat(retrieved.getId()).isEqualTo(created.getId()); // Dinamik ID ile karşılaştır
}
```

**Best Practice:**
- ✅ `assertThat(result.getId()).isNotNull()` - ID'nin null olmadığını kontrol et
- ✅ `assertThat(result.getId()).isPositive()` - ID'nin pozitif olduğunu kontrol et
- ✅ `assertThat(retrieved.getId()).isEqualTo(created.getId())` - Dinamik ID ile karşılaştır
- ❌ `assertThat(result.getId()).isEqualTo(1L)` - Statik ID kontrolü kullanma

### 4. Mock Usage

**İyi:**
```java
@Mock
private HospitalRepository hospitalRepository;

@InjectMocks
private HospitalService hospitalService;
```

**Kötü:**
```java
HospitalRepository repository = Mockito.mock(HospitalRepository.class);
```

### 5. Assertions

**AssertJ kullan (JUnit assertions yerine):**
```java
// İyi
assertThat(result).isNotNull();
assertThat(result).hasSize(2);
assertThat(result).contains(hospitalDTO1);

// Kötü
assertNotNull(result);
assertEquals(2, result.size());
```

---

## 8. Test Coverage ✅

### Hedef Test Coverage

- **Controller Tests:** %80+
- **Service Tests:** %85+
- **Integration Tests:** Kritik business logic için

### Coverage Raporu

```bash
# Maven ile coverage raporu
mvn clean test jacoco:report

# Coverage raporu: target/site/jacoco/index.html
```

---

## 9. Test Execution ✅

### Tüm Testleri Çalıştır

```bash
mvn clean test
```

### Belirli Test Class'ını Çalıştır

```bash
mvn test -Dtest=HospitalControllerTest
```

### Belirli Test Metodunu Çalıştır

```bash
mvn test -Dtest=HospitalControllerTest#getAllHospitals_ShouldReturnListOfHospitals
```

### Integration Testleri Çalıştır

```bash
mvn test -Dtest=*IntegrationTest
```

---

## 10. Örnek Test Senaryoları ✅

### Controller Test Senaryoları

1. **GET /api/v1/hospitals**
   - ✅ 200 OK
   - ✅ Doğru JSON response
   - ✅ Service metodunun çağrıldığı

2. **GET /api/v1/hospitals/{id}**
   - ✅ 200 OK (hospital exists)
   - ✅ 404 NOT_FOUND (hospital not exists)
   - ✅ Doğru hospital dönüyor

3. **POST /api/v1/hospitals**
   - ✅ 201 CREATED
   - ✅ Location header
   - ✅ Validation hataları (400 BAD_REQUEST)

### Service Test Senaryoları

1. **getHospitalById()**
   - ✅ Hospital bulunduğunda DTO dönüyor
   - ✅ Hospital bulunamadığında HospitalNotFoundException fırlatıyor

2. **getHospitalsByCity()**
   - ✅ Doğru şehirdeki hastaneleri dönüyor
   - ✅ Boş liste dönüyor (hastane yoksa)
   - ✅ IllegalArgumentException (city null/empty)

3. **createHospital()**
   - ✅ Hospital oluşturuluyor
   - ✅ Default değerler set ediliyor
   - ✅ Repository.save() çağrılıyor

### Integration Test Senaryoları

1. **Database Operations**
   - ✅ Hospital create ve retrieve
   - ✅ Hospital update
   - ✅ Hospital delete (soft delete)

2. **Queries**
   - ✅ City'ye göre filtreleme
   - ✅ District'e göre filtreleme
   - ✅ Airport distance'a göre filtreleme

---

## 11. Sorun Giderme ✅

### Sorun: Test Database Connection Failed

**Hata:**
```
Unable to connect to database
```

**Çözüm:**
1. `application-test.properties` dosyasının doğru olduğundan emin olun
2. H2 dependency'sinin eklendiğinden emin olun
3. Test profile'ının aktif olduğundan emin olun: `@ActiveProfiles("test")`

### Sorun: Mock Not Working

**Hata:**
```
NullPointerException in test
```

**Çözüm:**
1. `@ExtendWith(MockitoExtension.class)` annotation'ının olduğundan emin olun
2. `@Mock` ve `@InjectMocks` annotation'larını kontrol edin
3. `when().thenReturn()` chain'inin doğru olduğundan emin olun

### Sorun: Integration Test Slow

**Çözüm:**
1. `@Transactional` kullanarak her test sonrası rollback yapın
2. Test database'ini H2 in-memory kullanın
3. Sadece gerekli Spring context'leri yükleyin (`@DataJpaTest`, `@WebMvcTest`)

---

## 12. Sonraki Adımlar

### Ek Test Senaryoları

- [ ] ReservationServiceTest
- [ ] DoctorServiceTest
- [ ] AuthServiceTest
- [ ] ReviewServiceTest
- [ ] TravelPackageServiceTest

### Advanced Testing

- [ ] TestContainers ile PostgreSQL testleri
- [ ] Performance tests
- [ ] Security tests (Spring Security Test)
- [ ] Contract tests (Pact)
- [ ] Load tests (JMeter, Gatling)

---

## 13. Özet

✅ **Test yapısı oluşturuldu!**

**Özellikler:**
- ✅ Controller tests (MockMvc)
- ✅ Service tests (Mockito)
- ✅ Integration tests (H2 database)
- ✅ Test configuration (application-test.properties)
- ✅ Best practices ve örnekler

**Kullanım:**
```bash
# Tüm testleri çalıştır
mvn clean test

# Coverage raporu
mvn clean test jacoco:report
```

**Test Coverage:**
- HospitalControllerTest: ✅
- HospitalServiceTest: ✅
- HospitalIntegrationTest: ✅

