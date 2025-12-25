# API/Controller Katmanı Kılavuzu

## Genel Bakış

Bu kılavuz, API/Controller katmanı için best practice'leri açıklar:
- Global Error Handling (Merkezi Hata Yönetimi)
- API Versioning (API Versiyonlama)
- Rate Limiting (İstek Sınırlama)

## 1. Global Error Handling (Merkezi Hata Yönetimi)

### EnhancedGlobalExceptionHandler

Tüm exception'lar `EnhancedGlobalExceptionHandler` tarafından yakalanır. Controller'larda try-catch blokları kullanılmaz.

```java
@RestControllerAdvice
public class EnhancedGlobalExceptionHandler extends ResponseEntityExceptionHandler {
    // Tüm exception'ları handle eder
}
```

### ❌ YANLIŞ: Controller'da Try-Catch

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

### ✅ DOĞRU: GlobalExceptionHandler Kullanımı

```java
@PostMapping
public ResponseEntity<ApiResponseWrapper<ResponseDTO>> create(@Valid @RequestBody RequestDTO request) {
    // Exception handling GlobalExceptionHandler tarafından yapılır
    ResponseDTO result = service.create(request);
    return ResponseEntity.ok(ApiResponseWrapper.success("Başarılı", result));
}
```

### Handle Edilen Exception'lar

- `BusinessException` → HTTP 400
- `ResourceNotFoundException` → HTTP 404
- `ValidationException` → HTTP 400 (validation errors ile)
- `AccessDeniedException` → HTTP 403
- `MethodArgumentNotValidException` → HTTP 400
- `IllegalArgumentException` → HTTP 400
- `Exception` → HTTP 500

## 2. API Versioning (API Versiyonlama)

### Versioning Stratejileri

#### Path-Based Versioning (Önerilen)

```
/api/v1/reservations
/api/v2/reservations
```

```java
@RestController
@RequestMapping("/api/v1/reservations")
@ApiVersion("v1")
public class ReservationController {
    // ...
}
```

#### Header-Based Versioning (Alternatif)

```
X-API-Version: v1
```

### @ApiVersion Annotation

```java
@ApiVersion("v1")
@RestController
@RequestMapping("/api/v1/reservations")
public class ReservationController {
    // ...
}

// Deprecated version
@ApiVersion(value = "v1", deprecated = true, deprecationMessage = "Use v2 instead")
@RestController
@RequestMapping("/api/v1/reservations")
public class ReservationControllerV1 {
    // ...
}
```

### Response Headers

Version bilgisi response header'larında döner:

```
X-API-Version: v1
X-API-Version-Deprecated: true (if deprecated)
Warning: 299 - This API version is deprecated
```

### Migration Strategy

```java
// v1 (deprecated)
@ApiVersion(value = "v1", deprecated = true)
@RequestMapping("/api/v1/reservations")
public class ReservationControllerV1 {
    // Eski implementation
}

// v2 (current)
@ApiVersion("v2")
@RequestMapping("/api/v2/reservations")
public class ReservationControllerV2 {
    // Yeni implementation
}
```

## 3. Rate Limiting (İstek Sınırlama)

### @RateLimited Annotation

Rate limiting endpoint veya controller seviyesinde uygulanabilir:

```java
// Controller seviyesinde (tüm endpoint'ler için)
@RateLimited(requestsPerMinute = 60)
@RestController
@RequestMapping("/api/v1/reservations")
public class ReservationController {
    // ...
}

// Endpoint seviyesinde (özel limit)
@RateLimited(requestsPerMinute = 30, errorMessage = "Rezervasyon limiti aşıldı")
@PostMapping
public ResponseEntity<?> createReservation(@RequestBody RequestDTO request) {
    // ...
}
```

### Rate Limiting Parametreleri

```java
@RateLimited(
    requestsPerMinute = 60,        // Dakikada maksimum istek sayısı
    windowSeconds = 60,             // Zaman penceresi (saniye)
    perUser = true,                 // Kullanıcı bazlı mı, IP bazlı mı
    errorMessage = "Rate limit exceeded" // Özel hata mesajı
)
```

### Rate Limit Headers

Response header'larında rate limit bilgileri döner:

```
X-RateLimit-Limit: 60
X-RateLimit-Remaining: 45
X-RateLimit-Reset: 1234567890
```

### Rate Limit Key Stratejisi

- **perUser = true**: `{endpoint}:{userId}` (kullanıcı bazlı)
- **perUser = false**: `{endpoint}:{ip}` (IP bazlı)

### Redis Kullanımı

Rate limiting Redis kullanır (distributed rate limiting):

```yaml
# application.properties
spring.data.redis.host=localhost
spring.data.redis.port=6379
security.ratelimit.enabled=true
security.ratelimit.default-requests-per-minute=100
```

## 4. Örnek Profesyonel Controller

