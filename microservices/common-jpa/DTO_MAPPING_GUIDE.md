# DTO ve Mapping Kılavuzu

## Genel Bakış

Bu kılavuz, DTO (Data Transfer Object) ve Entity mapping konusunda best practice'leri açıklar. Entity'lerin doğrudan Controller'da kullanılması güvenlik açığıdır ve bu kılavuz bu sorunu çözer.

## 1. DTO Pattern

### Neden DTO Kullanmalıyız?

1. **Security**: Entity'lerin iç yapısını dış dünyaya açmaz
2. **API Stability**: Entity değişiklikleri API'yi etkilemez
3. **Performance**: Sadece gerekli alanlar gönderilir
4. **Validation**: Request/Response için farklı validation kuralları
5. **Flexibility**: API ve Entity arasında esnek dönüşüm

### Request/Response Ayrımı

**Request DTO**: Create/Update işlemleri için
- ID, createdAt gibi sistem alanları yok
- Sadece kullanıcı tarafından sağlanan alanlar

**Response DTO**: Read işlemleri için
- ID, timestamps, audit fields dahil
- Computed fields (fullName, age, etc.)
- Güvenlik: Sensitive fields masked veya excluded

## 2. Base DTO Classes

### BaseRequestDTO

Tüm Request DTO'ları `BaseRequestDTO`'dan extend eder:

```java
@Data
public class PatientRequestDTO extends BaseRequestDTO {
    private String firstName;
    private String lastName;
    private String email;
    // ... ID, createdAt YOK
}
```

### BaseResponseDTO

Tüm Response DTO'ları `BaseResponseDTO`'dan extend eder:

```java
@Data
public class PatientResponseDTO extends BaseResponseDTO {
    private UUID id;
    private String firstName;
    private LocalDateTime createdAt;
    // ... Audit fields dahil
}
```

## 3. Mapper Pattern (AutoMapper Benzeri)

### EntityMapper Interface

Generic mapper interface (AutoMapper benzeri):

```java
public interface EntityMapper<E extends BaseEntity, D extends BaseDTO> {
    D toDTO(E entity);
    E toEntity(D dto);
    List<D> toDTOList(List<E> entities);
    List<E> toEntityList(List<D> dtos);
    void updateEntityFromDTO(D dto, E entity);
}
```

### RequestResponseMapper Interface

Request/Response pattern için özel mapper:

```java
public interface RequestResponseMapper<E, Req extends BaseRequestDTO, Res extends BaseResponseDTO> {
    E toEntity(Req requestDTO);
    Res toResponseDTO(E entity);
    List<Res> toResponseDTOList(List<E> entities);
    void updateEntityFromRequest(Req requestDTO, E entity);
}
```

### Mapper Implementation

```java
@Component
public class PatientMapper implements RequestResponseMapper<Patient, PatientRequestDTO, PatientResponseDTO> {
    
    @Override
    public Patient toEntity(PatientRequestDTO requestDTO) {
        Patient patient = new Patient();
        patient.setFirstName(requestDTO.getFirstName());
        patient.setLastName(requestDTO.getLastName());
        // ... map other fields
        return patient;
    }
    
    @Override
    public PatientResponseDTO toResponseDTO(Patient entity) {
        PatientResponseDTO dto = new PatientResponseDTO();
        dto.setId(entity.getId());
        dto.setFirstName(entity.getFirstName());
        dto.setFullName(entity.getFirstName() + " " + entity.getLastName());
        // ... map other fields including computed fields
        return dto;
    }
    
    @Override
    public void updateEntityFromRequest(PatientRequestDTO requestDTO, Patient entity) {
        // Update only non-null fields
        if (requestDTO.getFirstName() != null) {
            entity.setFirstName(requestDTO.getFirstName());
        }
        // ... update other fields
    }
}
```

## 4. Kullanım Örnekleri

### Controller'da Kullanım

