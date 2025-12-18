package com.healthtourism.notificationservice.service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

/**
 * SMS Service using Twilio
 */
@Service
public class SMSService {

    @Value("${twilio.account.sid}")
    private String accountSid;

    @Value("${twilio.auth.token}")
    private String authToken;

    @Value("${twilio.phone.number}")
    private String fromPhoneNumber;

    @PostConstruct
    public void init() {
        if (!accountSid.equals("your_twilio_account_sid") && !authToken.equals("your_twilio_auth_token")) {
            Twilio.init(accountSid, authToken);
        }
    }

    /**
     * Send SMS
     */
    public void sendSMS(String to, String message) {
        if (accountSid.equals("your_twilio_account_sid") || authToken.equals("your_twilio_auth_token")) {
            System.out.println("SMS (Simulated): To: " + to + ", Message: " + message);
            return;
        }

        try {
            Message.creator(
                new PhoneNumber(to),
                new PhoneNumber(fromPhoneNumber),
                message
            ).create();
        } catch (Exception e) {
            throw new RuntimeException("Failed to send SMS: " + e.getMessage(), e);
        }
    }

    /**
     * Send OTP
     */
    public void sendOTP(String phoneNumber, String otp) {
        String message = "Health Tourism - Doğrulama kodunuz: " + otp + ". Bu kodu kimseyle paylaşmayın.";
        sendSMS(phoneNumber, message);
    }

    /**
     * Send notification SMS
     */
    public void sendNotificationSMS(String phoneNumber, String notification) {
        String message = "Health Tourism: " + notification;
        sendSMS(phoneNumber, message);
    }
}
