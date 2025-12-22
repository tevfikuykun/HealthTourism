package com.healthtourism.paymentservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class PaymentServiceApplicationTests {

    @Test
    void contextLoads() {
        // Verifies Spring context can start with test profile.
    }
}

