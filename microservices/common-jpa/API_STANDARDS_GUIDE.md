# API Standards Guide

## Genel Bakış

Bu kılavuz, profesyonel API standartları için best practice'leri açıklar:
- Global Response Wrapper (Standart dönüş formatı)
- Swagger/OpenAPI Dökümantasyonu
- Security Context Kullanımı (IDOR koruması)
- Merkezi Hata Yönetimi
- API Versioning

## 1. Global Response Wrapper

### Standart Response Formatı

Tüm başarılı API yanıtları `ApiResponseWrapper` formatında döner:

```json
{
  "success": true,
  "message": "İşlem başarılı",
  "data": { ... },
  "timestamp": "2025-12-25T10:00:00"
}
```

### Kullanım

```java
@PostMapping
public ResponseEntity<ApiResponseWrapper<ReservationDTO>> createReservation(
        @Valid @RequestBody ReservationRequestDTO request) {
    
    ReservationDTO result = reservationService.createReservation(request);
    
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(ApiResponseWrapper.success("Rezervasyon başarıyla oluşturuldu", result));
}
```

### Helper Methods

```java
// Başarılı yanıt (data ile)
ApiResponseWrapper.success("Mesaj", data);

// Başarılı yanıt (data olmadan)
ApiResponseWrapper.success("Mesaj");

// Varsayılan mesaj ile
ApiResponseWrapper.success(data);

// Hata yanıtı
ApiResponseWrapper.error("Hata mesajı");
```

### Error Response Formatı

Hata yanıtları `ErrorResponse` formatında (GlobalExceptionHandler tarafından):

```json
{
  "success": false,
  "status": 400,
  "errorCode": "VALIDATION_5001",
  "message": "Doğrulama hatası",
  "timestamp": "2025-12-25T10:00:00",
  "validationErrors": {
    "email": "E-posta formatı geçersiz"
  }
}
```

## 2. Swagger/OpenAPI Dökümantasyonu

### Controller Annotation'ları

```java
@RestController
@RequestMapping("/api/v1/reservations")
@Tag(name = "Reservation", description = "Rezervasyon yönetimi uç noktaları")
@Validated
@SecurityRequirement(name = "bearer-jwt")
public class ReservationController {
    // ...
}
```

### Endpoint Annotation'ları

```java
@PostMapping
@Operation(
    summary = "Yeni rezervasyon oluştur",
    description = "Hastane, doktor ve konaklama bilgilerini içeren yeni bir rezervasyon oluşturur."
)
@ApiResponses(value = {
    @ApiResponse(
        responseCode = "201",
        description = "Rezervasyon başarıyla oluşturuldu"
    ),
    @ApiResponse(
        responseCode = "400",
        description = "Geçersiz giriş verisi"
    ),
    @ApiResponse(
        responseCode = "409",
        description = "Randevu çakışması"
    )
})
@PreAuthorize("hasRole('USER')")
public ResponseEntity<ApiResponseWrapper<ReservationDTO>> createReservation(
        @Valid @RequestBody ReservationRequestDTO request) {
    // ...
}
```

### Parameter Annotation'ları

```java
@GetMapping("/{id}")
public ResponseEntity<ApiResponseWrapper<ReservationDTO>> getReservation(
        @Parameter(
            description = "Rezervasyon ID",
            required = true,
            example = "123e4567-e89b-12d3-a456-426614174000"
        )
        @PathVariable UUID id) {
    // ...
}
```

### Swagger UI Erişimi

- **Local**: `http://localhost:{port}/swagger-ui.html`
- **API Docs**: `http://localhost:{port}/api-docs`

## 3. Security Context Kullanımı (IDOR Koruması)

### ❌ YANLIŞ: User ID'yi Request'ten Almak

```java
// GÜVENLİK AÇIĞI: Kullanıcı başkasının ID'sini kullanabilir
@GetMapping("/user/{userId}")
public ResponseEntity<List<ReservationDTO>> getReservations(
        @PathVariable Long userId) {
    return ResponseEntity.ok(service.getReservationsByUser(userId));
}
```

