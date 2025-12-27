package com.example.HealthTourism.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Database Health Indicator
 * Veritabanı bağlantısının sağlıklı olup olmadığını kontrol eder
 * 
 * Endpoint: /actuator/health
 * 
 * Health Status:
 * - UP: Veritabanı bağlantısı sağlıklı
 * - DOWN: Veritabanı bağlantısı kopmuş veya hata var
 */
@Component
@RequiredArgsConstructor
public class DatabaseHealthIndicator implements HealthIndicator {
    
    private final DataSource dataSource;
    
    @Override
    public Health health() {
        try (Connection connection = dataSource.getConnection()) {
            // Veritabanı bağlantısını test et
            if (connection.isValid(1)) {
                return Health.up()
                        .withDetail("database", "MySQL")
                        .withDetail("status", "Connection is healthy")
                        .withDetail("catalog", connection.getCatalog())
                        .build();
            } else {
                return Health.down()
                        .withDetail("database", "MySQL")
                        .withDetail("status", "Connection is invalid")
                        .build();
            }
        } catch (SQLException e) {
            return Health.down()
                    .withDetail("database", "MySQL")
                    .withDetail("status", "Connection failed")
                    .withDetail("error", e.getMessage())
                    .build();
        }
    }
}

