package com.healthtourism.eventsourcing.config;

import org.axonframework.config.EventProcessingConfigurer;
import org.axonframework.eventhandling.EventBus;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.axonframework.eventsourcing.eventstore.jpa.JpaEventStorageEngine;
import org.axonframework.serialization.Serializer;
import org.axonframework.serialization.json.JacksonSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

/**
 * Axon Framework Configuration
 * Event Sourcing & CQRS setup
 */
@Configuration
public class AxonConfig {
    
    @Autowired
    private DataSource dataSource;
    
    @Bean
    @Primary
    public Serializer serializer() {
        return JacksonSerializer.defaultSerializer();
    }
    
    @Bean
    public JpaEventStorageEngine eventStorageEngine(Serializer serializer) {
        return JpaEventStorageEngine.builder()
                .dataSource(dataSource)
                .serializer(serializer)
                .build();
    }
    
    @Autowired
    public void configure(EventProcessingConfigurer config) {
        // Configure event processing groups
        config.usingSubscribingEventProcessors();
        
        // Async processing for read models
        config.byDefaultAssignTo("async-processing-group");
    }
}



