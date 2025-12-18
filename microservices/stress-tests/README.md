# Stress Test Suite for Health Tourism Platform

## Overview

Bu stress test suite'i, Health Tourism mikroservis mimarisinin Circuit Breaker threshold'larını ve Gateway timeout limitlerini test etmek için tasarlanmıştır.

## Test Senaryoları

### 1. API Gateway Load Test
- **Hedef**: Gateway'in yüksek trafikte performansını test et
- **Threads**: 100 concurrent users
- **Ramp-up**: 60 saniye
- **Endpoints**: 
  - GET /api/hospitals
  - GET /api/doctors
  - GET /api/iot-monitoring (60s timeout)
  - POST /api/cost-predictor/predict

### 2. Circuit Breaker Test
- **Hedef**: Resilience4j Circuit Breaker'ların devreye girişini test et
- **Senaryo**: Bir servisi yavaşlat/kapat ve Circuit Breaker'ın fallback mekanizmasını gözlemle
- **Beklenen**: Fallback metodları devreye girmeli

### 3. Gateway Timeout Test
- **Hedef**: IoT ve VR servisleri için timeout limitlerini test et
- **IoT Monitoring**: 60 saniye timeout
- **Virtual Tour**: 120 saniye timeout
- **Beklenen**: Timeout limitleri içinde response dönmeli

## JMeter Test

### Kurulum
```bash
# JMeter indir (https://jmeter.apache.org/download_jmeter.cgi)
# veya
brew install jmeter  # macOS
```

### Çalıştırma
```bash
cd microservices/stress-tests/jmeter
jmeter -n -t health-tourism-stress-test.jmx -l results.jtl -e -o report/
```

### Parametreler
- `GATEWAY_URL`: API Gateway URL (default: http://localhost:8080)
- `THREADS`: Concurrent user sayısı (default: 100)
- `RAMP_UP`: Ramp-up süresi saniye (default: 60)

## Locust Test

### Kurulum
```bash
pip install locust
```

### Çalıştırma
```bash
cd microservices/stress-tests/locust
locust -f locustfile.py --host=http://localhost:8080
```

### Web UI
Locust başladıktan sonra: http://localhost:8089

### Komut Satırından
```bash
locust -f locustfile.py --host=http://localhost:8080 --users=100 --spawn-rate=10 --run-time=5m --headless
```

## Test Metrikleri

### İzlenmesi Gerekenler

1. **Response Time**
   - P50 (median): < 500ms
   - P95: < 2s
   - P99: < 5s

2. **Error Rate**
   - Success rate: > 95%
   - Circuit Breaker açılma sayısı
   - Timeout sayısı

3. **Throughput**
   - Requests/second
   - Concurrent users

4. **Circuit Breaker Metrics**
   - Open state'e geçiş sayısı
   - Fallback çağrı sayısı
   - Half-open state geçişleri

## Circuit Breaker Monitoring

### Actuator Endpoints
```bash
# Circuit Breaker durumunu kontrol et
curl http://localhost:8080/actuator/health

# Resilience4j metrics
curl http://localhost:8009/actuator/metrics/resilience4j.circuitbreaker.calls
```

### Beklenen Davranış

1. **Normal Durum**: Tüm istekler başarılı
2. **Yüksek Yük**: Bazı istekler yavaşlar, Circuit Breaker açılabilir
3. **Servis Hatası**: Circuit Breaker açılır, fallback devreye girer
4. **Recovery**: Half-open state'te test istekleri gönderilir

## Test Senaryoları Detayları

### Senaryo 1: Normal Load
- 50 concurrent users
- 5 dakika süre
- Beklenen: %100 success rate

### Senaryo 2: High Load
- 200 concurrent users
- 10 dakika süre
- Beklenen: Circuit Breaker bazı servislerde açılabilir

### Senaryo 3: Spike Test
- 0 → 500 users (anlık spike)
- 2 dakika süre
- Beklenen: Sistem spike'i handle edebilmeli

### Senaryo 4: Endurance Test
- 100 concurrent users
- 1 saat süre
- Beklenen: Memory leak yok, performans stabil

## Sonuç Analizi

### Başarı Kriterleri
- ✅ Response time P95 < 2s
- ✅ Error rate < 5%
- ✅ Circuit Breaker doğru çalışıyor
- ✅ Fallback mekanizmaları devreye giriyor
- ✅ Timeout limitleri korunuyor

### İyileştirme Önerileri
- Response time yüksekse: Caching ekle
- Error rate yüksekse: Retry logic optimize et
- Circuit Breaker çok açılıyorsa: Threshold'ları ayarla
- Memory leak varsa: Connection pool'ları kontrol et

## Production Öncesi Checklist

- [ ] Normal load test başarılı
- [ ] High load test başarılı
- [ ] Spike test başarılı
- [ ] Endurance test başarılı
- [ ] Circuit Breaker test başarılı
- [ ] Timeout test başarılı
- [ ] Memory leak yok
- [ ] CPU usage normal
- [ ] Database connection pool yeterli
- [ ] Gateway timeout limitleri optimize