```java
@RestController
@RequestMapping("/api/patients")
public class PatientController {
    
    @Autowired
    private PatientServiceInterface patientService;
    
    @Autowired
    private PatientMapper patientMapper;
    
    @PostMapping
    public ResponseEntity<PatientResponseDTO> createPatient(
            @Valid @RequestBody PatientRequestDTO requestDTO) {
        
        // Request DTO -> Entity
        Patient patient = patientMapper.toEntity(requestDTO);
        
        // Business logic
        Patient created = patientService.createPatient(patient);
        
        // Entity -> Response DTO
        PatientResponseDTO response = patientMapper.toResponseDTO(created);
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<PatientResponseDTO> getPatient(@PathVariable UUID id) {
        Patient patient = patientService.findById(id)
            .orElseThrow(() -> ResourceNotFoundException.patientNotFound(id));
        
        // Entity -> Response DTO
        PatientResponseDTO response = patientMapper.toResponseDTO(patient);
        
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<PatientResponseDTO> updatePatient(
            @PathVariable UUID id,
            @Valid @RequestBody PatientRequestDTO requestDTO) {
        
        Patient existing = patientService.findById(id)
            .orElseThrow(() -> ResourceNotFoundException.patientNotFound(id));
        
        // Update entity from request DTO
        patientMapper.updateEntityFromRequest(requestDTO, existing);
        
        Patient updated = patientService.updatePatient(existing);
        
        // Entity -> Response DTO
        PatientResponseDTO response = patientMapper.toResponseDTO(updated);
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    public ResponseEntity<List<PatientResponseDTO>> getAllPatients() {
        List<Patient> patients = patientService.findAll();
        
        // List mapping
        List<PatientResponseDTO> response = patientMapper.toResponseDTOList(patients);
        
        return ResponseEntity.ok(response);
    }
}
```

### Service'de Kullanım

Service'ler Entity ile çalışır, DTO ile değil:

```java
@Service
public class PatientServiceImpl implements PatientServiceInterface {
    
    @Override
    public Patient createPatient(Patient patient) {
        // Business logic with Entity
        return patientRepository.save(patient);
    }
}
```

## 5. Request/Response DTO Örnekleri

### CreateAppointmentRequestDTO

```java
@Data
public class CreateAppointmentRequestDTO extends BaseRequestDTO {
    @NotNull
    private UUID patientId;
    
    @NotNull
    private UUID doctorId;
    
    @NotNull
    @Future
    private LocalDateTime appointmentDate;
    
    // Optional fields
    private UUID accommodationId;
    private String notes;
}
```

### AppointmentResponseDTO

```java
@Data
public class AppointmentResponseDTO extends BaseResponseDTO {
    private UUID id;
    private String appointmentNumber;
    private UUID patientId;
    private String patientName; // Computed
    private LocalDateTime appointmentDate;
    private String status;
    // ... Audit fields
}
```

## 6. Best Practices

### 1. Entity'ler Asla Controller'a Açılmamalı

❌ **YANLIŞ**:
```java
@PostMapping
public ResponseEntity<Patient> createPatient(@RequestBody Patient patient) {
    return ResponseEntity.ok(service.save(patient));
}
```

✅ **DOĞRU**:
```java
@PostMapping
public ResponseEntity<PatientResponseDTO> createPatient(@RequestBody PatientRequestDTO requestDTO) {
    Patient patient = mapper.toEntity(requestDTO);
    Patient created = service.createPatient(patient);
    return ResponseEntity.ok(mapper.toResponseDTO(created));
}
```

### 2. Request ve Response Ayrılmalı

- **Request DTO**: Create/Update için
- **Response DTO**: Read için
- Aynı DTO hem request hem response için kullanılmamalı

### 3. Sensitive Data Masking

```java
@Override
public PatientResponseDTO toResponseDTO(Patient entity) {
    PatientResponseDTO dto = new PatientResponseDTO();
    // ... map other fields
    
    // Mask sensitive data
    dto.setNationalId(maskSensitiveData(entity.getNationalId()));
    dto.setPassportNumber(maskSensitiveData(entity.getPassportNumber()));
    
    return dto;
}

private String maskSensitiveData(String data) {
    if (data == null || data.length() <= 4) {
        return "****";
    }
    return "****" + data.substring(data.length() - 4);
}
```

### 4. Computed Fields

Response DTO'larda computed fields eklenebilir:

```java
@Override
public PatientResponseDTO toResponseDTO(Patient entity) {
    PatientResponseDTO dto = new PatientResponseDTO();
    dto.setFirstName(entity.getFirstName());
    dto.setLastName(entity.getLastName());
    
    // Computed field
    dto.setFullName(entity.getFirstName() + " " + entity.getLastName());
    
    // Age calculation
    if (entity.getDateOfBirth() != null) {
        dto.setAge(Period.between(entity.getDateOfBirth(), LocalDate.now()).getYears());
    }
    
    return dto;
}
```

