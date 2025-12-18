package com.healthtourism.iotmonitoringservice.config;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.support.serializer.JsonSerde;

import java.time.Duration;
import java.util.Map;
import java.util.Properties;

@Configuration
@EnableKafkaStreams
public class KafkaStreamsConfig {

    @Value("${spring.kafka.bootstrap-servers:localhost:9092}")
    private String bootstrapServers;

    @Value("${kafka.streams.application-id:iot-monitoring-streams}")
    private String applicationId;

    @Bean
    public KStream<String, Map<String, Object>> iotDataStream(StreamsBuilder streamsBuilder) {
        // Input topic: raw IoT data from devices
        KStream<String, Map<String, Object>> source = streamsBuilder.stream(
            "iot-raw-data",
            Consumed.with(Serdes.String(), new JsonSerde<>(Map.class))
        );

        // Process and filter data
        KStream<String, Map<String, Object>> processed = source
            .filter((key, value) -> value != null && value.containsKey("userId"))
            .mapValues(value -> {
                // Add processing timestamp
                value.put("processedAt", System.currentTimeMillis());
                // Calculate derived metrics
                if (value.containsKey("heartRate") && value.containsKey("bloodPressure")) {
                    double heartRate = Double.parseDouble(value.get("heartRate").toString());
                    Map<String, Object> bp = (Map<String, Object>) value.get("bloodPressure");
                    double systolic = Double.parseDouble(bp.get("systolic").toString());
                    
                    // Calculate stress index (simplified)
                    double stressIndex = (heartRate / 100.0) * (systolic / 120.0);
                    value.put("stressIndex", stressIndex);
                }
                return value;
            });

        // Branch stream based on alert level
        KStream<String, Map<String, Object>>[] branches = processed.branch(
            (key, value) -> {
                // Critical alerts
                return isCritical(value);
            },
            (key, value) -> {
                // Warning alerts
                return isWarning(value);
            },
            (key, value) -> true // Normal data
        );

        // Send to different topics
        branches[0].to("iot-critical-alerts", Produced.with(Serdes.String(), new JsonSerde<>(Map.class)));
        branches[1].to("iot-warning-alerts", Produced.with(Serdes.String(), new JsonSerde<>(Map.class)));
        branches[2].to("iot-processed-data", Produced.with(Serdes.String(), new JsonSerde<>(Map.class)));

        // Aggregate data by user for time windows
        KGroupedStream<String, Map<String, Object>> grouped = processed.groupBy(
            (key, value) -> value.get("userId").toString(),
            Grouped.with(Serdes.String(), new JsonSerde<>(Map.class))
        );

        // Windowed aggregation (5-minute windows)
        TimeWindows windows = TimeWindows.ofSizeWithNoGrace(Duration.ofMinutes(5));
        grouped.windowedBy(windows)
            .aggregate(
                () -> new java.util.HashMap<String, Object>(),
                (key, value, aggregate) -> {
                    aggregate.put("userId", key);
                    aggregate.put("windowStart", System.currentTimeMillis());
                    aggregate.put("dataPoints", 
                        ((Integer) aggregate.getOrDefault("dataPoints", 0)) + 1);
                    return aggregate;
                },
                Materialized.with(Serdes.String(), new JsonSerde<>(Map.class))
            )
            .toStream()
            .to("iot-aggregated-data", Produced.with(WindowedSerdes.timeWindowedSerdeFrom(String.class), new JsonSerde<>(Map.class)));

        return processed;
    }

    private boolean isCritical(Map<String, Object> data) {
        // Check for critical conditions
        if (data.containsKey("heartRate")) {
            double hr = Double.parseDouble(data.get("heartRate").toString());
            if (hr > 150 || hr < 40) return true;
        }
        if (data.containsKey("temperature")) {
            double temp = Double.parseDouble(data.get("temperature").toString());
            if (temp > 39.5 || temp < 35.0) return true;
        }
        return false;
    }

    private boolean isWarning(Map<String, Object> data) {
        // Check for warning conditions
        if (data.containsKey("heartRate")) {
            double hr = Double.parseDouble(data.get("heartRate").toString());
            if (hr > 120 || hr < 50) return true;
        }
        if (data.containsKey("temperature")) {
            double temp = Double.parseDouble(data.get("temperature").toString());
            if (temp > 38.0 || temp < 36.0) return true;
        }
        return false;
    }

    @Bean
    public Properties kafkaStreamsProperties() {
        Properties props = new Properties();
        props.put("application.id", applicationId);
        props.put("bootstrap.servers", bootstrapServers);
        props.put("default.key.serde", Serdes.String().getClass().getName());
        props.put("default.value.serde", JsonSerde.class.getName());
        props.put("commit.interval.ms", 1000);
        return props;
    }
}

