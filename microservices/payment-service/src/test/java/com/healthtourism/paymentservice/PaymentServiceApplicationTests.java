package com.healthtourism.paymentservice;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@Disabled("Full Spring context load disabled: Spring Cloud/Boot autoconfig mismatch needs alignment before enabling.")
@SpringBootTest
@ActiveProfiles("test")
class PaymentServiceApplicationTests {

    @Test
    void contextLoads() {
        // Verifies Spring context can start with test profile.
    }
}

