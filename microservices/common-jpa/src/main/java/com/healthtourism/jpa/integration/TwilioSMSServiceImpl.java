package com.healthtourism.jpa.integration;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

import java.util.Map;

/**
 * Twilio SMS Service Implementation
 * 
 * Professional SMS service using Twilio API for sending SMS notifications,
 * OTP codes, appointment reminders, etc.
 */
@Service
public class TwilioSMSServiceImpl implements SMSService {
    
    private static final Logger logger = LoggerFactory.getLogger(TwilioSMSServiceImpl.class);
    
    @Value("${twilio.account.sid:}")
    private String accountSid;
    
    @Value("${twilio.auth.token:}")
    private String authToken;
    
    @Value("${twilio.phone.number:}")
    private String fromPhoneNumber;
    
    @Value("${twilio.enabled:true}")
    private boolean enabled;
    
    @PostConstruct
    public void init() {
        if (enabled && accountSid != null && !accountSid.isEmpty() && 
            !accountSid.equals("your_twilio_account_sid") &&
            authToken != null && !authToken.isEmpty() &&
            !authToken.equals("your_twilio_auth_token")) {
            Twilio.init(accountSid, authToken);
            logger.info("Twilio SMS service initialized with phone number: {}", fromPhoneNumber);
        } else {
            logger.warn("Twilio SMS service disabled or not configured");
        }
    }
    
    @Override
    public void sendSMS(String to, String message) {
        if (!enabled) {
            logger.warn("Twilio not configured, SMS not sent to: {}", to);
            logger.info("SMS (Simulated): To: {}, Message: {}", to, message);
            return;
        }
        
        try {
            Message.creator(
                new PhoneNumber(to),
                new PhoneNumber(fromPhoneNumber),
                message
            ).create();
            
            logger.info("SMS sent successfully to: {}", to);
        } catch (Exception e) {
            logger.error("Failed to send SMS to {}: {}", to, e.getMessage(), e);
            throw new RuntimeException("SMS gönderme hatası: " + e.getMessage(), e);
        }
    }
    
    @Override
    public void sendOTP(String phoneNumber, String otp) {
        String message = String.format(
            "Health Tourism - Doğrulama kodunuz: %s. Bu kodu kimseyle paylaşmayın. Kod 5 dakika geçerlidir.",
            otp
        );
        sendSMS(phoneNumber, message);
    }
    
    @Override
    public void sendAppointmentConfirmation(String phoneNumber, Map<String, String> appointmentDetails) {
        String date = appointmentDetails.getOrDefault("date", "N/A");
        String time = appointmentDetails.getOrDefault("time", "N/A");
        String doctor = appointmentDetails.getOrDefault("doctor", "N/A");
        String hospital = appointmentDetails.getOrDefault("hospital", "N/A");
        
        String message = String.format(
            "Health Tourism - Randevu Onayı: %s tarihinde, %s saatinde %s doktoru ile %s hastanesinde randevunuz oluşturulmuştur.",
            date, time, doctor, hospital
        );
        sendSMS(phoneNumber, message);
    }
    
    @Override
    public void sendAppointmentReminder(String phoneNumber, Map<String, String> appointmentDetails, int reminderHours) {
        String date = appointmentDetails.getOrDefault("date", "N/A");
        String time = appointmentDetails.getOrDefault("time", "N/A");
        String doctor = appointmentDetails.getOrDefault("doctor", "N/A");
        String hospital = appointmentDetails.getOrDefault("hospital", "N/A");
        
        String message = String.format(
            "Health Tourism - Randevu Hatırlatıcı: %d saat sonra %s tarihinde, %s saatinde %s doktoru ile %s hastanesinde randevunuz var.",
            reminderHours, date, time, doctor, hospital
        );
        sendSMS(phoneNumber, message);
    }
    
    @Override
    public void sendNotification(String phoneNumber, String notification) {
        String message = "Health Tourism: " + notification;
        sendSMS(phoneNumber, message);
    }
    
    @Override
    public void sendBulkSMS(String[] phoneNumbers, String message) {
        for (String phoneNumber : phoneNumbers) {
            try {
                sendSMS(phoneNumber, message);
            } catch (Exception e) {
                logger.error("Failed to send bulk SMS to {}: {}", phoneNumber, e.getMessage());
                // Continue with next number instead of failing entire bulk operation
            }
        }
    }
}

