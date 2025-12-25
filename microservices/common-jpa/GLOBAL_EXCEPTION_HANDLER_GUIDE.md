# Global Exception Handler - Profesyonel Kullanım Kılavuzu

## 🎯 Amaç

GlobalExceptionHandler, tüm REST controller'lardan fırlatılan exception'ları yakalayarak:
1. **Güvenli** hata mesajları döner (stack trace yok)
2. **Tutarlı** hata formatı sağlar
3. **Kullanıcı dostu** mesajlar gösterir
4. **Teknik detayları** sadece server-side loglar

## 📋 Kullanım

### Controller'da Try-Catch Yok!

**❌ YANLIŞ:**
```java
@PostMapping
public ResponseEntity<?> createReservation(@RequestBody ReservationRequest request) {
    try {
        Reservation reservation = reservationService.create(request);
        return ResponseEntity.ok(reservation);
    } catch (Exception e) {
        return ResponseEntity.status(500).body("Hata: " + e.getMessage()); // ❌ Güvensiz
    }
}
```

**✅ DOĞRU:**
```java
@PostMapping
public ResponseEntity<ApiResponse<ReservationDTO>> createReservation(
        @Valid @RequestBody ReservationRequestDTO request) {
    // ✅ Try-catch yok - GlobalExceptionHandler yakalar
    ReservationDTO reservation = reservationService.createReservation(request, userId);
    return ResponseEntity.ok(ApiResponse.success("Rezervasyon oluşturuldu", reservation));
}
```

## 🔒 Güvenlik Best Practices

### ❌ ASLA YAPMAYIN:

```java
// ❌ Stack trace'i client'a gönderme
catch (Exception e) {
    return ResponseEntity.status(500).body(e.toString()); // Güvensiz!
}

// ❌ Database hatalarını gösterme
catch (SQLException e) {
    return ResponseEntity.status(500).body("SQL Error: " + e.getMessage()); // Güvensiz!
}

// ❌ Table/column isimlerini gösterme
catch (Exception e) {
    return ResponseEntity.status(500).body("Table 'users' not found"); // Güvensiz!
}
```

### ✅ DOĞRU YAKLAŞIM:

```java
// ✅ GlobalExceptionHandler otomatik yakalar
// ✅ Teknik detaylar sadece log'da
// ✅ Kullanıcıya genel mesaj döner
@ExceptionHandler(Exception.class)
public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex) {
    log.error("Beklenmedik hata: ", ex); // Server-side log
    return ResponseEntity.status(500)
        .body(ErrorResponse.internalServerError()); // Kullanıcı dostu mesaj
}
```

## 📊 Hata Tipleri ve HTTP Status Kodları

| Exception Type | HTTP Status | Error Code | Örnek |
|----------------|-------------|------------|-------|
| `BusinessException` | 400 | Business-specific | `RESERVATION_CONFLICT` |
| `ResourceNotFoundException` | 404 | `RESOURCE_NOT_FOUND` | "Doktor bulunamadı" |
| `MethodArgumentNotValidException` | 400 | `VALIDATION_FAILED` | Field validation errors |
| `ValidationException` | 400 | `VALIDATION_FAILED` | Custom validation errors |
| `IllegalArgumentException` | 400 | `INVALID_ARGUMENT` | "Geçersiz parametre" |
| `Exception` (catch-all) | 500 | `INTERNAL_SERVER_ERROR` | Generic error message |

## 💡 Örnek Kullanım Senaryoları

### Senaryo 1: Business Exception

**Service:**
```java
public ReservationDTO createReservation(ReservationRequestDTO request) {
    Doctor doctor = doctorRepository.findById(request.getDoctorId())
        .orElseThrow(() -> new BusinessException(
            ErrorCode.DOCTOR_NOT_FOUND, 
            "Doktor bulunamadı: " + request.getDoctorId()
        ));
    // ...
}
```

**Response (400 Bad Request):**
```json
{
  "errorCode": "DOCTOR_NOT_FOUND",
  "message": "Doktor bulunamadı: 123",
  "timestamp": "2024-03-25T10:30:00"
}
```

### Senaryo 2: Validation Exception

**Controller:**
```java
@PostMapping
public ResponseEntity<ApiResponse<ReservationDTO>> createReservation(
        @Valid @RequestBody ReservationRequestDTO request) {
    // @Valid annotation automatically validates
}
```

**DTO:**
```java
public class ReservationRequestDTO {
    @NotNull(message = "Randevu tarihi boş olamaz")
    @Future(message = "Randevu tarihi gelecekte olmalıdır")
    private LocalDateTime appointmentDate;
    
    @Email(message = "Email formatı geçersiz")
    private String email;
}
```

**Response (400 Bad Request):**
```json
{
  "errorCode": "VALIDATION_FAILED",
  "message": "Doğrulama hatası: appointmentDate: Randevu tarihi boş olamaz, email: Email formatı geçersiz",
  "fieldErrors": {
    "appointmentDate": "Randevu tarihi boş olamaz",
    "email": "Email formatı geçersiz"
  },
  "timestamp": "2024-03-25T10:30:00"
}
```

### Senaryo 3: Unexpected Exception

**Service:**
```java
public ReservationDTO getReservationById(UUID id) {
    // Unexpected database error occurs
    return reservationRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Database connection failed")); // ❌ Should be custom exception
}
```

**Response (500 Internal Server Error):**
```json
{
  "errorCode": "INTERNAL_SERVER_ERROR",
  "message": "Şu an işleminizi gerçekleştiremiyoruz, lütfen daha sonra tekrar deneyiniz.",
  "timestamp": "2024-03-25T10:30:00"
}
```

**Server Log:**
```
ERROR - Beklenmedik hata oluştu: 
java.lang.RuntimeException: Database connection failed
    at com.healthtourism.service.ReservationService.getReservationById(ReservationService.java:123)
    ...
```

