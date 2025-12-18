package com.healthtourism.medicaldocumentservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class EncryptionServiceTest {

    @InjectMocks
    private EncryptionService encryptionService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(encryptionService, "secretKeyString", "healthtourism-secret-key-32bytes!!");
    }

    @Test
    void testEncryptDecrypt() throws Exception {
        // Given
        byte[] originalData = "Test medical document content".getBytes();

        // When
        byte[] encrypted = encryptionService.encrypt(originalData);
        byte[] decrypted = encryptionService.decrypt(encrypted);

        // Then
        assertNotNull(encrypted);
        assertNotEquals(originalData, encrypted); // Encrypted should be different
        assertArrayEquals(originalData, decrypted); // Decrypted should match original
    }

    @Test
    void testEncryptDecrypt_EmptyData() throws Exception {
        // Given
        byte[] emptyData = new byte[0];

        // When
        byte[] encrypted = encryptionService.encrypt(emptyData);
        byte[] decrypted = encryptionService.decrypt(encrypted);

        // Then
        assertNotNull(encrypted);
        assertArrayEquals(emptyData, decrypted);
    }
}
