# Entegrasyon Servisleri Kılavuzu

## Genel Bakış

Bu kılavuz, Email/SMS ve File Storage entegrasyon servislerinin kullanımını açıklar.

## 1. Email Service (SendGrid)

### Interface: `EmailService`

Email gönderme işlemleri için professional interface.

### Implementation: `SendGridEmailServiceImpl`

SendGrid API kullanarak email gönderir.

### Kullanım

```java
@Autowired
private EmailService emailService;

// Basit email gönderme
emailService.sendEmail(
    "patient@example.com",
    "Randevu Onayı",
    "<h1>Randevunuz onaylandı</h1>"
);

// Randevu onayı email'i
Map<String, String> appointmentDetails = Map.of(
    "date", "2025-01-15",
    "time", "14:00",
    "doctor", "Dr. Ahmet Yılmaz",
    "hospital", "İstanbul Hastanesi"
);
emailService.sendAppointmentConfirmation(
    "patient@example.com",
    "Ahmet Yılmaz",
    appointmentDetails
);

// Welcome email
emailService.sendWelcomeEmail("newuser@example.com", "Mehmet Demir");

// Password reset email
emailService.sendPasswordResetEmail(
    "user@example.com",
    "reset-token-123",
    60 // 60 dakika geçerli
);

// Attachment ile email
Map<String, Object> attachments = Map.of(
    "medical-report.pdf", "/path/to/file.pdf"
);
emailService.sendEmailWithAttachments(
    "patient@example.com",
    "Tıbbi Rapor",
    "<p>Ekte tıbbi raporunuz bulunmaktadır.</p>",
    attachments
);
```

### Configuration

```properties
sendgrid.enabled=true
sendgrid.api.key=${SENDGRID_API_KEY}
sendgrid.from.email=noreply@healthtourism.com
sendgrid.from.name=Health Tourism
```

## 2. SMS Service (Twilio)

### Interface: `SMSService`

SMS gönderme işlemleri için professional interface.

### Implementation: `TwilioSMSServiceImpl`

Twilio API kullanarak SMS gönderir.

### Kullanım

```java
@Autowired
private SMSService smsService;

// Basit SMS gönderme
smsService.sendSMS(
    "+905551234567",
    "Randevunuz 15 Ocak 2025, 14:00'te."
);

// OTP gönderme
smsService.sendOTP("+905551234567", "123456");

// Randevu onayı SMS
Map<String, String> appointmentDetails = Map.of(
    "date", "2025-01-15",
    "time", "14:00",
    "doctor", "Dr. Ahmet Yılmaz",
    "hospital", "İstanbul Hastanesi"
);
smsService.sendAppointmentConfirmation(
    "+905551234567",
    appointmentDetails
);

// Randevu hatırlatıcı SMS
smsService.sendAppointmentReminder(
    "+905551234567",
    appointmentDetails,
    24 // 24 saat önceden hatırlat
);

// Bildirim SMS
smsService.sendNotification(
    "+905551234567",
    "Tıbbi raporunuz hazır."
);

// Toplu SMS
smsService.sendBulkSMS(
    new String[]{"+905551234567", "+905559876543"},
    "Genel duyuru mesajı"
);
```

### Configuration

```properties
twilio.enabled=true
twilio.account.sid=${TWILIO_ACCOUNT_SID}
twilio.auth.token=${TWILIO_AUTH_TOKEN}
twilio.phone.number=${TWILIO_PHONE_NUMBER}
```

## 3. File Storage Service

### Interface: `FileStorageService`

File storage işlemleri için professional interface.

### Implementations

#### Azure Blob Storage: `AzureBlobStorageServiceImpl`

Azure Blob Storage kullanarak dosya saklar.

#### AWS S3: `AwsS3StorageServiceImpl`

AWS S3 kullanarak dosya saklar.

### Kullanım

```java
@Autowired
private FileStorageService fileStorageService;

// Dosya yükleme
byte[] fileContent = Files.readAllBytes(Paths.get("medical-report.pdf"));
String fileUrl = fileStorageService.uploadFile(
    "medical-report.pdf",
    fileContent,
    "application/pdf",
    Map.of("patientId", "123", "category", "medical-document")
);

// InputStream ile yükleme
try (InputStream inputStream = new FileInputStream("report.pdf")) {
    String fileUrl = fileStorageService.uploadFile(
        "report.pdf",
        inputStream,
        "application/pdf",
        null
    );
}

// Dosya indirme
byte[] downloadedContent = fileStorageService.downloadFile(fileUrl);

// InputStream ile indirme
InputStream stream = fileStorageService.downloadFileAsStream(fileUrl);

// Dosya silme
fileStorageService.deleteFile(fileUrl);

// Dosya varlık kontrolü
boolean exists = fileStorageService.fileExists(fileUrl);

// Dosya metadata
Map<String, String> metadata = fileStorageService.getFileMetadata(fileUrl);
String contentType = metadata.get("contentType");
long fileSize = Long.parseLong(metadata.get("size"));

// Pre-signed URL oluşturma (geçici erişim)
String presignedUrl = fileStorageService.generatePresignedUrl(fileUrl, 60); // 60 dakika

// Dosya listeleme
List<String> files = fileStorageService.listFiles("medical-documents/2025/");

// Dosya kopyalama
fileStorageService.copyFile(sourceUrl, destinationUrl);

// Dosya boyutu
long fileSize = fileStorageService.getFileSize(fileUrl);
```

