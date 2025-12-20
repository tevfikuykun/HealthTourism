package com.healthtourism.iotmonitoringservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * MQTT Message Handler
 * Processes messages from IoT devices via MQTT protocol
 */
@Service
public class MqttMessageHandler {

    @Autowired
    private IoTMonitoringService ioTMonitoringService;

    @Autowired
    private ObjectMapper objectMapper;

    @ServiceActivator(inputChannel = "mqttInputChannel")
    public void handleMqttMessage(Message<?> message) {
        try {
            String topic = (String) message.getHeaders().get("mqtt_receivedTopic");
            String payload = message.getPayload().toString();
            
            // Parse MQTT topic to extract device/user info
            // Format: iot/patient/{userId}/data or iot/device/{deviceId}/status
            String[] topicParts = topic.split("/");
            
            if (topicParts.length >= 4 && "patient".equals(topicParts[1]) && "data".equals(topicParts[3])) {
                // Patient monitoring data
                Long userId = Long.parseLong(topicParts[2]);
                Map<String, Object> data = objectMapper.readValue(payload, Map.class);
                
                // Process monitoring data
                processPatientData(userId, data);
            } else if (topicParts.length >= 4 && "device".equals(topicParts[1]) && "status".equals(topicParts[3])) {
                // Device status update
                String deviceId = topicParts[2];
                Map<String, Object> status = objectMapper.readValue(payload, Map.class);
                
                // Process device status
                processDeviceStatus(deviceId, status);
            }
        } catch (Exception e) {
            System.err.println("Error processing MQTT message: " + e.getMessage());
        }
    }

    private void processPatientData(Long userId, Map<String, Object> data) {
        try {
            Long reservationId = data.containsKey("reservationId") ? 
                Long.valueOf(data.get("reservationId").toString()) : null;
            Long doctorId = data.containsKey("doctorId") ? 
                Long.valueOf(data.get("doctorId").toString()) : null;
            String deviceType = data.getOrDefault("deviceType", "UNKNOWN").toString();
            String deviceId = data.getOrDefault("deviceId", "").toString();
            
            @SuppressWarnings("unchecked")
            Map<String, Object> monitoringData = (Map<String, Object>) data.get("data");
            
            if (reservationId != null && doctorId != null) {
                ioTMonitoringService.recordMonitoringData(
                    userId, reservationId, doctorId, deviceType, deviceId, monitoringData);
            }
        } catch (Exception e) {
            System.err.println("Error processing patient data: " + e.getMessage());
        }
    }

    private void processDeviceStatus(String deviceId, Map<String, Object> status) {
        // Handle device status updates (online/offline, battery level, etc.)
        System.out.println("Device " + deviceId + " status: " + status);
    }
}





