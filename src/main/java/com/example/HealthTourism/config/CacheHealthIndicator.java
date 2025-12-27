package com.example.HealthTourism.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Cache Health Indicator
 * Redis cache sisteminin sağlıklı olup olmadığını kontrol eder
 * 
 * Endpoint: /actuator/health
 * 
 * Health Status:
 * - UP: Redis cache sistemi çalışıyor
 * - DOWN: Redis cache sistemi kullanılamıyor
 * 
 * Cache İstatistikleri:
 * - Redis connection status
 * - Cache names: Aktif cache'lerin listesi
 * - Redis info: Redis server bilgileri
 */
@Component
@RequiredArgsConstructor
public class CacheHealthIndicator implements HealthIndicator {
    
    private final CacheManager cacheManager;
    private final RedisConnectionFactory redisConnectionFactory;
    
    @Override
    public Health health() {
        try {
            // Redis connection test
            boolean isConnected = redisConnectionFactory.getConnection().ping() != null;
            
            if (!isConnected) {
                return Health.down()
                        .withDetail("cache", "Redis")
                        .withDetail("status", "Redis connection failed")
                        .withDetail("error", "Cannot ping Redis server")
                        .build();
            }
            
            // Get cache names
            var cacheNames = cacheManager.getCacheNames();
            Map<String, Object> cacheDetails = new HashMap<>();
            cacheNames.forEach(name -> {
                var cache = cacheManager.getCache(name);
                if (cache != null) {
                    cacheDetails.put(name, "Available");
                }
            });
            
            // Get Redis version (if available)
            String redisVersion = "Unknown";
            try {
                var conn = redisConnectionFactory.getConnection();
                var infoProperties = conn.info("server");
                if (infoProperties != null && infoProperties.containsKey("redis_version")) {
                    redisVersion = infoProperties.getProperty("redis_version");
                }
                conn.close();
            } catch (Exception e) {
                // Ignore info retrieval errors
            }
            
            return Health.up()
                    .withDetail("cache", "Redis")
                    .withDetail("status", "Cache is healthy")
                    .withDetail("connected", true)
                    .withDetail("cacheNames", cacheNames)
                    .withDetail("cacheCount", cacheNames.size())
                    .withDetail("redisVersion", redisVersion)
                    .withDetail("caches", cacheDetails)
                    .build();
        } catch (Exception e) {
            return Health.down()
                    .withDetail("cache", "Redis")
                    .withDetail("status", "Cache health check failed")
                    .withDetail("error", e.getMessage())
                    .withDetail("connected", false)
                    .build();
        }
    }
}

