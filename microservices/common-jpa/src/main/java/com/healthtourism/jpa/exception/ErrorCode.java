package com.healthtourism.jpa.exception;

/**
 * Error Code Enum
 * 
 * Centralized error codes for business exceptions.
 * Each error code represents a specific business rule violation or error condition.
 * 
 * Error codes follow the pattern: {DOMAIN}_{ERROR_TYPE}
 * Example: PATIENT_NOT_FOUND, APPOINTMENT_CONFLICT, etc.
 */
public enum ErrorCode {
    
    // Patient Errors (1000-1999)
    PATIENT_NOT_FOUND("PATIENT_1001", "Hasta bulunamadı"),
    PATIENT_ALREADY_EXISTS("PATIENT_1002", "Bu hasta zaten kayıtlı"),
    PATIENT_INACTIVE("PATIENT_1003", "Hasta aktif değil"),
    PATIENT_AGE_INVALID("PATIENT_1004", "Hasta yaşı geçersiz"),
    PATIENT_PHONE_INVALID("PATIENT_1005", "Telefon numarası formatı geçersiz"),
    PATIENT_EMAIL_INVALID("PATIENT_1006", "E-posta adresi formatı geçersiz"),
    
    // Appointment/Reservation Errors (2000-2999)
    APPOINTMENT_NOT_FOUND("APPOINTMENT_2001", "Randevu bulunamadı"),
    APPOINTMENT_CONFLICT("APPOINTMENT_2002", "Bu saatte başka bir randevu var"),
    APPOINTMENT_DATE_PAST("APPOINTMENT_2003", "Randevu tarihi geçmişte olamaz"),
    APPOINTMENT_DATE_INVALID("APPOINTMENT_2004", "Randevu tarihi geçersiz"),
    APPOINTMENT_DOUBLE_BOOKING("APPOINTMENT_2005", "Bir hasta aynı güne iki randevu alamaz"),
    APPOINTMENT_CANCELLED("APPOINTMENT_2006", "Randevu iptal edilmiş"),
    APPOINTMENT_COMPLETED("APPOINTMENT_2007", "Randevu tamamlanmış"),
    
    // Doctor Errors (3000-3999)
    DOCTOR_NOT_FOUND("DOCTOR_3001", "Doktor bulunamadı"),
    DOCTOR_UNAVAILABLE("DOCTOR_3002", "Doktor müsait değil"),
    DOCTOR_INACTIVE("DOCTOR_3003", "Doktor aktif değil"),
    
    // Hospital Errors (4000-4999)
    HOSPITAL_NOT_FOUND("HOSPITAL_4001", "Hastane bulunamadı"),
    HOSPITAL_INACTIVE("HOSPITAL_4002", "Hastane aktif değil"),
    HOSPITAL_CAPACITY_FULL("HOSPITAL_4003", "Hastane kapasitesi dolu"),
    
    // Validation Errors (5000-5999)
    VALIDATION_FAILED("VALIDATION_5001", "Doğrulama hatası"),
    REQUIRED_FIELD_MISSING("VALIDATION_5002", "Zorunlu alan eksik"),
    FIELD_FORMAT_INVALID("VALIDATION_5003", "Alan formatı geçersiz"),
    FIELD_VALUE_INVALID("VALIDATION_5004", "Alan değeri geçersiz"),
    PHONE_FORMAT_INVALID("VALIDATION_5005", "Telefon numarası formatı geçersiz"),
    EMAIL_FORMAT_INVALID("VALIDATION_5006", "E-posta formatı geçersiz"),
    AGE_LIMIT_EXCEEDED("VALIDATION_5007", "Yaş sınırı aşıldı"),
    DATE_INVALID("VALIDATION_5008", "Tarih geçersiz"),
    
    // Business Rule Errors (6000-6999)
    BUSINESS_RULE_VIOLATION("BUSINESS_6001", "İş kuralı ihlali"),
    DUPLICATE_ENTRY("BUSINESS_6002", "Tekrarlanan kayıt"),
    OPERATION_NOT_ALLOWED("BUSINESS_6003", "İşlem izin verilmiyor"),
    INSUFFICIENT_PERMISSIONS("BUSINESS_6004", "Yetersiz yetki"),
    
    // Rate Limiting Errors (7000-7999)
    RATE_LIMIT_EXCEEDED("RATE_LIMIT_7001", "Rate limit aşıldı"),
    
    // System Errors (9000-9999)
    INTERNAL_ERROR("SYSTEM_9001", "Sistem hatası"),
    DATABASE_ERROR("SYSTEM_9002", "Veritabanı hatası"),
    EXTERNAL_SERVICE_ERROR("SYSTEM_9003", "Harici servis hatası");
    
    private final String code;
    private final String message;
    
    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getMessage() {
        return message;
    }
    
    /**
     * Find ErrorCode by code string
     */
    public static ErrorCode findByCode(String code) {
        for (ErrorCode errorCode : values()) {
            if (errorCode.getCode().equals(code)) {
                return errorCode;
            }
        }
        return INTERNAL_ERROR; // Default fallback
    }
}

