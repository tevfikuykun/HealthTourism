package com.healthtourism.analyticsservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "analytics_data")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String metricType; // revenue, users, reservations, services

    @Column(nullable = false)
    private String period; // daily, weekly, monthly, yearly

    @Column(nullable = false)
    private LocalDateTime date;

    @Column(nullable = false)
    private Double value;

    @Column(columnDefinition = "TEXT")
    private String metadata; // JSON additional data

    @Column(nullable = false)
    private LocalDateTime createdAt;
}

