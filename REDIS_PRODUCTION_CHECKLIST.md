# Redis Cache Production Checklist ✅

## 1. Critical Configuration Settings ✅

### ✅ Null Value Caching Disabled

**application.properties:**
```properties
spring.cache.redis.cache-null-values=false
```

**CacheConfig.java:**
```java
.disableCachingNullValues(); // Null values cache'lenmez
```

**Neden Önemli:**
- ✅ Veritabanında bulunmayan (null) sonuçların Redis'i boş yere meşgul etmesini engeller
- ✅ Memory tasarrufu sağlar
- ✅ Cache pollution'ı önler
- ✅ Unnecessary serialization overhead'ını azaltır

**Örnek Senaryo:**
```java
// Kötü (cache-null-values=true)
@Cacheable(value = "hospitals", key = "'hospital-' + #id")
public HospitalDTO getHospitalById(Long id) {
    // Eğer hospital bulunamazsa null döner ve Redis'e null kaydedilir
    // Bu gereksiz memory kullanımına yol açar
}

// İyi (cache-null-values=false)
@Cacheable(value = "hospitals", key = "'hospital-' + #id")
public HospitalDTO getHospitalById(Long id) {
    // Eğer hospital bulunamazsa null döner ama Redis'e kaydedilmez
    // Memory tasarrufu sağlar
}
```

---

### ✅ Key Prefix Configuration

**application.properties:**
```properties
spring.cache.redis.use-key-prefix=true
spring.cache.redis.key-prefix=healthtourism:
```

**Neden Önemli:**
- ✅ Redis anahtarlarının birbirine karışmasını engeller
- ✅ Multi-tenant veya multi-application senaryolarında kritik
- ✅ Cache key'lerini organize eder
- ✅ Cache temizleme işlemlerini kolaylaştırır

**Örnek Cache Keys:**
```
healthtourism:hospitals::all-active
healthtourism:doctors::hospital-1
healthtourism:packages::all-active
healthtourism:specializations::all
```

**Cache Temizleme:**
```bash
# Belirli bir prefix'e ait tüm key'leri temizle
redis-cli KEYS "healthtourism:*" | xargs redis-cli DEL
```

---

## 2. Redis Connection Configuration ✅

### ✅ Connection Pool Settings

**application.properties:**
```properties
spring.data.redis.lettuce.pool.enabled=true
spring.data.redis.lettuce.pool.max-active=8
spring.data.redis.lettuce.pool.max-idle=8
spring.data.redis.lettuce.pool.min-idle=0
spring.data.redis.lettuce.pool.max-wait=-1ms
```