### ✅ DOĞRU: User ID'yi SecurityContext'ten Almak

```java
// GÜVENLİ: Kullanıcı sadece kendi verilerine erişebilir
@GetMapping("/my-reservations")
public ResponseEntity<ApiResponseWrapper<List<ReservationDTO>>> getMyReservations() {
    UUID currentUserId = SecurityContextHelper.getCurrentUserId();
    List<ReservationDTO> list = service.getReservationsByUser(currentUserId);
    return ResponseEntity.ok(ApiResponseWrapper.success("Liste getirildi", list));
}
```

### SecurityContextHelper Kullanımı

```java
// User ID al
UUID userId = SecurityContextHelper.getCurrentUserId();

// User email al
String email = SecurityContextHelper.getCurrentUserEmail();

// User roles al
String[] roles = SecurityContextHelper.getCurrentUserRoles();

// Role kontrolü
if (SecurityContextHelper.hasRole("ROLE_ADMIN")) {
    // Admin işlemleri
}

// Authentication kontrolü
if (SecurityContextHelper.isAuthenticated()) {
    // Authenticated user işlemleri
}
```

### Ownership Verification

```java
@GetMapping("/{id}")
public ResponseEntity<ApiResponseWrapper<ReservationDTO>> getReservation(@PathVariable UUID id) {
    UUID currentUserId = SecurityContextHelper.getCurrentUserId();
    
    Reservation reservation = service.findById(id)
        .orElseThrow(() -> ResourceNotFoundException.reservationNotFound(id));
    
    // Ownership kontrolü
    if (!reservation.getUserId().equals(currentUserId) && 
        !SecurityContextHelper.hasRole("ROLE_ADMIN")) {
        throw new AccessDeniedException("Bu kaynağa erişim yetkiniz yok");
    }
    
    return ResponseEntity.ok(ApiResponseWrapper.success(reservation));
}
```

## 4. Merkezi Hata Yönetimi

### GlobalExceptionHandler

Tüm exception'lar `GlobalExceptionHandler` tarafından yakalanır:

```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException ex) {
        // Otomatik olarak ErrorResponse formatında döner
    }
    
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
        // 404 yanıtı döner
    }
    
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(ValidationException ex) {
        // Validation errors ile birlikte döner
    }
}
```

### Controller'da Try-Catch Kullanımı

❌ **YANLIŞ**: Controller'da try-catch

```java
@PostMapping
public ResponseEntity<?> create(@RequestBody RequestDTO request) {
    try {
        return ResponseEntity.ok(service.create(request));
    } catch (Exception e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
```

✅ **DOĞRU**: GlobalExceptionHandler kullanımı

```java
@PostMapping
public ResponseEntity<ApiResponseWrapper<ResponseDTO>> create(@Valid @RequestBody RequestDTO request) {
    // Exception handling GlobalExceptionHandler tarafından yapılır
    ResponseDTO result = service.create(request);
    return ResponseEntity.ok(ApiResponseWrapper.success("Başarılı", result));
}
```

## 5. API Versioning

### URL Versioning Pattern

Tüm API endpoint'leri `/api/v1/` prefix'i ile başlar:

```java
@RestController
@RequestMapping("/api/v1/reservations")
public class ReservationController {
    // ...
}
```

### Version Naming Convention

- `/api/v1/` - Version 1 (current)
- `/api/v2/` - Version 2 (future)
- `/api/v1.1/` - Minor version (optional)

### Migration Strategy

Eski version'lar deprecated olarak işaretlenir:

```java
@Deprecated
@RestController
@RequestMapping("/api/reservations") // Versionless (deprecated)
public class ReservationControllerV0 {
    // ...
}

@RestController
@RequestMapping("/api/v1/reservations") // Current version
public class ReservationController {
    // ...
}
```

