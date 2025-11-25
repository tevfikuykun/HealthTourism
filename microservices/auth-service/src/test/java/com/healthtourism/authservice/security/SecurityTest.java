package com.healthtourism.authservice.security;

import com.healthtourism.authservice.controller.AuthController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@DisplayName("Security Tests")
class SecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Should allow unauthenticated access to auth endpoints")
    void testAuthEndpointsAccessible() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                .with(csrf()))
                .andExpect(status().isBadRequest()); // Bad request is expected without body, but endpoint is accessible
    }

    @Test
    @DisplayName("Should allow unauthenticated access to login")
    void testLoginEndpointAccessible() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                .with(csrf()))
                .andExpect(status().isBadRequest()); // Bad request is expected without body
    }
}

