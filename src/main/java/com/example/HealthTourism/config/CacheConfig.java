package com.example.HealthTourism.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Cache Configuration
 * Redis distributed cache yapılandırması
 * 
 * Özellikler:
 * - Redis Cache: Distributed caching (multiple instances arasında paylaşımlı)
 * - Cache TTL: Cache-specific time-to-live
 * - JSON Serialization: Complex objects için JSON serializer
 * - Null value caching: Disabled (null values cache'lenmez)
 * 
 * Avantajlar:
 * - Distributed: Birden fazla instance cache'i paylaşır
 * - Persistent: Redis restart olsa bile cache korunabilir (persistence enabled ise)
 * - High Performance: Redis'in yüksek performansı
 * - Scalability: Redis cluster ile ölçeklenebilir
 * 
 * Cache Name'leri ve TTL:
 * - "hospitals": 5 dakika
 * - "doctors": 5 dakika
 * - "packages": 10 dakika
 * - "specializations": 30 dakika
 * - "accommodations": 5 dakika
 * - "flights": 5 dakika
 * - "transfers": 5 dakika
 * - "carRentals": 5 dakika
 */
@Configuration
@EnableCaching
public class CacheConfig {
    
    @Value("${spring.cache.redis.time-to-live:300}") // Default: 5 minutes
    private long defaultTtlSeconds;
    
    /**
     * Redis Cache Manager Bean
     * Primary cache manager - Redis kullanır
     * 
     * Eğer Redis kullanılamıyorsa, Caffeine fallback kullanılabilir
     * (fallback için ConditionalOnMissingBean ile CaffeineCacheManager eklenebilir)
     */
    @Bean
    @Primary
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        // ObjectMapper for JSON serialization
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.findAndRegisterModules();
        GenericJackson2JsonRedisSerializer jsonSerializer = new GenericJackson2JsonRedisSerializer(objectMapper);
        
        // Default cache configuration
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofSeconds(defaultTtlSeconds))
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(jsonSerializer))
                .disableCachingNullValues(); // Null values cache'lenmez
        
        // Cache-specific configurations
        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
        
        // Hospitals cache: 5 minutes
        cacheConfigurations.put("hospitals", defaultConfig.entryTtl(Duration.ofMinutes(5)));
        
        // Doctors cache: 5 minutes
        cacheConfigurations.put("doctors", defaultConfig.entryTtl(Duration.ofMinutes(5)));
        
        // Packages cache: 10 minutes (daha uzun süre çünkü daha az değişir)
        cacheConfigurations.put("packages", defaultConfig.entryTtl(Duration.ofMinutes(10)));
        
        // Specializations cache: 30 minutes (çok nadiren değişir)
        cacheConfigurations.put("specializations", defaultConfig.entryTtl(Duration.ofMinutes(30)));
        
        // Accommodations cache: 5 minutes
        cacheConfigurations.put("accommodations", defaultConfig.entryTtl(Duration.ofMinutes(5)));
        
        // Flights cache: 5 minutes
        cacheConfigurations.put("flights", defaultConfig.entryTtl(Duration.ofMinutes(5)));
        
        // Transfers cache: 5 minutes
        cacheConfigurations.put("transfers", defaultConfig.entryTtl(Duration.ofMinutes(5)));
        
        // Car rentals cache: 5 minutes
        cacheConfigurations.put("carRentals", defaultConfig.entryTtl(Duration.ofMinutes(5)));
        
        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(cacheConfigurations)
                .build();
    }
}

