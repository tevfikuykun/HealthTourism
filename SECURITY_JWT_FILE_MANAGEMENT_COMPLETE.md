# ✅ Security/JWT ve File Management - TAMAMLANDI

## 📋 Tamamlanan Özellikler

### 1. ✅ JWT Authentication Filter (Common Module)

**Oluşturulan Modül:** `common-jwt-filter`

**Özellikler:**
- JWT token validation
- Spring Security context'e authentication ekleme
- Public endpoint'ler için bypass
- Request attributes'a user bilgileri ekleme (userId, userEmail, userRole)

**Dosyalar:**
- `JwtAuthenticationFilter.java` - JWT token validation filter
- `SecurityConfig.java` - Spring Security konfigürasyonu
- `Role.java` - Role constants (USER, ADMIN, DOCTOR, MODERATOR)
- `RequiresRole.java` - Custom annotation for RBAC

**Kullanım:**
```java
// Servislerde JWT filter'ı kullanmak için:
// 1. pom.xml'e dependency ekle:
<dependency>
    <groupId>com.healthtourism</groupId>
    <artifactId>common-jwt-filter</artifactId>
    <version>1.0.0</version>
</dependency>

// 2. application.properties'e ekle:
jwt.secret=${JWT_SECRET:your-secret-key}
jwt.enabled=true

// 3. SecurityConfig import et (otomatik olarak çalışır)
```

### 2. ✅ Role-Based Access Control (RBAC)

**Özellikler:**
- `@PreAuthorize` annotation desteği
- Role-based endpoint protection
- Method-level security

**Kullanım:**
```java
@PreAuthorize("hasRole('ADMIN')")
@PostMapping("/admin-only")
public ResponseEntity<?> adminOnlyEndpoint() {
    // Sadece ADMIN rolüne sahip kullanıcılar erişebilir
}

@PreAuthorize("hasAnyRole('ADMIN', 'USER')")
@PostMapping("/user-or-admin")
public ResponseEntity<?> userOrAdminEndpoint() {
    // ADMIN veya USER rolüne sahip kullanıcılar erişebilir
}
```

### 3. ✅ Image Upload & Management

**File Storage Service İyileştirmeleri:**

#### 3.1. ImageService
- Image validation (format, dimensions)
- Image compression
- Thumbnail generation
- Resize functionality

**Özellikler:**
- Allowed formats: jpg, jpeg, png, gif, webp
- Max dimensions: 2048x2048
- Automatic compression (quality: 0.8)
- Thumbnail size: 300x300

#### 3.2. FileStorageService İyileştirmeleri
- Image-specific upload method
- Automatic image validation
- Image compression (if enabled)
- Entity-specific image upload (Hospital/Doctor)

#### 3.3. FileStorageController İyileştirmeleri
- Swagger annotations
- Role-based access control
- Image upload endpoint for Hospital/Doctor
- User ID extraction from JWT token

**Yeni Endpoint:**
```bash
POST /api/files/upload/image/{entityType}/{entityId}
# entityType: hospital veya doctor
# entityId: Hospital veya Doctor ID
```

### 4. ✅ Hospital Service - Image Upload

**Eklenenler:**
- `imageUrl` field to Hospital entity
- `thumbnailUrl` field to Hospital entity
- Image upload endpoint: `POST /api/hospitals/{id}/upload-image`
- Admin-only access

### 5. ✅ Password Encoding

**Mevcut:** ✅ Auth Service'de `BCryptPasswordEncoder` zaten mevcut

**Konfigürasyon:**
```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(12); // Increased strength
}
```

## 📁 Oluşturulan/Güncellenen Dosyalar

### Common JWT Filter Module
```
microservices/common-jwt-filter/
├── pom.xml ✨ YENİ
└── src/main/java/com/healthtourism/common/jwt/
    ├── JwtAuthenticationFilter.java ✨ YENİ
    ├── SecurityConfig.java ✨ YENİ
    ├── Role.java ✨ YENİ
    └── RequiresRole.java ✨ YENİ
```

### File Storage Service
```
microservices/file-storage-service/
├── src/main/java/com/healthtourism/filestorageservice/
│   ├── service/
│   │   └── ImageService.java ✨ YENİ
│   ├── controller/
│   │   └── FileStorageController.java 🔄 GÜNCELLENDİ (Swagger, RBAC, Image upload)
│   └── service/
│       └── FileStorageService.java 🔄 GÜNCELLENDİ (Image validation, compression)
├── pom.xml 🔄 GÜNCELLENDİ (Security, Swagger dependencies)
└── src/main/resources/
    └── application.properties 🔄 GÜNCELLENDİ (Image config, JWT config)
```

### Hospital Service
```
microservices/hospital-service/
├── src/main/java/com/healthtourism/hospitalservice/
│   ├── entity/
│   │   └── Hospital.java 🔄 GÜNCELLENDİ (imageUrl, thumbnailUrl fields)
│   └── controller/
│       └── HospitalController.java 🔄 GÜNCELLENDİ (Image upload endpoint)
```

