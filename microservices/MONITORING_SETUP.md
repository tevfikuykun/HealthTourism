# Monitoring Setup Guide

## Prometheus Metrics

Her servise Prometheus metrics eklemek için:

### 1. pom.xml'e Dependency Ekle

```xml
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-registry-prometheus</artifactId>
</dependency>
```

### 2. application.properties'e Ekle

```properties
management.endpoints.web.exposure.include=health,info,metrics,prometheus
management.metrics.export.prometheus.enabled=true
```

## Grafana Dashboard

1. Prometheus'u data source olarak ekle
2. Pre-built dashboard'ları kullan
3. Custom dashboard'lar oluştur

## ELK Stack (Elasticsearch, Logstash, Kibana)

### Logging Configuration

```properties
logging.level.root=INFO
logging.level.com.healthtourism=DEBUG
logging.file.name=logs/application.log
```

### Logstash Configuration

```ruby
input {
  file {
    path => "/logs/*.log"
    type => "microservice"
  }
}
```

## Alerting

- Prometheus Alertmanager
- Email notifications
- Slack integration

