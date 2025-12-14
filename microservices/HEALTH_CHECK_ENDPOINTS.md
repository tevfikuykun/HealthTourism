# Health Check Endpoints

Tüm servisler için health check endpoint'leri:

## Actuator Endpoints

Her servis için Spring Boot Actuator endpoint'leri:

- `GET /actuator/health` - Servis sağlık durumu
- `GET /actuator/info` - Servis bilgileri
- `GET /actuator/metrics` - Metrikler
- `GET /actuator/prometheus` - Prometheus metrikleri

## Örnek Kullanım

```bash
# Health check
curl http://localhost:8001/actuator/health

# Metrics
curl http://localhost:8001/actuator/metrics

# Prometheus
curl http://localhost:8001/actuator/prometheus
```

## Tüm Servisler

| Servis | Port | Health Endpoint |
|--------|------|----------------|
| User Service | 8001 | http://localhost:8001/actuator/health |
| Hospital Service | 8002 | http://localhost:8002/actuator/health |
| Doctor Service | 8003 | http://localhost:8003/actuator/health |
| ... | ... | ... |

