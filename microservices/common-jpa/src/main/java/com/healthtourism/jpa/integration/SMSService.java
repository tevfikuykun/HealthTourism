package com.healthtourism.jpa.integration;

import java.util.Map;

/**
 * SMS Service Interface
 * 
 * Professional SMS service abstraction for sending SMS via various providers
 * (Twilio, AWS SNS, etc.)
 */
public interface SMSService {
    
    /**
     * Send SMS message
     * 
     * @param to Phone number (E.164 format, e.g., +905551234567)
     * @param message SMS message content
     */
    void sendSMS(String to, String message);
    
    /**
     * Send OTP (One-Time Password) SMS
     * 
     * @param phoneNumber Phone number
     * @param otp OTP code
     */
    void sendOTP(String phoneNumber, String otp);
    
    /**
     * Send appointment confirmation SMS
     * 
     * @param phoneNumber Phone number
     * @param appointmentDetails Appointment details (date, time, doctor, hospital)
     */
    void sendAppointmentConfirmation(String phoneNumber, Map<String, String> appointmentDetails);
    
    /**
     * Send appointment reminder SMS
     * 
     * @param phoneNumber Phone number
     * @param appointmentDetails Appointment details
     * @param reminderHours Hours before appointment
     */
    void sendAppointmentReminder(String phoneNumber, Map<String, String> appointmentDetails, int reminderHours);
    
    /**
     * Send notification SMS
     * 
     * @param phoneNumber Phone number
     * @param notification Notification message
     */
    void sendNotification(String phoneNumber, String notification);
    
    /**
     * Send bulk SMS (for multiple recipients)
     * 
     * @param phoneNumbers Array of phone numbers
     * @param message SMS message content
     */
    void sendBulkSMS(String[] phoneNumbers, String message);
}

