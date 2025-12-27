package com.example.HealthTourism.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Redis Configuration
 * Redis connection ve serialization yapılandırması
 * 
 * Özellikler:
 * - RedisTemplate: Key-value operations için
 * - JSON Serialization: Complex objects için JSON serializer
 * - String Serialization: Keys için string serializer
 * - JavaTimeModule: LocalDateTime, LocalDate gibi Java 8 time types desteği
 * 
 * Kullanım:
 * Redis cache ve distributed cache için kullanılır
 */
@Configuration
public class RedisConfig {
    
    /**
     * Redis Template Bean
     * Redis operations için template
     * 
     * Serialization:
     * - Keys: String
     * - Values: JSON (Jackson)
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        
        // Key serializer: String
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        
        // Value serializer: JSON (Jackson)
        ObjectMapper objectMapper = createObjectMapper();
        Jackson2JsonRedisSerializer<Object> jsonSerializer = new Jackson2JsonRedisSerializer<>(objectMapper, Object.class);
        template.setValueSerializer(jsonSerializer);
        template.setHashValueSerializer(jsonSerializer);
        
        template.afterPropertiesSet();
        return template;
    }
    
    /**
     * Object Mapper Bean
     * JSON serialization için ObjectMapper
     * Java 8 time types desteği ile
     */
    @Bean
    public ObjectMapper objectMapper() {
        return createObjectMapper();
    }
    
    /**
     * Object Mapper Oluştur
     * JavaTimeModule ile Java 8 time types desteği
     */
    private ObjectMapper createObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.findAndRegisterModules();
        return mapper;
    }
}

