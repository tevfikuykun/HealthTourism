package com.healthtourism.notificationservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class SMSServiceTest {

    @InjectMocks
    private SMSService smsService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(smsService, "accountSid", "test_account_sid");
        ReflectionTestUtils.setField(smsService, "authToken", "test_auth_token");
        ReflectionTestUtils.setField(smsService, "fromPhoneNumber", "+1234567890");
    }

    @Test
    void testSendSMS_SimulationMode() {
        // Given - Using default test credentials (simulation mode)
        ReflectionTestUtils.setField(smsService, "accountSid", "your_twilio_account_sid");
        ReflectionTestUtils.setField(smsService, "authToken", "your_twilio_auth_token");

        // When & Then - Should not throw exception in simulation mode
        assertDoesNotThrow(() -> {
            smsService.sendSMS("+1234567890", "Test message");
        });
    }

    @Test
    void testSendOTP() {
        // Given
        ReflectionTestUtils.setField(smsService, "accountSid", "your_twilio_account_sid");
        ReflectionTestUtils.setField(smsService, "authToken", "your_twilio_auth_token");

        // When & Then
        assertDoesNotThrow(() -> {
            smsService.sendOTP("+1234567890", "123456");
        });
    }

    @Test
    void testSendNotificationSMS() {
        // Given
        ReflectionTestUtils.setField(smsService, "accountSid", "your_twilio_account_sid");
        ReflectionTestUtils.setField(smsService, "authToken", "your_twilio_auth_token");

        // When & Then
        assertDoesNotThrow(() -> {
            smsService.sendNotificationSMS("+1234567890", "Test notification");
        });
    }
}