## 🎨 Frontend Integration

### JavaScript/TypeScript

```typescript
// API client with error handling
async function createReservation(data: ReservationRequest) {
  try {
    const response = await fetch('/api/v1/reservations', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(data)
    });
    
    if (!response.ok) {
      const error: ErrorResponse = await response.json();
      
      // Handle specific error codes
      switch (error.errorCode) {
        case 'RESERVATION_CONFLICT':
          showError('Bu saatte rezervasyon yapılamaz');
          break;
        case 'VALIDATION_FAILED':
          // Show field-specific errors
          Object.entries(error.fieldErrors || {}).forEach(([field, message]) => {
            showFieldError(field, message);
          });
          break;
        case 'INTERNAL_SERVER_ERROR':
          showError('Sunucu hatası. Lütfen daha sonra tekrar deneyin.');
          break;
        default:
          showError(error.message);
      }
      return;
    }
    
    const result: ApiResponse<ReservationDTO> = await response.json();
    showSuccess(result.message);
  } catch (error) {
    showError('Bağlantı hatası');
  }
}
```

## 🔍 Logging Best Practices

### ✅ DOĞRU Loglama:

```java
@ExceptionHandler(BusinessException.class)
public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException ex) {
    // ✅ Full exception with stack trace (server-side only)
    log.error("İş mantığı hatası: {} (ErrorCode: {})", 
        ex.getMessage(), ex.getErrorCode().getCode(), ex);
    
    // ✅ User-friendly message (client-side)
    ErrorResponse error = ErrorResponse.of(ex.getErrorCode().getCode(), ex.getMessage());
    return ResponseEntity.badRequest().body(error);
}
```

### ❌ YANLIŞ Loglama:

```java
@ExceptionHandler(Exception.class)
public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex) {
    // ❌ Stack trace'i client'a gönderme
    return ResponseEntity.status(500)
        .body(new ErrorResponse("ERROR", ex.toString(), LocalDateTime.now())); // Güvensiz!
}
```

## 📝 Error Codes Reference

### Business Errors (400 Bad Request)

| Error Code | Description | Example |
|------------|-------------|---------|
| `RESERVATION_CONFLICT` | Appointment slot already taken | "Doktor bu saatte müsait değil" |
| `DOCTOR_NOT_FOUND` | Doctor not found | "Doktor bulunamadı" |
| `HOSPITAL_NOT_FOUND` | Hospital not found | "Hastane bulunamadı" |
| `INVALID_SPECIALIZATION` | Invalid specialization | "Geçersiz uzmanlık alanı" |

### Validation Errors (400 Bad Request)

| Error Code | Description | Example |
|------------|-------------|---------|
| `VALIDATION_FAILED` | Field validation failed | "Email formatı geçersiz" |
| `INVALID_ARGUMENT` | Invalid method argument | "Geçersiz parametre" |

### Resource Errors (404 Not Found)

| Error Code | Description | Example |
|------------|-------------|---------|
| `RESOURCE_NOT_FOUND` | Resource not found | "Rezervasyon bulunamadı" |
| `RESERVATION_NOT_FOUND` | Reservation not found | "Rezervasyon bulunamadı" |

### System Errors (500 Internal Server Error)

| Error Code | Description |
|------------|-------------|
| `INTERNAL_SERVER_ERROR` | Generic system error |

## 🚀 Migration Guide

### Adım 1: Try-Catch'leri Kaldır

**Önce:**
```java
@PostMapping
public ResponseEntity<?> create(@RequestBody Request request) {
    try {
        return ResponseEntity.ok(service.create(request));
    } catch (Exception e) {
        return ResponseEntity.status(500).body(e.getMessage());
    }
}
```

**Sonra:**
```java
@PostMapping
public ResponseEntity<ApiResponse<ResponseDTO>> create(@Valid @RequestBody RequestDTO request) {
    ResponseDTO data = service.create(request);
    return ResponseEntity.ok(ApiResponse.success("Başarılı", data));
}
```

### Adım 2: Custom Exception Kullan

**Önce:**
```java
if (doctor == null) {
    throw new RuntimeException("Doctor not found"); // ❌ Generic
}
```

**Sonra:**
```java
Doctor doctor = doctorRepository.findById(id)
    .orElseThrow(() -> new ResourceNotFoundException(
        ErrorCode.DOCTOR_NOT_FOUND, 
        "Doktor bulunamadı: " + id
    )); // ✅ Specific exception with error code
```

### Adım 3: Validation Annotation'ları Ekle

**Önce:**
```java
public class Request {
    private String email; // ❌ No validation
}
```

**Sonra:**
```java
public class RequestDTO {
    @Email(message = "Email formatı geçersiz")
    @NotBlank(message = "Email boş olamaz")
    private String email; // ✅ Automatic validation
}
```

## ✅ Checklist

- [ ] Tüm controller'lardan try-catch kaldırıldı
- [ ] Custom exception'lar (BusinessException, ResourceNotFoundException) kullanılıyor
- [ ] DTO'larda @Valid annotation'ları var
- [ ] ErrorResponse formatı tutarlı
- [ ] Stack trace'ler client'a gönderilmiyor
- [ ] Server-side logging yapılıyor
- [ ] Error codes standartlaştırıldı
- [ ] Frontend error handling implementasyonu yapıldı

## 📚 İlgili Dosyalar

- `ErrorResponse.java` - Standard error response DTO
- `GlobalExceptionHandler.java` - Exception handler implementation
- `BusinessException.java` - Business logic exceptions
- `ResourceNotFoundException.java` - Resource not found exceptions
- `ValidationException.java` - Custom validation exceptions
- `ErrorCode.java` - Error code enum

