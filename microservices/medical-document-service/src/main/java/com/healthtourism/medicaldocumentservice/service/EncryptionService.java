package com.healthtourism.medicaldocumentservice.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Encryption Service for GDPR/KVKK compliant file storage
 * Encrypts medical documents before storage
 */
@Service
public class EncryptionService {

    @Value("${encryption.secret.key:healthtourism-secret-key-32bytes!!}")
    private String secretKeyString;

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES";

    /**
     * Encrypt file content
     */
    public byte[] encrypt(byte[] data) throws Exception {
        SecretKeySpec secretKey = new SecretKeySpec(secretKeyString.getBytes(), ALGORITHM);
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        return cipher.doFinal(data);
    }

    /**
     * Decrypt file content
     */
    public byte[] decrypt(byte[] encryptedData) throws Exception {
        SecretKeySpec secretKey = new SecretKeySpec(secretKeyString.getBytes(), ALGORITHM);
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        return cipher.doFinal(encryptedData);
    }

    /**
     * Encrypt file and save
     */
    public void encryptFile(Path inputFile, Path outputFile) throws Exception {
        byte[] fileContent = Files.readAllBytes(inputFile);
        byte[] encryptedContent = encrypt(fileContent);
        Files.write(outputFile, encryptedContent);
    }

    /**
     * Decrypt file and read
     */
    public byte[] decryptFile(Path encryptedFile) throws Exception {
        byte[] encryptedContent = Files.readAllBytes(encryptedFile);
        return decrypt(encryptedContent);
    }
}
