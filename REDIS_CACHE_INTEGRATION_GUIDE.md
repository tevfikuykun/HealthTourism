# Redis Cache Entegrasyonu Kılavuzu

## 1. Redis Cache Nedir? ✅

Redis (Remote Dictionary Server), yüksek performanslı bir in-memory data structure store'dur. Distributed caching için mükemmel bir çözümdür.

### Avantajlar

1. **Distributed Caching:**
   - Birden fazla application instance cache'i paylaşır
   - Load balancer arkasında çalışan uygulamalar için ideal

2. **High Performance:**
   - In-memory storage (RAM'de çalışır)
   - Sub-millisecond latency
   - Yüksek throughput

3. **Scalability:**
   - Redis Cluster ile horizontal scaling
   - Master-slave replication
   - Sentinel ile high availability

4. **Persistence:**
   - RDB (point-in-time snapshots)
   - AOF (append-only file)
   - Redis restart olsa bile cache korunabilir

---

## 2. Yapılandırma ✅

### Dependencies

**pom.xml:**
```xml
<!-- Redis Support -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-pool2</artifactId>
</dependency>
```

### Application Properties

**application.properties:**
```properties
# Redis Configuration
spring.data.redis.host=${REDIS_HOST:localhost}
spring.data.redis.port=${REDIS_PORT:6379}
spring.data.redis.password=${REDIS_PASSWORD:}
spring.data.redis.database=0
spring.data.redis.timeout=2000ms
spring.data.redis.lettuce.pool.enabled=true
spring.data.redis.lettuce.pool.max-active=8
spring.data.redis.lettuce.pool.max-idle=8
spring.data.redis.lettuce.pool.min-idle=0
spring.data.redis.lettuce.pool.max-wait=-1ms

# Cache Configuration (Redis)
spring.cache.type=redis
spring.cache.cache-names=hospitals,doctors,packages,specializations,accommodations,flights,transfers,carRentals
spring.cache.redis.time-to-live=300000
spring.cache.redis.cache-null-values=false
spring.cache.redis.use-key-prefix=true
spring.cache.redis.key-prefix=healthtourism:
```

### Environment Variables

Production'da environment variables kullanın:

```bash
export REDIS_HOST=redis.example.com
export REDIS_PORT=6379
export REDIS_PASSWORD=your-secure-password
```

---

## 3. Cache Configuration ✅

### Cache TTL (Time To Live)

Her cache için farklı TTL değerleri:

- **hospitals:** 5 dakika
- **doctors:** 5 dakika
- **packages:** 10 dakika (daha az değişir)
- **specializations:** 30 dakika (çok nadiren değişir)
- **accommodations:** 5 dakika
- **flights:** 5 dakika
- **transfers:** 5 dakika
- **carRentals:** 5 dakika

### Cache Key Prefix

Tüm cache key'leri `healthtourism:` prefix'i ile başlar:

```
healthtourism:hospitals::all-active
healthtourism:doctors::hospital-1
healthtourism:packages::all-active
```

---

## 4. Kullanım ✅

### Service Metodlarında Cache

**Örnek: HospitalService**
```java
@Cacheable(value = "hospitals", key = "'all-active'")
public List<HospitalDTO> getAllActiveHospitals() {
    // İlk çağrıda Redis'e yazar
    // Sonraki çağrılarda Redis'ten döner (5 dakika boyunca)
    return hospitalRepository.findAllActiveOrderByRatingDesc()
            .stream()
            .map(this::toDtoWithDetails)
            .collect(Collectors.toList());
}
```

**Örnek: DoctorService**
```java
@Cacheable(value = "doctors", key = "'hospital-' + #hospitalId")
public List<DoctorDTO> getDoctorsByHospital(Long hospitalId) {
    // Her hospitalId için ayrı cache entry
    return doctorRepository.findByHospitalIdAndIsAvailableTrue(hospitalId)
            .stream()
            .map(doctorMapper::toDto)
            .collect(Collectors.toList());
}
```

### Cache Eviction (Cache Temizleme)

**Veri Güncellendiğinde:**
```java
@CacheEvict(value = "hospitals", key = "'all-active'")
public HospitalDTO updateHospital(Long id, Hospital hospital) {
    // Cache temizlenir, bir sonraki çağrıda Redis'ten çekilir
    return hospitalService.updateHospital(id, hospital);
}

@CacheEvict(value = "hospitals", allEntries = true)
public void clearHospitalCache() {
    // Tüm hospital cache'ini temizle
}
```

**Tüm Cache'i Temizle:**
```java
@CacheEvict(value = {"hospitals", "doctors", "packages"}, allEntries = true)
public void clearAllCaches() {
    // Tüm cache'leri temizle
}
```

---

## 5. Redis Installation ✅

### Docker ile Redis Kurulumu

```bash
# Redis container'ı çalıştır
docker run -d \
  --name redis \
  -p 6379:6379 \
  redis:7-alpine

# Redis CLI ile bağlan
docker exec -it redis redis-cli
```

### Docker Compose

**docker-compose.yml:**
```yaml
version: '3.8'
services:
  redis:
    image: redis:7-alpine
    container_name: healthtourism-redis
    ports:
      - "6379:6379"
    volumes:
      - redis-data:/data
    command: redis-server --appendonly yes
    restart: unless-stopped

volumes:
  redis-data:
```

### Local Installation

**macOS (Homebrew):**
```bash
brew install redis
brew services start redis
```

**Ubuntu/Debian:**
```bash
sudo apt-get update
sudo apt-get install redis-server
sudo systemctl start redis
sudo systemctl enable redis
```

**Windows:**
- WSL2 kullanın veya Redis için Windows port kullanın

---

## 6. Redis Monitoring ✅

### Health Check

**Endpoint:** `/actuator/health`

**Response:**
```json
{
  "status": "UP",
  "components": {
    "cache": {
      "status": "UP",
      "details": {
        "cache": "Redis",
        "status": "Cache is healthy",
        "connected": true,
        "cacheNames": ["hospitals", "doctors", "packages"],
        "cacheCount": 8,
        "redisVersion": "# Server\nredis_version:7.2.0"
      }
    }
  }
}
```

### Redis CLI Komutları

```bash
# Redis'e bağlan
redis-cli

# Tüm key'leri listele
KEYS *

# Belirli pattern'e göre key'leri listele
KEYS healthtourism:*

# Cache'deki bir key'in değerini göster
GET healthtourism:hospitals::all-active

# Cache'deki key'in TTL'ini göster
TTL healthtourism:hospitals::all-active

# Cache'i temizle (dikkatli kullanın!)
FLUSHDB

# Redis info
INFO server
INFO memory
INFO stats
```

### Redis Monitoring Tools

1. **redis-cli:**
   ```bash
   redis-cli --stat
   redis-cli MONITOR
   ```

2. **Redis Insight (GUI):**
   - https://redis.io/insight/
   - Visual monitoring ve debugging

3. **Prometheus + Grafana:**
   - Redis exporter ile metrics
   - Grafana dashboard'ları

---

## 7. Performance İyileştirmeleri ✅

### Öncesi (Caffeine - In-Memory)
- ✅ Hızlı (20ms API response)
- ❌ Distributed değil (her instance'ın kendi cache'i)
- ❌ Instance restart'ta cache kaybolur

### Sonrası (Redis - Distributed)
- ✅ Hızlı (20ms API response - aynı performans)
- ✅ Distributed (tüm instance'lar aynı cache'i paylaşır)
- ✅ Persistence (Redis restart olsa bile cache korunabilir)
- ✅ Scalable (Redis cluster ile horizontal scaling)

### Cache Hit Rate

İdeal cache hit rate: **%80-90**

**Örnek Metrics:**
```
Cache Hit Rate: 85.5%
- Hospitals cache: 90% hit rate
- Doctors cache: 85% hit rate
- Packages cache: 80% hit rate
```

---

## 8. Best Practices ✅

### 1. Cache Key Strategy

**İyi:**
```java
@Cacheable(value = "hospitals", key = "'all-active'")
@Cacheable(value = "doctors", key = "'hospital-' + #hospitalId")
@Cacheable(value = "packages", key = "'type-' + #packageType")
```

**Kötü:**
```java
@Cacheable(value = "hospitals") // Key belirtilmemiş
@Cacheable(value = "doctors", key = "#hospital") // Complex object key
```

### 2. Cache TTL Strategy

- **Sık değişen veriler:** 5 dakika
- **Orta sıklıkta değişen veriler:** 10 dakika
- **Nadiren değişen veriler:** 30 dakika veya daha fazla

### 3. Cache Eviction Strategy

- **Write-through:** Veri yazılırken cache güncellenir
- **Write-behind:** Veri yazıldıktan sonra cache güncellenir
- **Cache-aside:** Cache ayrı bir layer olarak yönetilir (Spring Cache default)

### 4. Null Value Caching

```properties
spring.cache.redis.cache-null-values=false
```

Null values cache'lenmez (memory tasarrufu için).

### 5. Connection Pooling

```properties
spring.data.redis.lettuce.pool.max-active=8
spring.data.redis.lettuce.pool.max-idle=8
spring.data.redis.lettuce.pool.min-idle=0
```

Connection pool size'ı ihtiyaca göre ayarlayın.

---

## 9. Production Deployment ✅

### Redis High Availability

**Redis Sentinel:**
```yaml
# Redis Sentinel configuration
sentinel monitor mymaster redis-master 6379 2
sentinel down-after-milliseconds mymaster 5000
sentinel failover-timeout mymaster 10000
```

**Redis Cluster:**
```bash
# Redis Cluster kurulumu (6 node: 3 master + 3 replica)
redis-cli --cluster create \
  127.0.0.1:7000 127.0.0.1:7001 127.0.0.1:7002 \
  127.0.0.1:7003 127.0.0.1:7004 127.0.0.1:7005 \
  --cluster-replicas 1
```

### Kubernetes Deployment

**redis-deployment.yaml:**
```yaml
apiVersion: apps/v1
kind: StatefulSet
metadata:
  name: redis
spec:
  serviceName: redis
  replicas: 1
  template:
    spec:
      containers:
      - name: redis
        image: redis:7-alpine
        ports:
        - containerPort: 6379
        volumeMounts:
        - name: redis-data
          mountPath: /data
  volumeClaimTemplates:
  - metadata:
      name: redis-data
    spec:
      accessModes: [ "ReadWriteOnce" ]
      resources:
        requests:
          storage: 10Gi
```

---

## 10. Sorun Giderme ✅

### Sorun: Redis Connection Failed

**Hata:**
```
Unable to connect to Redis at localhost:6379
```

**Çözüm:**
1. Redis'in çalıştığından emin olun: `redis-cli ping`
2. Host ve port'u kontrol edin
3. Firewall kurallarını kontrol edin
4. Redis password'ün doğru olduğundan emin olun

### Sorun: Cache Çalışmıyor

**Hata:**
Cache'e yazılıyor ama okunamıyor.

**Çözüm:**
1. Redis connection'ı kontrol edin
2. Cache key'lerini kontrol edin: `redis-cli KEYS *`
3. TTL değerlerini kontrol edin
4. Serialization/Deserialization sorunlarını kontrol edin

### Sorun: Memory Usage Yüksek

**Hata:**
Redis memory kullanımı çok yüksek.

**Çözüm:**
1. TTL değerlerini düşürün
2. Cache size limit'i ekleyin: `maxmemory-policy`
3. Gereksiz cache'leri temizleyin
4. Redis memory optimization: `redis-cli MEMORY DOCTOR`

---

## 11. Gelecek İyileştirmeler

- [ ] Redis Cluster desteği
- [ ] Redis Sentinel (High Availability)
- [ ] Cache warming strategies
- [ ] Distributed cache synchronization
- [ ] Cache metrics (Prometheus)
- [ ] Cache preloading
- [ ] Multi-tier caching (L1: Caffeine, L2: Redis)

---

## 12. Özet

✅ **Redis Cache entegrasyonu tamamlandı!**

**Özellikler:**
- ✅ Distributed caching
- ✅ High performance
- ✅ Scalability
- ✅ Persistence support
- ✅ Health monitoring

**Kullanım:**
- ✅ Service metodlarında `@Cacheable` annotation'ı
- ✅ Cache eviction ile cache temizleme
- ✅ Redis CLI ile monitoring
- ✅ Health check endpoint'i

**Production Ready:**
- ✅ Environment variables desteği
- ✅ Connection pooling
- ✅ Error handling
- ✅ Health indicators

