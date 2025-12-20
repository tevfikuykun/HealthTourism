package com.healthtourism.security.encryption;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * JPA Attribute Converter for automatic field encryption/decryption
 * Use with @Convert annotation on sensitive entity fields
 */
@Converter
@Component
public class EncryptedFieldConverter implements AttributeConverter<String, String> {

    private static AES256GCMEncryptor encryptor;

    @Value("${encryption.aes.key:}")
    public void setEncryptionKey(String key) {
        if (key != null && !key.isEmpty()) {
            encryptor = new AES256GCMEncryptor(key);
        }
    }

    @Override
    public String convertToDatabaseColumn(String attribute) {
        if (attribute == null || attribute.isEmpty()) {
            return attribute;
        }
        if (encryptor == null) {
            return attribute; // Encryption not configured
        }
        return encryptor.encrypt(attribute);
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return dbData;
        }
        if (encryptor == null) {
            return dbData; // Encryption not configured
        }
        try {
            return encryptor.decrypt(dbData);
        } catch (Exception e) {
            // Data might not be encrypted (legacy data)
            return dbData;
        }
    }
}





