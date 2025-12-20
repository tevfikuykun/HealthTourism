package com.healthtourism.flink.job;

import com.healthtourism.flink.model.IoTEvent;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.cep.CEP;
import org.apache.flink.cep.PatternStream;
import org.apache.flink.cep.pattern.Pattern;
import org.apache.flink.cep.pattern.conditions.SimpleCondition;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.Map;

/**
 * Emergency Detection Job using Flink CEP
 * Complex Event Processing: "Eğer hastanın nabzı 2 dakika boyunca 100'ün üzerindeyse 
 * ve lokasyonu hastane dışındaysa acil durum başlat"
 */
@Component
public class EmergencyDetectionJob {
    
    private StreamExecutionEnvironment env;
    
    public void start() throws Exception {
        env = StreamExecutionEnvironment.getExecutionEnvironment();
        
        // Kafka Source
        KafkaSource<String> source = KafkaSource.<String>builder()
            .setBootstrapServers("localhost:9092")
            .setTopics("iot-monitoring-data")
            .setGroupId("flink-emergency-detection")
            .setStartingOffsets(OffsetsInitializer.latest())
            .build();
        
        // Read from Kafka
        DataStream<String> kafkaStream = env.fromSource(
            source,
            WatermarkStrategy.forBoundedOutOfOrderness(Duration.ofSeconds(20)),
            "Kafka Source"
        );
        
        // Parse JSON to IoTEvent
        DataStream<IoTEvent> events = kafkaStream.map(new MapFunction<String, IoTEvent>() {
            @Override
            public IoTEvent map(String value) throws Exception {
                // Parse JSON (using Jackson or Gson)
                return parseIoTEvent(value);
            }
        });
        
        // Define CEP Pattern
        // Pattern: Heart rate > 100 for 2 minutes AND location outside hospital
        Pattern<IoTEvent, ?> emergencyPattern = Pattern.<IoTEvent>begin("highHeartRate")
            .where(new SimpleCondition<IoTEvent>() {
                @Override
                public boolean filter(IoTEvent event) {
                    return event.getHeartRate() != null && event.getHeartRate() > 100;
                }
            })
            .timesOrMore(10) // At least 10 events (assuming 1 event per 12 seconds = 2 minutes)
            .within(org.apache.flink.streaming.api.windowing.time.Time.minutes(2))
            .next("outsideHospital")
            .where(new SimpleCondition<IoTEvent>() {
                @Override
                public boolean filter(IoTEvent event) {
                    // Check if location is outside hospital
                    return isOutsideHospital(event.getLatitude(), event.getLongitude());
                }
            });
        
        // Apply CEP Pattern
        PatternStream<IoTEvent> patternStream = CEP.pattern(
            events.keyBy(IoTEvent::getUserId),
            emergencyPattern
        );
        
        // Process matches
        DataStream<EmergencyAlert> alerts = patternStream.select(
            (Map<String, List<IoTEvent>> pattern) -> {
                List<IoTEvent> highHeartRateEvents = pattern.get("highHeartRate");
                IoTEvent lastEvent = highHeartRateEvents.get(highHeartRateEvents.size() - 1);
                
                EmergencyAlert alert = new EmergencyAlert();
                alert.setUserId(lastEvent.getUserId());
                alert.setTimestamp(java.time.LocalDateTime.now());
                alert.setAlertType("HIGH_HEART_RATE_OUTSIDE_HOSPITAL");
                alert.setMessage("Patient has high heart rate (>100) for 2 minutes outside hospital");
                alert.setLocation(lastEvent.getLocation());
                alert.setLatitude(lastEvent.getLatitude());
                alert.setLongitude(lastEvent.getLongitude());
                
                return alert;
            }
        );
        
        // Send alerts to Kafka
        alerts.addSink(new SinkFunction<EmergencyAlert>() {
            @Override
            public void invoke(EmergencyAlert value, Context context) throws Exception {
                // Send to emergency alerts topic
                sendToKafka("emergency-alerts", value);
            }
        });
        
        // Execute
        env.execute("Emergency Detection Job");
    }
    
    private IoTEvent parseIoTEvent(String json) {
        // Parse JSON to IoTEvent
        // In production, use Jackson ObjectMapper
        return new IoTEvent(); // Placeholder
    }
    
    private boolean isOutsideHospital(Double latitude, Double longitude) {
        // Check if coordinates are outside hospital boundaries
        // In production, use geospatial calculations
        return true; // Placeholder
    }
    
    private void sendToKafka(String topic, EmergencyAlert alert) {
        // Send alert to Kafka
        // In production, use Flink Kafka Producer
    }
    
    // Emergency Alert Model
    @lombok.Data
    public static class EmergencyAlert {
        private Long userId;
        private java.time.LocalDateTime timestamp;
        private String alertType;
        private String message;
        private String location;
        private Double latitude;
        private Double longitude;
    }
}



