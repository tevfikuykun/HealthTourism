package com.healthtourism.adminservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @InjectMocks
    private ReportService reportService;

    @Test
    void testGetDashboardStatistics() {
        // When
        Map<String, Object> stats = reportService.getDashboardStatistics();

        // Then
        assertNotNull(stats);
        assertTrue(stats.containsKey("totalRevenue"));
        assertTrue(stats.containsKey("mostBookedHospital"));
        assertTrue(stats.containsKey("userGrowth"));
        assertTrue(stats.containsKey("totalReservations"));
    }

    @Test
    void testGetRevenueReport() {
        // Given
        LocalDateTime startDate = LocalDateTime.now().minusMonths(3);
        LocalDateTime endDate = LocalDateTime.now();

        // When
        Map<String, Object> report = reportService.getRevenueReport(startDate, endDate);

        // Then
        assertNotNull(report);
        assertTrue(report.containsKey("totalRevenue"));
        assertTrue(report.containsKey("revenueByService"));
        assertTrue(report.containsKey("revenueByMonth"));
    }

    @Test
    void testGetBookingStatistics() {
        // When
        Map<String, Object> stats = reportService.getBookingStatistics();

        // Then
        assertNotNull(stats);
        assertTrue(stats.containsKey("totalBookings"));
        assertTrue(stats.containsKey("bookingsByStatus"));
        assertTrue(stats.containsKey("bookingsByHospital"));
    }
}