## 🚀 Kullanım Örnekleri

### 1. JWT Token ile API Çağrısı

```bash
# Login ve token alma
POST http://localhost:8001/api/auth/login
{
  "email": "user@example.com",
  "password": "password123"
}

# Response:
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "...",
  "role": "USER",
  ...
}

# Token ile API çağrısı
GET http://localhost:8009/api/reservations/user/1
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### 2. Image Upload (Hospital)

```bash
POST http://localhost:8027/api/files/upload/image/hospital/1
Authorization: Bearer {token}
Content-Type: multipart/form-data

file: [image file]
```

**Response:**
```json
{
  "id": 1,
  "fileName": "uuid_hospital_image.jpg",
  "originalFileName": "hospital_image.jpg",
  "filePath": "./uploads/uuid_hospital_image.jpg",
  "contentType": "image/jpeg",
  "fileSize": 245678,
  "uploadedBy": "1",
  "serviceName": "hospital-service",
  "category": "IMAGE_HOSPITAL",
  "uploadedAt": "2023-10-15T10:30:00"
}
```

### 3. Role-Based Access Control

```java
// Admin-only endpoint
@PreAuthorize("hasRole('ADMIN')")
@PostMapping("/hospitals/{id}/upload-image")
public ResponseEntity<HospitalDTO> uploadHospitalImage(...) {
    // Sadece ADMIN erişebilir
}

// User veya Admin erişebilir
@PreAuthorize("hasAnyRole('ADMIN', 'USER')")
@PostMapping("/reservations")
public ResponseEntity<ReservationDTO> createReservation(...) {
    // USER veya ADMIN erişebilir
}
```

### 4. Image Validation Örneği

```java
// ImageService otomatik olarak:
// 1. Format kontrolü (jpg, jpeg, png, gif, webp)
// 2. Boyut kontrolü (max 2048x2048)
// 3. Compression (quality: 0.8)
// 4. Thumbnail oluşturma (300x300)
```

## 🔧 Konfigürasyon

### application.properties (File Storage Service)

```properties
# File Upload
file.upload-dir=./uploads
file.max-size=10485760
file.image-compression-enabled=true

# Image Configuration
image.allowed-formats=jpg,jpeg,png,gif,webp
image.max-width=2048
image.max-height=2048
image.thumbnail-width=300
image.thumbnail-height=300

# JWT Configuration
jwt.secret=${JWT_SECRET:defaultSecretKeyForDevelopmentOnlyChangeInProduction}
jwt.enabled=true
```

### Servislere JWT Filter Ekleme

**1. pom.xml'e dependency ekle:**
```xml
<dependency>
    <groupId>com.healthtourism</groupId>
    <artifactId>common-jwt-filter</artifactId>
    <version>1.0.0</version>
</dependency>
```

**2. application.properties'e ekle:**
```properties
jwt.secret=${JWT_SECRET:your-secret-key}
jwt.enabled=true
```

**3. Component scan ekle (eğer gerekirse):**
```java
@SpringBootApplication
@ComponentScan(basePackages = {"com.healthtourism.common.jwt", "com.healthtourism.yourservice"})
public class YourServiceApplication {
    // ...
}
```

## 📝 Sonraki Adımlar

### Öncelikli:
1. ✅ JWT Filter - Tamamlandı
2. ✅ RBAC - Tamamlandı
3. ✅ Image Upload - Tamamlandı
4. ⏳ Doctor Service'e image upload endpoint ekle
5. ⏳ Diğer servislere JWT filter entegrasyonu

### İsteğe Bağlı:
- S3/Cloudinary entegrasyonu
- Image CDN entegrasyonu
- Image optimization (WebP conversion)
- Multiple image upload (gallery)

## ✅ Test Checklist

- [x] JWT Filter çalışıyor
- [x] Token validation çalışıyor
- [x] Role-based access control çalışıyor
- [x] Image validation çalışıyor
- [x] Image compression çalışıyor
- [x] Image upload endpoint çalışıyor
- [x] Hospital image upload çalışıyor
- [ ] Doctor image upload test edildi
- [ ] S3/Cloudinary entegrasyonu test edildi

## 🔐 Güvenlik Notları

1. **JWT Secret:** Production'da mutlaka güçlü bir secret key kullanın
2. **HTTPS:** Production'da mutlaka HTTPS kullanın
3. **File Size Limits:** Dosya boyutu limitlerini uygun şekilde ayarlayın
4. **Image Validation:** Tüm image upload'ları validate edin
5. **Access Control:** Admin-only endpoint'leri koruyun

## 📚 Referanslar

- [Spring Security JWT](https://spring.io/guides/topicals/spring-security-architecture)
- [JJWT Library](https://github.com/jwtk/jjwt)
- [Spring Boot File Upload](https://spring.io/guides/gs/uploading-files/)
- [Image Processing in Java](https://docs.oracle.com/javase/tutorial/2d/images/index.html)

---

**Tarih:** 2023-10-15  
**Durum:** ✅ Tamamlandı
