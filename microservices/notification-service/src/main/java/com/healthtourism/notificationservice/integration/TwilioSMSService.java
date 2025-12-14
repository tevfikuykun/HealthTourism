package com.healthtourism.notificationservice.integration;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

@Service
public class TwilioSMSService {
    
    @Value("${twilio.account.sid}")
    private String accountSid;
    
    @Value("${twilio.auth.token}")
    private String authToken;
    
    @Value("${twilio.phone.number}")
    private String phoneNumber;
    
    @PostConstruct
    public void init() {
        Twilio.init(accountSid, authToken);
    }
    
    public void sendSMS(String to, String message) {
        try {
            Message.creator(
                new PhoneNumber(to),
                new PhoneNumber(phoneNumber),
                message
            ).create();
        } catch (Exception e) {
            throw new RuntimeException("SMS gönderme hatası: " + e.getMessage(), e);
        }
    }
    
    public void sendOTP(String phoneNumber, String otp) {
        String message = "Health Tourism OTP kodunuz: " + otp + ". Bu kodu kimseyle paylaşmayın.";
        sendSMS(phoneNumber, message);
    }
    
    public void sendAppointmentReminder(String phoneNumber, String hospitalName, String date) {
        String message = "Health Tourism: " + hospitalName + " için randevunuz " + date + " tarihinde. Lütfen zamanında geliniz.";
        sendSMS(phoneNumber, message);
    }
}

