package com.healthtourism.whatsappservice.service;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.*;

@Service
public class WhatsAppService {
    
    @Value("${twilio.whatsapp.from}")
    private String whatsappFrom;
    
    @Value("${twilio.account.sid}")
    private String accountSid;
    
    @Value("${twilio.auth.token}")
    private String authToken;
    
    @PostConstruct
    public void init() {
        Twilio.init(accountSid, authToken);
    }
    
    public Map<String, Object> sendMessage(String to, String message) {
        try {
            Message.creator(
                new PhoneNumber("whatsapp:" + to),
                new PhoneNumber("whatsapp:" + whatsappFrom),
                message
            ).create();
            return Map.of("success", true, "message", "WhatsApp message sent");
        } catch (Exception e) {
            throw new RuntimeException("WhatsApp gönderme hatası: " + e.getMessage(), e);
        }
    }
    
    public Map<String, Object> sendAppointmentReminder(String phoneNumber, Map<String, String> appointment) {
        String message = String.format(
            "🏥 Health Tourism Randevu Hatırlatıcı\n\n" +
            "Hastane: %s\n" +
            "Doktor: %s\n" +
            "Tarih: %s\n" +
            "Saat: %s\n\n" +
            "Lütfen zamanında geliniz.",
            appointment.get("hospital"),
            appointment.get("doctor"),
            appointment.get("date"),
            appointment.get("time")
        );
        return sendMessage(phoneNumber, message);
    }
    
    public Map<String, Object> sendWelcomeMessage(String phoneNumber, String name) {
        String message = String.format(
            "👋 Merhaba %s!\n\n" +
            "Health Tourism'a hoş geldiniz! Size nasıl yardımcı olabiliriz?",
            name
        );
        return sendMessage(phoneNumber, message);
    }
}