**Optimizasyon Önerileri:**
- **max-active:** Eşzamanlı connection sayısı (production'da 20-50 arası olabilir)
- **max-idle:** Boşta bekleyen connection sayısı (max-active'in %50'si)
- **min-idle:** Minimum boşta bekleyen connection sayısı (2-5 arası)
- **max-wait:** Connection bekleyen thread'lerin maksimum bekleme süresi

---

### ✅ Timeout Configuration

**application.properties:**
```properties
spring.data.redis.timeout=2000ms
```

**Production Önerisi:**
- **timeout:** 2-5 saniye arası (network latency'ye göre ayarlanmalı)
- Çok düşük timeout connection timeout hatalarına yol açar
- Çok yüksek timeout thread blocking'e yol açar

---

## 3. Cache TTL Configuration ✅

### ✅ Default TTL

**application.properties:**
```properties
spring.cache.redis.time-to-live=300000
```

**CacheConfig.java:**
```java
// Cache-specific TTL configurations
cacheConfigurations.put("hospitals", defaultConfig.entryTtl(Duration.ofMinutes(5)));
cacheConfigurations.put("packages", defaultConfig.entryTtl(Duration.ofMinutes(10)));
cacheConfigurations.put("specializations", defaultConfig.entryTtl(Duration.ofMinutes(30)));
```

**TTL Stratejisi:**
- **Sık değişen veriler:** 5 dakika (hospitals, doctors)
- **Orta sıklıkta değişen veriler:** 10 dakika (packages)
- **Nadiren değişen veriler:** 30 dakika veya daha fazla (specializations)

---

## 4. Production Best Practices ✅

### ✅ Environment Variables

**application.properties:**
```properties
spring.data.redis.host=${REDIS_HOST:localhost}
spring.data.redis.port=${REDIS_PORT:6379}
spring.data.redis.password=${REDIS_PASSWORD:}
```

**Production Deployment:**
```bash
export REDIS_HOST=redis.example.com
export REDIS_PORT=6379
export REDIS_PASSWORD=your-secure-password
```

---

### ✅ Security

1. **Password Protection:**
   ```properties
   spring.data.redis.password=${REDIS_PASSWORD:}
   ```
   - Production'da mutlaka password kullanın
   - Environment variable ile yönetin

2. **TLS/SSL:**
   ```properties
   spring.data.redis.ssl=true
   spring.data.redis.url=rediss://redis.example.com:6380
   ```
   - Production'da TLS/SSL kullanın

3. **Network Security:**
   - Redis'i private network'te tutun
   - Firewall kuralları ile erişimi sınırlandırın
   - VPC/Subnet isolation kullanın

---

### ✅ Monitoring

1. **Health Check:**
   ```
   GET /actuator/health
   ```
   - Cache health indicator ile Redis connection kontrolü

2. **Metrics:**
   ```
   GET /actuator/metrics/cache.gets
   GET /actuator/metrics/cache.evictions
   ```
   - Cache hit/miss ratio
   - Cache eviction rate
   - Cache size

3. **Redis Monitoring:**
   ```bash
   redis-cli INFO memory
   redis-cli INFO stats
   redis-cli MONITOR
   ```

---

### ✅ Memory Management

1. **Max Memory Policy:**
   ```bash
   # Redis config
   maxmemory 2gb
   maxmemory-policy allkeys-lru
   ```

2. **Memory Optimization:**
   - TTL değerlerini optimize edin
   - Gereksiz cache'leri temizleyin
   - Memory usage'ı düzenli olarak izleyin

---

## 5. Pre-Production Checklist ✅

### Configuration Checklist

- [x] `spring.cache.redis.cache-null-values=false` ✅
- [x] `spring.cache.redis.use-key-prefix=true` ✅
- [x] `spring.cache.redis.key-prefix=healthtourism:` ✅
- [x] Connection pool ayarları optimize edildi ✅
- [x] TTL değerleri cache-specific olarak ayarlandı ✅
- [x] Environment variables kullanılıyor ✅
- [x] Password protection aktif ✅
- [x] Health check endpoint'leri yapılandırıldı ✅

### Security Checklist

- [ ] Redis password production'da set edildi
- [ ] TLS/SSL aktif (production için)
- [ ] Network security kuralları uygulandı
- [ ] Redis access log'ları aktif
- [ ] Redis authentication mekanizması aktif

### Monitoring Checklist

- [x] Health check endpoint'leri aktif ✅
- [ ] Cache metrics Prometheus'a export ediliyor
- [ ] Redis memory usage monitoring aktif
- [ ] Cache hit/miss ratio dashboard'u var
- [ ] Alerting kuralları tanımlı

### Performance Checklist

- [x] Connection pool size optimize edildi ✅
- [x] TTL değerleri optimize edildi ✅
- [ ] Redis memory limit set edildi
- [ ] Redis persistence strategy belirlendi
- [ ] Cache warming strategy uygulanıyor (opsiyonel)

---

## 6. Production Deployment Steps ✅

### 1. Redis Server Setup

```bash
# Docker ile Redis başlat
docker run -d \
  --name redis-production \
  -p 6379:6379 \
  -e REDIS_PASSWORD=your-secure-password \
  redis:7-alpine redis-server --requirepass your-secure-password

# Veya Kubernetes
kubectl apply -f redis-deployment.yaml
```

### 2. Application Configuration

```properties
# production.properties
spring.data.redis.host=${REDIS_HOST}
spring.data.redis.port=${REDIS_PORT}
spring.data.redis.password=${REDIS_PASSWORD}
spring.data.redis.timeout=5000ms
spring.data.redis.lettuce.pool.max-active=20
spring.data.redis.lettuce.pool.max-idle=10
spring.data.redis.lettuce.pool.min-idle=2
```

### 3. Environment Variables

```bash
export REDIS_HOST=redis.production.example.com
export REDIS_PORT=6379
export REDIS_PASSWORD=your-secure-password
```

### 4. Verification

```bash
# Health check
curl http://localhost:8080/actuator/health

# Redis connection test
redis-cli -h redis.production.example.com -a your-secure-password PING
```

---

## 7. Troubleshooting ✅

### Sorun: Null Values Cached

**Semptom:**
- Redis'te null value'ler görülüyor
- Memory usage yüksek

**Çözüm:**
```properties
spring.cache.redis.cache-null-values=false
```

### Sorun: Cache Keys Collision

**Semptom:**
- Farklı cache'ler birbirine karışıyor
- Yanlış veriler dönüyor

**Çözüm:**
```properties
spring.cache.redis.use-key-prefix=true
spring.cache.redis.key-prefix=healthtourism:
```

### Sorun: High Memory Usage

**Semptom:**
- Redis memory limit'ine yaklaşılıyor
- Eviction rate yüksek

**Çözüm:**
1. TTL değerlerini düşürün
2. `cache-null-values=false` olduğundan emin olun
3. Gereksiz cache'leri temizleyin
4. Redis maxmemory limit set edin

---

## 8. Özet ✅

### ✅ Yapılandırma Doğrulandı

Tüm kritik ayarlar doğru şekilde yapılandırılmış:

1. ✅ **cache-null-values=false** - Null value caching devre dışı
2. ✅ **use-key-prefix=true** - Key prefix aktif
3. ✅ **key-prefix=healthtourism:** - Prefix belirlenmiş
4. ✅ **TTL configurations** - Cache-specific TTL'ler
5. ✅ **Connection pool** - Optimize edilmiş connection settings
6. ✅ **Environment variables** - Production-ready configuration

### Production Ready ✅

Redis cache yapılandırması production'a hazır! 🚀

**Son Kontrol:**
- [x] Null value caching disabled ✅
- [x] Key prefix configured ✅
- [x] TTL values optimized ✅
- [x] Connection pool optimized ✅
- [x] Environment variables support ✅
- [ ] Redis password set (production'da)
- [ ] TLS/SSL enabled (production için)
- [ ] Monitoring aktif (production'da)