## 6. Örnek Profesyonel Controller

```java
@RestController
@RequestMapping("/api/v1/reservations")
@Tag(name = "Reservation", description = "Rezervasyon yönetimi uç noktaları")
@Validated
@SecurityRequirement(name = "bearer-jwt")
public class ReservationController {
    
    private final ReservationServiceInterface reservationService;
    private final ReservationMapper reservationMapper;
    
    @PostMapping
    @Operation(
        summary = "Yeni rezervasyon oluştur",
        description = "Hastane, doktor ve konaklama bilgilerini içeren yeni bir rezervasyon oluşturur."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Rezervasyon başarıyla oluşturuldu"),
        @ApiResponse(responseCode = "400", description = "Geçersiz giriş verisi"),
        @ApiResponse(responseCode = "409", description = "Randevu çakışması")
    })
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponseWrapper<ReservationResponseDTO>> createReservation(
            @Valid @RequestBody CreateReservationRequestDTO request) {
        
        // Security: Get user ID from SecurityContext
        UUID currentUserId = SecurityContextHelper.getCurrentUserId();
        
        // Map Request DTO to Entity
        Reservation reservation = reservationMapper.toEntity(request);
        reservation.setUserId(currentUserId);
        
        // Business logic
        Reservation created = reservationService.createReservation(reservation);
        
        // Map Entity to Response DTO
        ReservationResponseDTO response = reservationMapper.toResponseDTO(created);
        
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ApiResponseWrapper.success("Rezervasyon başarıyla oluşturuldu", response));
    }
    
    @GetMapping("/my-reservations")
    @Operation(summary = "Kullanıcının rezervasyonlarını listele")
    @ApiResponse(responseCode = "200", description = "Rezervasyonlar başarıyla getirildi")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponseWrapper<List<ReservationResponseDTO>>> getMyReservations() {
        
        // Security: Get user ID from SecurityContext
        UUID currentUserId = SecurityContextHelper.getCurrentUserId();
        
        // Business logic
        List<Reservation> reservations = reservationService.getReservationsByUserId(currentUserId);
        
        // Map to Response DTOs
        List<ReservationResponseDTO> response = reservationMapper.toResponseDTOList(reservations);
        
        return ResponseEntity.ok(
            ApiResponseWrapper.success("Rezervasyonlar başarıyla getirildi", response)
        );
    }
}
```

## 7. Best Practices

### 1. Response Wrapper Kullanımı

- ✅ Tüm başarılı yanıtlar `ApiResponseWrapper` ile
- ✅ Hata yanıtları `ErrorResponse` ile (GlobalExceptionHandler)
- ❌ Direkt entity veya DTO dönme

### 2. Security

- ✅ User ID'yi SecurityContext'ten al
- ✅ Ownership verification yap
- ❌ User ID'yi path variable veya request body'den alma

### 3. Documentation

- ✅ Tüm endpoint'ler için Swagger annotation'ları
- ✅ @Operation, @ApiResponse kullan
- ✅ Parameter description'ları ekle

### 4. Error Handling

- ✅ GlobalExceptionHandler kullan
- ❌ Controller'da try-catch kullanma
- ✅ BusinessException, ResourceNotFoundException kullan

### 5. API Versioning

- ✅ `/api/v1/` prefix kullan
- ✅ Deprecated endpoint'leri işaretle
- ✅ Migration strategy planla

## 8. Frontend Integration

Frontend ekibi şu formatı her zaman bekleyebilir:

```typescript
// Success Response
interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
  timestamp: string;
}

// Error Response
interface ErrorResponse {
  success: false;
  status: number;
  errorCode: string;
  message: string;
  timestamp: string;
  validationErrors?: Record<string, string>;
}

// Usage
const response = await api.get('/api/v1/reservations/my-reservations');
if (response.data.success) {
  const reservations = response.data.data; // Type-safe
} else {
  // Handle error (should not happen with GlobalExceptionHandler)
}
```