### Configuration

#### Azure Blob Storage

```properties
azure.storage.enabled=true
azure.storage.connection-string=${AZURE_STORAGE_CONNECTION_STRING}
azure.storage.container-name=healthtourism-documents
```

#### AWS S3

```properties
aws.s3.enabled=true
aws.s3.region=us-east-1
aws.s3.bucket-name=healthtourism-documents
aws.s3.access-key-id=${AWS_ACCESS_KEY_ID}
aws.s3.secret-access-key=${AWS_SECRET_ACCESS_KEY}
```

### Dependencies

#### Azure Blob Storage

```xml
<dependency>
    <groupId>com.azure</groupId>
    <artifactId>azure-storage-blob</artifactId>
    <version>12.23.0</version>
</dependency>
```

#### AWS S3

```xml
<dependency>
    <groupId>software.amazon.awssdk</groupId>
    <artifactId>s3</artifactId>
    <version>2.20.0</version>
</dependency>
```

## 4. Örnek Kullanım Senaryoları

### Randevu Onayı Senaryosu

```java
@Service
public class AppointmentService {
    
    @Autowired
    private EmailService emailService;
    
    @Autowired
    private SMSService smsService;
    
    public void confirmAppointment(Appointment appointment) {
        // Email gönder
        Map<String, String> details = Map.of(
            "date", appointment.getDate().toString(),
            "time", appointment.getTime().toString(),
            "doctor", appointment.getDoctor().getName(),
            "hospital", appointment.getHospital().getName()
        );
        
        emailService.sendAppointmentConfirmation(
            appointment.getPatient().getEmail(),
            appointment.getPatient().getName(),
            details
        );
        
        // SMS gönder
        smsService.sendAppointmentConfirmation(
            appointment.getPatient().getPhone(),
            details
        );
    }
}
```

### Sağlık Dökümanı Yükleme Senaryosu

```java
@Service
public class MedicalDocumentService {
    
    @Autowired
    private FileStorageService fileStorageService;
    
    public String uploadMedicalDocument(
            MultipartFile file,
            UUID patientId,
            String documentType) throws IOException {
        
        byte[] fileContent = file.getBytes();
        String contentType = file.getContentType();
        
        Map<String, String> metadata = Map.of(
            "patientId", patientId.toString(),
            "documentType", documentType,
            "uploadDate", LocalDateTime.now().toString()
        );
        
        String fileUrl = fileStorageService.uploadFile(
            file.getOriginalFilename(),
            fileContent,
            contentType,
            metadata
        );
        
        return fileUrl;
    }
    
    public String generateDocumentAccessUrl(String fileUrl) {
        // Geçici erişim URL'i oluştur (15 dakika geçerli)
        return fileStorageService.generatePresignedUrl(fileUrl, 15);
    }
}
```

## 5. Best Practices

### Email Service

- ✅ HTML email template'leri kullan
- ✅ Responsive email tasarımı
- ✅ Email içeriğinde sensitive bilgi bulundurma
- ✅ Rate limiting uygula

### SMS Service

- ✅ SMS mesajları kısa tut (160 karakter önerilir)
- ✅ Türkçe karakter desteği kontrol et
- ✅ OTP kodları için güvenli random generator kullan
- ✅ SMS maliyetlerini göz önünde bulundur

### File Storage Service

- ✅ Dosya boyutu limitleri koy
- ✅ Content-type validation yap
- ✅ Dosya isimlerini sanitize et
- ✅ Metadata kullanarak dosya bilgilerini sakla
- ✅ Pre-signed URL'ler için expiration time belirle
- ✅ GDPR/KVKK uyumluluğu için encryption kullan

## 6. Güvenlik

- ✅ API key'leri environment variable olarak sakla
- ✅ Production'da secret management system kullan (Vault, AWS Secrets Manager, etc.)
- ✅ File upload'larında virus scanning yap
- ✅ Pre-signed URL'ler için kısa expiration time kullan
- ✅ Access control policies uygula

## 7. Error Handling

Tüm servisler exception fırlatabilir. GlobalExceptionHandler ile handle edin:

```java
try {
    emailService.sendEmail(to, subject, content);
} catch (Exception e) {
    logger.error("Failed to send email: {}", e.getMessage(), e);
    // Error handling
}
```

## 8. Testing

Test ortamında servisleri disable edebilirsiniz:

```properties
sendgrid.enabled=false
twilio.enabled=false
azure.storage.enabled=false
aws.s3.enabled=false
```

Servisler disabled olduğunda log mesajları yazdırır ancak gerçek API çağrıları yapmaz.