### 5. Null Safety

Mapper'lar null-safe olmalı:

```java
@Override
public PatientResponseDTO toResponseDTO(Patient entity) {
    if (entity == null) {
        return null;
    }
    // ... mapping logic
}
```

### 6. Partial Updates

Update işlemlerinde sadece non-null fields update edilmeli:

```java
@Override
public void updateEntityFromRequest(PatientRequestDTO requestDTO, Patient entity) {
    if (requestDTO.getFirstName() != null) {
        entity.setFirstName(requestDTO.getFirstName());
    }
    // ... only update non-null fields
}
```

## 7. Mapping Utilities

MapperUtils class'ı common mapping operations için:

```java
// Map list
List<PatientResponseDTO> dtos = MapperUtils.mapList(
    patients, 
    mapper::toResponseDTO
);

// Map with null filtering
List<PatientResponseDTO> dtos = MapperUtils.mapListFilterNull(
    patients, 
    mapper::toResponseDTO
);

// Safe mapping
PatientResponseDTO dto = MapperUtils.mapOrNull(
    patient, 
    mapper::toResponseDTO
);
```

## 8. Yeni DTO ve Mapper Oluşturma

### Adım 1: Request DTO

```java
@Data
public class DoctorRequestDTO extends BaseRequestDTO {
    @NotBlank
    private String firstName;
    // ...
}
```

### Adım 2: Response DTO

```java
@Data
public class DoctorResponseDTO extends BaseResponseDTO {
    private UUID id;
    private String firstName;
    private String fullName; // Computed
    // ...
}
```

### Adım 3: Mapper

```java
@Component
public class DoctorMapper implements RequestResponseMapper<Doctor, DoctorRequestDTO, DoctorResponseDTO> {
    // Implement methods
}
```

### Adım 4: Controller'da Kullanım

```java
@Autowired
private DoctorMapper doctorMapper;

@PostMapping
public ResponseEntity<DoctorResponseDTO> createDoctor(@RequestBody DoctorRequestDTO requestDTO) {
    Doctor doctor = doctorMapper.toEntity(requestDTO);
    Doctor created = doctorService.create(doctor);
    return ResponseEntity.ok(doctorMapper.toResponseDTO(created));
}
```

## 9. Migration Guide

### Mevcut Controller'ları Güncelleme

1. **Request DTO Oluştur**: Create/Update için
2. **Response DTO Oluştur**: Read için
3. **Mapper Oluştur**: Entity <-> DTO mapping
4. **Controller Güncelle**: DTO kullan
5. **Service Değiştirme**: Service Entity ile çalışmaya devam eder

Örnek:

```java
// ÖNCE
@PostMapping
public ResponseEntity<Patient> createPatient(@RequestBody Patient patient) {
    return ResponseEntity.ok(service.save(patient));
}

// SONRA
@PostMapping
public ResponseEntity<PatientResponseDTO> createPatient(@RequestBody PatientRequestDTO requestDTO) {
    Patient patient = patientMapper.toEntity(requestDTO);
    Patient created = patientService.createPatient(patient);
    return ResponseEntity.ok(patientMapper.toResponseDTO(created));
}
```

## 10. Testing

### Mapper Testing

```java
class PatientMapperTest {
    
    private PatientMapper mapper = new PatientMapper();
    
    @Test
    void toEntity_ShouldMapCorrectly() {
        PatientRequestDTO dto = new PatientRequestDTO();
        dto.setFirstName("John");
        dto.setLastName("Doe");
        
        Patient entity = mapper.toEntity(dto);
        
        assertEquals("John", entity.getFirstName());
        assertEquals("Doe", entity.getLastName());
    }
    
    @Test
    void toResponseDTO_ShouldIncludeComputedFields() {
        Patient entity = new Patient();
        entity.setId(UUID.randomUUID());
        entity.setFirstName("John");
        entity.setLastName("Doe");
        
        PatientResponseDTO dto = mapper.toResponseDTO(entity);
        
        assertEquals("John Doe", dto.getFullName());
    }
}
```

