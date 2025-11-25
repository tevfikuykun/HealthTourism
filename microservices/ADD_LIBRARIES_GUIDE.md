# 📚 Kütüphane Ekleme Rehberi

## ✅ Eklenen Kütüphaneler

### 1. **Bean Validation** ✅
- **Kütüphane:** `spring-boot-starter-validation`
- **Kullanım:** Input validation için DTO'larda `@Valid`, `@NotNull`, `@Email`, `@Size` gibi annotation'lar
- **Durum:** user-service'e eklendi, diğer servislere de eklenmeli

### 2. **Swagger/OpenAPI** ✅
- **Kütüphane:** `springdoc-openapi-starter-webmvc-ui` (v2.3.0)
- **Kullanım:** API dokümantasyonu otomatik oluşturma
- **Erişim:** `http://localhost:PORT/swagger-ui.html`
- **Durum:** user-service'e eklendi, config dosyası oluşturuldu

### 3. **Circuit Breaker (Resilience4j)** ✅
- **Kütüphane:** `spring-cloud-starter-circuitbreaker-resilience4j`
- **Kullanım:** Servis çağrılarında fault tolerance
- **Durum:** user-service'e eklendi

### 4. **Distributed Tracing (Zipkin)** ✅
- **Kütüphaneler:** 
  - `micrometer-tracing-bridge-brave`
  - `zipkin-reporter-brave`
- **Kullanım:** Microservice'ler arası request tracking
- **Durum:** user-service'e eklendi

### 5. **Spring Cache + Redis** ✅
- **Kütüphaneler:**
  - `spring-boot-starter-cache`
  - `spring-boot-starter-data-redis`
- **Kullanım:** Performans için caching
- **Durum:** user-service'e eklendi, CacheConfig oluşturuldu

### 6. **MapStruct** ✅
- **Kütüphane:** `mapstruct` (v1.5.5.Final)
- **Kullanım:** Entity ↔ DTO mapping otomatikleştirme
- **Durum:** user-service'e eklendi, compiler plugin yapılandırıldı

### 7. **Testing Dependencies** ✅
- **Kütüphaneler:**
  - `spring-boot-starter-test`
  - `testcontainers-junit-jupiter` (v1.19.3)
  - `testcontainers-mysql` (v1.19.3)
- **Kullanım:** Integration testler için
- **Durum:** user-service'e eklendi

### 8. **Actuator & Prometheus** ✅
- **Kütüphaneler:**
  - `spring-boot-starter-actuator`
  - `micrometer-registry-prometheus`
- **Kullanım:** Health checks, metrics
- **Durum:** Bazı servislerde zaten var, tüm servislere eklenmeli

## 🔄 Tüm Servislere Uygulanması Gerekenler

### Adım 1: pom.xml Güncellemesi
Her servisin `pom.xml` dosyasına yukarıdaki dependency'ler eklenmeli.

### Adım 2: application.properties Güncellemesi
```properties
# Actuator
management.endpoints.web.exposure.include=health,info,prometheus,metrics
management.endpoint.health.show-details=always
management.metrics.export.prometheus.enabled=true

# Redis Cache
spring.data.redis.host=localhost
spring.data.redis.port=6379
spring.cache.type=redis
spring.cache.cache-names=users,hospitals,doctors
spring.cache.redis.time-to-live=600000

# Swagger
springdoc.api-docs.path=/api-docs
springdoc.swagger-ui.path=/swagger-ui.html

# Tracing
management.tracing.sampling.probability=1.0
```

### Adım 3: Config Sınıfları Ekleme
Her servise şu config sınıfları eklenmeli:
- `SwaggerConfig.java`
- `CacheConfig.java`
- `GlobalExceptionHandler.java`

## 📋 Servis Listesi (Güncellenmesi Gerekenler)

1. ✅ user-service (Örnek olarak güncellendi)
2. ⏳ hospital-service
3. ⏳ doctor-service
4. ⏳ accommodation-service
5. ⏳ flight-service
6. ⏳ car-rental-service
7. ⏳ transfer-service
8. ⏳ package-service
9. ⏳ reservation-service
10. ⏳ payment-service
11. ⏳ notification-service
12. ⏳ medical-document-service
13. ⏳ telemedicine-service
14. ⏳ patient-followup-service
15. ⏳ blog-service
16. ⏳ faq-service
17. ⏳ favorite-service
18. ⏳ appointment-calendar-service
19. ⏳ contact-service
20. ⏳ testimonial-service
21. ⏳ gallery-service
22. ⏳ insurance-service
23. ⏳ auth-service
24. ⏳ monitoring-service
25. ⏳ logging-service
26. ⏳ file-storage-service
27. ⏳ admin-service

## 🚀 Hızlı Uygulama

Tüm servislere aynı anda uygulamak için:
1. `ENHANCED_POM_TEMPLATE.xml` dosyasını referans al
2. Her servisin pom.xml'ini güncelle
3. Config sınıflarını kopyala
4. application.properties'i güncelle

## 📝 Validation Kullanım Örneği

```java
@PostMapping
public ResponseEntity<UserDTO> createUser(@Valid @RequestBody UserRequestDTO request) {
    // Validation otomatik çalışır
    return ResponseEntity.ok(userService.createUser(request));
}

// DTO'da:
public class UserRequestDTO {
    @NotNull(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;
    
    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    private String firstName;
}
```

## 🔍 Circuit Breaker Kullanım Örneği

```java
@CircuitBreaker(name = "hospital-service", fallbackMethod = "fallbackMethod")
public HospitalDTO getHospital(Long id) {
    // Service call
}

public HospitalDTO fallbackMethod(Long id, Exception ex) {
    // Fallback logic
}
```

## 💾 Cache Kullanım Örneği

```java
@Cacheable(value = "users", key = "#id")
public UserDTO getUserById(Long id) {
    // Database call
}

@CacheEvict(value = "users", key = "#id")
public void deleteUser(Long id) {
    // Delete logic
}
```

## 🗺️ MapStruct Kullanım Örneği

```java
@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDTO toDTO(User user);
    User toEntity(UserDTO dto);
    List<UserDTO> toDTOList(List<User> users);
}
```


