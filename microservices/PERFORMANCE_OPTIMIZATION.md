# Performance Optimization Guide

## Caching Strategy

### Redis Cache Configuration

```java
@Configuration
@EnableCaching
public class CacheConfig {
    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory factory) {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofMinutes(10));
        return RedisCacheManager.builder(factory)
            .cacheDefaults(config)
            .build();
    }
}
```

### Cache Usage

```java
@Cacheable(value = "hospitals", key = "#id")
public Hospital getHospital(Long id) {
    return repository.findById(id).orElse(null);
}
```

## Database Optimization

1. **Indexes**: Önemli alanlara index ekle
2. **Connection Pool**: HikariCP kullan
3. **Query Optimization**: N+1 problem'lerini çöz

## API Gateway Optimization

1. **Rate Limiting**: Zaten var
2. **Caching**: Response caching ekle
3. **Compression**: Gzip compression

## Frontend Optimization

1. **Code Splitting**: Lazy loading zaten var
2. **Image Optimization**: WebP format
3. **Bundle Size**: Tree shaking

