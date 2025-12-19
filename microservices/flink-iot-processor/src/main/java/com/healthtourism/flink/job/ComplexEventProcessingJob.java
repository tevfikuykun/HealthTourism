package com.healthtourism.flink.job;

import com.healthtourism.flink.model.IoTEvent;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.cep.CEP;
import org.apache.flink.cep.PatternStream;
import org.apache.flink.cep.pattern.Pattern;
import org.apache.flink.cep.pattern.conditions.SimpleCondition;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * Complex Event Processing Job
 * Multiple CEP patterns for different scenarios
 */
@Component
public class ComplexEventProcessingJob {
    
    /**
     * Pattern 1: High temperature + High heart rate = Fever alert
     */
    public Pattern<IoTEvent, ?> feverPattern() {
        return Pattern.<IoTEvent>begin("highTemp")
            .where(new SimpleCondition<IoTEvent>() {
                @Override
                public boolean filter(IoTEvent event) {
                    return event.getTemperature() != null && event.getTemperature() > 38.5;
                }
            })
            .next("highHeartRate")
            .where(new SimpleCondition<IoTEvent>() {
                @Override
                public boolean filter(IoTEvent event) {
                    return event.getHeartRate() != null && event.getHeartRate() > 90;
                }
            })
            .within(org.apache.flink.streaming.api.windowing.time.Time.minutes(5));
    }
    
    /**
     * Pattern 2: Rapid heart rate increase = Stress alert
     */
    public Pattern<IoTEvent, ?> stressPattern() {
        return Pattern.<IoTEvent>begin("normal")
            .where(new SimpleCondition<IoTEvent>() {
                @Override
                public boolean filter(IoTEvent event) {
                    return event.getHeartRate() != null && event.getHeartRate() < 80;
                }
            })
            .next("rapidIncrease")
            .where(new SimpleCondition<IoTEvent>() {
                @Override
                public boolean filter(IoTEvent event) {
                    return event.getHeartRate() != null && event.getHeartRate() > 120;
                }
            })
            .within(org.apache.flink.streaming.api.windowing.time.Time.minutes(1));
    }
    
    /**
     * Pattern 3: Low blood pressure + High heart rate = Shock alert
     */
    public Pattern<IoTEvent, ?> shockPattern() {
        return Pattern.<IoTEvent>begin("lowBP")
            .where(new SimpleCondition<IoTEvent>() {
                @Override
                public boolean filter(IoTEvent event) {
                    return event.getBloodPressureSystolic() != null && 
                           event.getBloodPressureSystolic() < 90;
                }
            })
            .next("highHR")
            .where(new SimpleCondition<IoTEvent>() {
                @Override
                public boolean filter(IoTEvent event) {
                    return event.getHeartRate() != null && event.getHeartRate() > 100;
                }
            })
            .within(org.apache.flink.streaming.api.windowing.time.Time.minutes(3));
    }
}


