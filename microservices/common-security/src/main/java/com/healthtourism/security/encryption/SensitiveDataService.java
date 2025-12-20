package com.healthtourism.security.encryption;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Service for encrypting/decrypting sensitive data
 * TC Kimlik, Pasaport, Hastalık bilgileri
 */
@Service
public class SensitiveDataService {

    private final AES256GCMEncryptor encryptor;

    public SensitiveDataService(@Value("${encryption.aes.key}") String encryptionKey) {
        this.encryptor = new AES256GCMEncryptor(encryptionKey);
    }

    /**
     * Encrypt TC Kimlik No
     */
    public String encryptTCKimlik(String tcKimlik) {
        if (tcKimlik == null || tcKimlik.isEmpty()) return tcKimlik;
        return encryptor.encrypt(tcKimlik);
    }

    /**
     * Decrypt TC Kimlik No
     */
    public String decryptTCKimlik(String encryptedTCKimlik) {
        if (encryptedTCKimlik == null || encryptedTCKimlik.isEmpty()) return encryptedTCKimlik;
        return encryptor.decrypt(encryptedTCKimlik);
    }

    /**
     * Encrypt Passport Number
     */
    public String encryptPassport(String passport) {
        if (passport == null || passport.isEmpty()) return passport;
        return encryptor.encrypt(passport);
    }

    /**
     * Decrypt Passport Number
     */
    public String decryptPassport(String encryptedPassport) {
        if (encryptedPassport == null || encryptedPassport.isEmpty()) return encryptedPassport;
        return encryptor.decrypt(encryptedPassport);
    }

    /**
     * Encrypt Medical Condition/Disease info
     */
    public String encryptMedicalCondition(String condition) {
        if (condition == null || condition.isEmpty()) return condition;
        return encryptor.encrypt(condition);
    }

    /**
     * Decrypt Medical Condition/Disease info
     */
    public String decryptMedicalCondition(String encryptedCondition) {
        if (encryptedCondition == null || encryptedCondition.isEmpty()) return encryptedCondition;
        return encryptor.decrypt(encryptedCondition);
    }

    /**
     * Generic encrypt method
     */
    public String encrypt(String data) {
        if (data == null || data.isEmpty()) return data;
        return encryptor.encrypt(data);
    }

    /**
     * Generic decrypt method
     */
    public String decrypt(String encryptedData) {
        if (encryptedData == null || encryptedData.isEmpty()) return encryptedData;
        return encryptor.decrypt(encryptedData);
    }

    /**
     * Mask TC Kimlik for display (show only last 4 digits)
     */
    public String maskTCKimlik(String tcKimlik) {
        if (tcKimlik == null || tcKimlik.length() < 4) return "***";
        return "*******" + tcKimlik.substring(tcKimlik.length() - 4);
    }

    /**
     * Mask Passport for display
     */
    public String maskPassport(String passport) {
        if (passport == null || passport.length() < 4) return "***";
        return "***" + passport.substring(passport.length() - 4);
    }
}





