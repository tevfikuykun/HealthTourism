package com.healthtourism.iotmonitoringservice.controller;

import com.healthtourism.iotmonitoringservice.entity.PatientMonitoringData;
import com.healthtourism.iotmonitoringservice.service.IoTMonitoringService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/iot-monitoring")
@CrossOrigin(origins = "*")
@Tag(name = "IoT Monitoring", description = "Post-op remote patient monitoring via wearable devices")
public class IoTMonitoringController {
    
    @Autowired
    private IoTMonitoringService ioTMonitoringService;
    
    @PostMapping("/data")
    @Operation(summary = "Receive monitoring data from IoT device",
               description = "Accepts data from Apple Watch, Fitbit, Samsung Health, etc.")
    public ResponseEntity<PatientMonitoringData> recordData(
            @RequestBody Map<String, Object> request) {
        try {
            Long userId = Long.valueOf(request.get("userId").toString());
            Long reservationId = Long.valueOf(request.get("reservationId").toString());
            Long doctorId = Long.valueOf(request.get("doctorId").toString());
            String deviceType = request.get("deviceType").toString();
            String deviceId = request.getOrDefault("deviceId", "").toString();
            
            @SuppressWarnings("unchecked")
            Map<String, Object> data = (Map<String, Object>) request.get("data");
            
            PatientMonitoringData monitoringData = ioTMonitoringService.recordMonitoringData(
                    userId, reservationId, doctorId, deviceType, deviceId, data);
            
            return ResponseEntity.ok(monitoringData);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/user/{userId}")
    @Operation(summary = "Get all monitoring data for a user")
    public ResponseEntity<List<PatientMonitoringData>> getDataByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(ioTMonitoringService.getMonitoringDataByUser(userId));
    }
    
    @GetMapping("/user/{userId}/recent")
    @Operation(summary = "Get recent monitoring data (last N hours)")
    public ResponseEntity<List<PatientMonitoringData>> getRecentDataByUser(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "24") int hours) {
        return ResponseEntity.ok(ioTMonitoringService.getRecentDataByUser(userId, hours));
    }
    
    @GetMapping("/user/{userId}/latest")
    @Operation(summary = "Get latest monitoring data for a user")
    public ResponseEntity<PatientMonitoringData> getLatestDataByUser(@PathVariable Long userId) {
        PatientMonitoringData data = ioTMonitoringService.getLatestDataByUser(userId);
        return data != null ? ResponseEntity.ok(data) : ResponseEntity.notFound().build();
    }
    
    @GetMapping("/reservation/{reservationId}")
    @Operation(summary = "Get monitoring data for a reservation")
    public ResponseEntity<List<PatientMonitoringData>> getDataByReservation(
            @PathVariable Long reservationId) {
        return ResponseEntity.ok(ioTMonitoringService.getMonitoringDataByReservation(reservationId));
    }
    
    @GetMapping("/doctor/{doctorId}")
    @Operation(summary = "Get monitoring data for a doctor's patients")
    public ResponseEntity<List<PatientMonitoringData>> getDataByDoctor(@PathVariable Long doctorId) {
        return ResponseEntity.ok(ioTMonitoringService.getMonitoringDataByDoctor(doctorId));
    }
    
    @GetMapping("/alerts/{alertStatus}")
    @Operation(summary = "Get alerts by status", 
               description = "Alert statuses: NORMAL, WARNING, CRITICAL")
    public ResponseEntity<List<PatientMonitoringData>> getAlerts(@PathVariable String alertStatus) {
        return ResponseEntity.ok(ioTMonitoringService.getAlerts(alertStatus));
    }
}
