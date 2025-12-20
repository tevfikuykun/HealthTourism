package com.healthtourism.jpa.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Cache Configuration
 * Redis-based L2 cache for Hibernate
 */
@Configuration
@EnableCaching
public class CacheConfig {
    
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofHours(1))
            .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
            .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()))
            .disableCachingNullValues();
        
        // Cache region configurations
        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
        
        // Patient cache - 2 hours TTL
        cacheConfigurations.put("patientCache", defaultConfig.entryTtl(Duration.ofHours(2)));
        
        // Hospital cache - 4 hours TTL
        cacheConfigurations.put("hospitalCache", defaultConfig.entryTtl(Duration.ofHours(4)));
        
        // Doctor cache - 2 hours TTL
        cacheConfigurations.put("doctorCache", defaultConfig.entryTtl(Duration.ofHours(2)));
        
        // Reservation cache - 1 hour TTL
        cacheConfigurations.put("reservationCache", defaultConfig.entryTtl(Duration.ofHours(1)));
        
        // Medical record cache - 1 hour TTL
        cacheConfigurations.put("medicalRecordCache", defaultConfig.entryTtl(Duration.ofHours(1)));
        
        return RedisCacheManager.builder(connectionFactory)
            .cacheDefaults(defaultConfig)
            .withInitialCacheConfigurations(cacheConfigurations)
            .build();
    }
}



