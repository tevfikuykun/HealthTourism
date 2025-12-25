# Business Logic Katmanı Kılavuzu

## Genel Bakış

Bu kılavuz, Business Logic katmanında Service sınıfları, Validation ve Exception yönetimi için best practice'leri açıklar.

## 1. Dependency Injection: Service Interface Pattern

### Neden Interface Kullanmalıyız?

1. **Loose Coupling**: Servisler arası bağımlılık interface üzerinden yönetilir
2. **Testability**: Mock'lama kolaylaşır
3. **Clear Contracts**: Service contract'ları açıkça tanımlanır
4. **Flexibility**: Implementation değişiklikleri kolaylaşır

### BaseService Interface

Tüm service interface'leri `BaseService` interface'ini extend etmelidir:

```java
public interface PatientServiceInterface extends BaseService<Patient> {
    Optional<Patient> findByEmail(String email);
    Patient createPatient(Patient patient);
}
```

### Service Implementation

Implementation sınıfları `*Impl` suffix'i ile isimlendirilir:

```java
@Service
@Transactional
public class PatientServiceImpl implements PatientServiceInterface {
    
    @Autowired
    private PatientRepository patientRepository;
    
    @Autowired
    private PhoneValidator phoneValidator;
    
    @Override
    public Patient createPatient(Patient patient) {
        // Business logic
        phoneValidator.validate(patient.getPhone());
        return patientRepository.save(patient);
    }
}
```

### Kullanım

Controller'larda interface inject edilir:

```java
@RestController
public class PatientController {
    
    @Autowired
    private PatientServiceInterface patientService; // Interface injection
    
    @PostMapping("/patients")
    public ResponseEntity<Patient> createPatient(@RequestBody Patient patient) {
        Patient created = patientService.createPatient(patient);
        return ResponseEntity.ok(created);
    }
}
```

## 2. Validation: FluentValidation Benzeri Yapı

### BusinessRuleValidator Interface

Business rule validation'ları için `BusinessRuleValidator` interface'i kullanılır:

```java
public interface BusinessRuleValidator<T> {
    void validate(T object) throws ValidationException;
    boolean isValid(T object);
}
```

### Validator Sınıfları

Her validation kuralı için ayrı validator sınıfı oluşturulur:

```java
@Component
public class PhoneValidator implements BusinessRuleValidator<String> {
    
    @Override
    public void validate(String phone) throws ValidationException {
        // Validation logic
        if (phone == null || !PHONE_PATTERN.matcher(phone).matches()) {
            throw new ValidationException(
                ErrorCode.PHONE_FORMAT_INVALID,
                "Telefon numarası formatı geçersiz"
            );
        }
    }
}
```

### Mevcut Validator'lar

- **PhoneValidator**: Telefon numarası formatı validation'ı
- **AgeValidator**: Yaş limitleri validation'ı
- **AppointmentValidator**: Randevu business rule validation'ı

### Service'de Kullanım

```java
@Service
public class PatientServiceImpl implements PatientServiceInterface {
    
    @Autowired
    private PhoneValidator phoneValidator;
    
    @Autowired
    private AgeValidator ageValidator;
    
    @Override
    public Patient createPatient(Patient patient) {
        // Validation
        phoneValidator.validate(patient.getPhone());
        ageValidator.validate(patient.getDateOfBirth());
        
        // Business logic
        return patientRepository.save(patient);
    }
}
```

### Yeni Validator Oluşturma

```java
@Component
public class EmailValidator implements BusinessRuleValidator<String> {
    
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@(.+)$"
    );
    
    @Override
    public void validate(String email) throws ValidationException {
        if (email == null || !EMAIL_PATTERN.matcher(email).matches()) {
            throw new ValidationException(
                ErrorCode.EMAIL_FORMAT_INVALID,
                "E-posta formatı geçersiz"
            );
        }
    }
}
```

## 3. Custom Exceptions: BusinessException Hierarchy

### Exception Hierarchy

```
BusinessException (base)
├── ResourceNotFoundException (404)
└── ValidationException (400)
```

### ErrorCode Enum

Tüm hata kodları `ErrorCode` enum'unda tanımlanır:

```java
public enum ErrorCode {
    PATIENT_NOT_FOUND("PATIENT_1001", "Hasta bulunamadı"),
    APPOINTMENT_CONFLICT("APPOINTMENT_2002", "Bu saatte başka bir randevu var"),
    PHONE_FORMAT_INVALID("VALIDATION_5005", "Telefon numarası formatı geçersiz"),
    // ...
}
```

### Exception Kullanımı

#### ResourceNotFoundException

```java
// Service'de kullanım
Patient patient = patientRepository.findById(id)
    .orElseThrow(() -> ResourceNotFoundException.patientNotFound(id));
```

#### ValidationException

```java
// Validator'da kullanım
if (phone == null) {
    throw new ValidationException(
        ErrorCode.PHONE_FORMAT_INVALID,
        "Telefon numarası boş olamaz"
    );
}
```

#### BusinessException

```java
// Business rule violation
if (hasExistingAppointmentOnDate) {
    throw new BusinessException(
        ErrorCode.APPOINTMENT_DOUBLE_BOOKING,
        "Bir hasta aynı güne iki randevu alamaz"
    );
}
```

### ErrorResponse Format

Exception'lar otomatik olarak `ErrorResponse` formatına dönüştürülür:

```json
{
  "status": 400,
  "errorCode": "VALIDATION_5005",
  "message": "Telefon numarası formatı geçersiz",
  "timestamp": "2023-10-15T10:30:00",
  "validationErrors": {
    "phone": "Telefon numarası formatı geçersiz"
  }
}
```

## 4. Best Practices

### Service Layer

1. **Interface Kullanımı**: Tüm service'ler interface ile tanımlanmalı
2. **Transaction Management**: `@Transactional` annotation kullanılmalı
3. **Validation**: Business rule validation'ları validator sınıflarında olmalı
4. **Exception Handling**: BusinessException kullanılmalı, generic Exception kullanılmamalı

### Validation

1. **Separation of Concerns**: Validation logic service'ten ayrı olmalı
2. **Reusability**: Validator'lar reusable olmalı
3. **Composability**: Birden fazla validator birleştirilebilmeli
4. **Error Messages**: Anlaşılır ve kullanıcı dostu hata mesajları

### Exception Handling

1. **Error Codes**: Her hata için unique error code
2. **HTTP Status Mapping**: Doğru HTTP status code'ları
3. **Structured Errors**: Validation error'ları field-level olmalı
4. **Logging**: Tüm exception'lar loglanmalı

## 5. Örnekler

### Tam Örnek: Patient Service

```java
// Interface
public interface PatientServiceInterface extends BaseService<Patient> {
    Patient createPatient(Patient patient);
    Patient updatePatient(UUID id, Patient patient);
}

// Implementation
@Service
@Transactional
public class PatientServiceImpl implements PatientServiceInterface {
    
    @Autowired
    private PatientRepository patientRepository;
    
    @Autowired
    private PhoneValidator phoneValidator;
    
    @Autowired
    private AgeValidator ageValidator;
    
    @Override
    public Patient createPatient(Patient patient) {
        // Validation
        phoneValidator.validate(patient.getPhone());
        ageValidator.validate(patient.getDateOfBirth());
        
        // Business logic
        if (patientRepository.findByEmail(patient.getEmail()).isPresent()) {
            throw new ValidationException(
                ErrorCode.PATIENT_ALREADY_EXISTS,
                "Bu e-posta adresi ile kayıtlı hasta zaten mevcut"
            );
        }
        
        return patientRepository.save(patient);
    }
    
    @Override
    public Patient updatePatient(UUID id, Patient patient) {
        Patient existing = patientRepository.findById(id)
            .orElseThrow(() -> ResourceNotFoundException.patientNotFound(id));
        
        // Validation
        if (patient.getPhone() != null) {
            phoneValidator.validate(patient.getPhone());
            existing.setPhone(patient.getPhone());
        }
        
        return patientRepository.save(existing);
    }
}
```

### Appointment Service Örneği (Business Rule)

```java
@Service
@Transactional
public class AppointmentServiceImpl implements AppointmentServiceInterface {
    
    @Autowired
    private AppointmentRepository appointmentRepository;
    
    @Autowired
    private AppointmentValidator appointmentValidator;
    
    @Override
    public Appointment createAppointment(AppointmentRequest request) {
        // Business rule: Appointment date cannot be in the past
        appointmentValidator.validateAppointmentDateNotInPast(request.getAppointmentDate());
        
        // Business rule: Patient cannot have two appointments on the same day
        LocalDate appointmentDate = request.getAppointmentDate().toLocalDate();
        boolean hasExistingAppointment = appointmentRepository
            .existsByPatientIdAndDate(request.getPatientId(), appointmentDate);
        appointmentValidator.validateNoDoubleBookingOnSameDay(hasExistingAppointment);
        
        // Business logic
        Appointment appointment = new Appointment();
        // ... set fields
        return appointmentRepository.save(appointment);
    }
}
```

## 6. Migration Guide

### Mevcut Service'leri Güncelleme

1. **Interface Oluştur**: Service için interface oluştur
2. **Implementation Ayır**: Implementation'ı `*Impl` class'ına taşı
3. **Validators Ekle**: Business rule validation'larını validator'lara taşı
4. **Exception'ları Güncelle**: Generic Exception'ları BusinessException'a çevir
5. **Controller'ları Güncelle**: Controller'larda interface inject et

## 7. Testing

### Service Testing

```java
@ExtendWith(MockitoExtension.class)
class PatientServiceImplTest {
    
    @Mock
    private PatientRepository patientRepository;
    
    @Mock
    private PhoneValidator phoneValidator;
    
    @InjectMocks
    private PatientServiceImpl patientService;
    
    @Test
    void createPatient_ShouldThrowException_WhenPhoneInvalid() {
        // Given
        Patient patient = new Patient();
        patient.setPhone("invalid");
        when(phoneValidator.validate(anyString()))
            .thenThrow(new ValidationException(ErrorCode.PHONE_FORMAT_INVALID));
        
        // When/Then
        assertThrows(ValidationException.class, () -> 
            patientService.createPatient(patient));
    }
}
```

### Validator Testing

```java
class PhoneValidatorTest {
    
    private PhoneValidator validator = new PhoneValidator();
    
    @Test
    void validate_ShouldThrowException_WhenPhoneNull() {
        assertThrows(ValidationException.class, () -> 
            validator.validate(null));
    }
    
    @Test
    void validate_ShouldNotThrow_WhenPhoneValid() {
        assertDoesNotThrow(() -> validator.validate("+90 555 123 4567"));
    }
}
```