```java
@RestController
@RequestMapping("/api/v1/reservations")
@Tag(name = "Reservation", description = "Rezervasyon yönetimi")
@SecurityRequirement(name = "bearer-jwt")
@ApiVersion("v1")
@RateLimited(requestsPerMinute = 60) // Controller-level rate limit
@Validated
public class ReservationController {
    
    private final ReservationServiceInterface reservationService;
    private final ReservationMapper reservationMapper;
    
    @PostMapping
    @Operation(summary = "Yeni rezervasyon oluştur")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Rezervasyon oluşturuldu"),
        @ApiResponse(responseCode = "400", description = "Geçersiz istek"),
        @ApiResponse(responseCode = "429", description = "Rate limit aşıldı")
    })
    @PreAuthorize("hasRole('USER')")
    @RateLimited(requestsPerMinute = 30) // Endpoint-level rate limit
    public ResponseEntity<ApiResponseWrapper<ReservationResponseDTO>> createReservation(
            @Valid @RequestBody CreateReservationRequestDTO request) {
        
        // Security: Get user ID from SecurityContext
        UUID userId = SecurityContextHelper.getCurrentUserId();
        
        // Business logic (no try-catch - handled by GlobalExceptionHandler)
        Reservation reservation = reservationMapper.toEntity(request);
        reservation.setUserId(userId);
        Reservation created = reservationService.createReservation(reservation);
        ReservationResponseDTO response = reservationMapper.toResponseDTO(created);
        
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ApiResponseWrapper.success("Rezervasyon oluşturuldu", response));
    }
    
    @GetMapping("/my-reservations")
    @Operation(summary = "Kullanıcının rezervasyonlarını listele")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponseWrapper<List<ReservationResponseDTO>>> getMyReservations() {
        UUID userId = SecurityContextHelper.getCurrentUserId();
        List<Reservation> reservations = reservationService.getReservationsByUserId(userId);
        List<ReservationResponseDTO> response = reservationMapper.toResponseDTOList(reservations);
        return ResponseEntity.ok(ApiResponseWrapper.success("Liste getirildi", response));
    }
}
```

## 5. Best Practices

### 1. Error Handling

- ✅ GlobalExceptionHandler kullan
- ❌ Controller'da try-catch kullanma
- ✅ Custom exception'lar kullan (BusinessException, ResourceNotFoundException)

### 2. API Versioning

- ✅ Path-based versioning kullan (`/api/v1/`)
- ✅ @ApiVersion annotation ekle
- ✅ Deprecated version'ları işaretle
- ✅ Migration planı oluştur

### 3. Rate Limiting

- ✅ Endpoint'lerde @RateLimited kullan
- ✅ Kritik endpoint'lerde daha düşük limit
- ✅ Redis kullan (distributed rate limiting)
- ✅ Rate limit headers ekle

### 4. Security

- ✅ User ID'yi SecurityContext'ten al
- ✅ @PreAuthorize ile role kontrolü
- ✅ Ownership verification

## 6. Configuration

### application.properties

```properties
# Rate Limiting
security.ratelimit.enabled=true
security.ratelimit.default-requests-per-minute=100

# Redis (for rate limiting)
spring.data.redis.host=localhost
spring.data.redis.port=6379
```

### Interceptor Configuration

Rate limiting ve versioning interceptor'ları otomatik olarak kayıt edilir:

```java
@Configuration
public class RateLimitingConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(rateLimitingInterceptor)
            .addPathPatterns("/api/**");
    }
}
```

## 7. Response Examples

### Success Response

```json
{
  "success": true,
  "message": "Rezervasyon oluşturuldu",
  "data": { ... },
  "timestamp": "2025-12-25T10:00:00"
}
```

**Headers:**
```
X-API-Version: v1
X-RateLimit-Limit: 60
X-RateLimit-Remaining: 59
X-RateLimit-Reset: 1234567890
```

### Error Response (Rate Limit)

```json
{
  "success": false,
  "status": 429,
  "errorCode": "RATE_LIMIT_EXCEEDED",
  "message": "Rate limit exceeded. Please try again later.",
  "timestamp": "2025-12-25T10:00:00"
}
```

**Headers:**
```
X-RateLimit-Limit: 60
X-RateLimit-Remaining: 0
X-RateLimit-Reset: 1234567890
```

### Error Response (Deprecated Version)

**Headers:**
```
X-API-Version: v1
X-API-Version-Deprecated: true
Warning: 299 - This API version is deprecated. Please migrate to v2.
```

## 8. Migration Guide

### Mevcut Controller'ları Güncelleme

1. **GlobalExceptionHandler**: Try-catch bloklarını kaldır
2. **API Versioning**: `/api/v1/` prefix ekle, @ApiVersion ekle
3. **Rate Limiting**: @RateLimited annotation ekle
4. **Response Wrapper**: ApiResponseWrapper kullan

Örnek:

```java
// ÖNCE
@RestController
@RequestMapping("/api/reservations")
public class ReservationController {
    @PostMapping
    public ResponseEntity<?> create(@RequestBody RequestDTO request) {
        try {
            return ResponseEntity.ok(service.create(request));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}

// SONRA
@RestController
@RequestMapping("/api/v1/reservations")
@ApiVersion("v1")
@RateLimited(requestsPerMinute = 60)
public class ReservationController {
    @PostMapping
    public ResponseEntity<ApiResponseWrapper<ResponseDTO>> create(@Valid @RequestBody RequestDTO request) {
        ResponseDTO result = service.create(request);
        return ResponseEntity.ok(ApiResponseWrapper.success("Başarılı", result));
    }
}
```

