package com.healthtourism.common.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public abstract class BaseUnitTest {
    
    @BeforeEach
    void setUp() {
        // Common setup for all unit tests
    }
}

