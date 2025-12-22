package com.healthtourism.reservationservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class ReservationServiceApplicationTests {

    @Test
    void contextLoads() {
        // Verifies Spring context can start with test profile.
    }
}

