package com.example.HealthTourism.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Register Request DTO
 * Enterprise-grade with comprehensive validation annotations
 * 
 * Security Best Practices:
 * - Password strength validation (min 8 chars, uppercase, lowercase, number, special char)
 * - Email format validation
 * - Name format validation (letters only)
 * - Phone number format validation
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    
    @NotBlank(message = "Email gereklidir")
    @Email(message = "Geçerli bir email adresi giriniz")
    @Size(max = 255, message = "Email en fazla 255 karakter olabilir")
    private String email;
    
    @NotBlank(message = "Şifre gereklidir")
    @Size(min = 8, max = 128, message = "Şifre 8-128 karakter arasında olmalıdır")
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
        message = "Şifre en az bir büyük harf, bir küçük harf, bir rakam ve bir özel karakter içermelidir"
    )
    private String password;
    
    @NotBlank(message = "Ad gereklidir")
    @Size(min = 2, max = 50, message = "Ad 2-50 karakter arasında olmalıdır")
    @Pattern(regexp = "^[a-zA-ZıİöÖüÜşŞğĞçÇ\\s]+$", message = "Ad sadece harf içermelidir")
    private String firstName;
    
    @NotBlank(message = "Soyad gereklidir")
    @Size(min = 2, max = 50, message = "Soyad 2-50 karakter arasında olmalıdır")
    @Pattern(regexp = "^[a-zA-ZıİöÖüÜşŞğĞçÇ\\s]+$", message = "Soyad sadece harf içermelidir")
    private String lastName;
    
    @NotBlank(message = "Telefon numarası gereklidir")
    @Pattern(
        regexp = "^[+]?[(]?[0-9]{1,4}[)]?[-\\s.]?[(]?[0-9]{1,4}[)]?[-\\s.]?[0-9]{1,9}$",
        message = "Geçerli bir telefon numarası giriniz"
    )
    private String phone;
    
    @NotBlank(message = "Ülke gereklidir")
    @Size(max = 100, message = "Ülke adı en fazla 100 karakter olabilir")
    private String country;
    
    /**
     * Kullanıcı rolü (USER, ADMIN, DOCTOR)
     * Default: USER (eğer belirtilmezse)
     */
    @Pattern(regexp = "^(USER|ADMIN|DOCTOR)$", message = "Rol USER, ADMIN veya DOCTOR olmalıdır")
    private String role;
}

