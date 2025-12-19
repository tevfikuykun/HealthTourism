package com.healthtourism.camel.config;

import org.apache.camel.component.hl7.HL7MLLPCodec;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Camel Configuration
 * Setup for HL7, codecs, and other components
 */
@Configuration
public class CamelConfig {
    
    @Bean
    public HL7MLLPCodec hl7decoder() {
        return new HL7MLLPCodec();
    }
    
    @Bean
    public HL7MLLPCodec hl7encoder() {
        return new HL7MLLPCodec();
    }
}

