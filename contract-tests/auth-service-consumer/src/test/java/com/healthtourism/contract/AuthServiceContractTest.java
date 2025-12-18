package com.healthtourism.contract;

import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.V4Pact;
import au.com.dius.pact.core.model.annotations.Pact;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.IOException;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Pact Contract Tests for Auth Service
 * Ensures API contracts between microservices don't break
 */
@ExtendWith(PactConsumerTestExt.class)
@PactTestFor(providerName = "auth-service")
class AuthServiceContractTest {

    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Pact(consumer = "user-service")
    public V4Pact loginSuccessPact(PactDslWithProvider builder) {
        return builder
            .given("user exists with email test@example.com")
            .uponReceiving("a login request with valid credentials")
            .path("/api/auth/login")
            .method("POST")
            .headers(Map.of("Content-Type", "application/json"))
            .body(new PactDslJsonBody()
                .stringType("email", "test@example.com")
                .stringType("password", "password123"))
            .willRespondWith()
            .status(200)
            .headers(Map.of("Content-Type", "application/json"))
            .body(new PactDslJsonBody()
                .stringType("accessToken")
                .stringType("refreshToken")
                .stringValue("tokenType", "Bearer")
                .integerType("userId")
                .stringType("email")
                .stringType("role")
                .integerType("expiresIn"))
            .toPact(V4Pact.class);
    }

    @Pact(consumer = "user-service")
    public V4Pact loginFailurePact(PactDslWithProvider builder) {
        return builder
            .given("user does not exist")
            .uponReceiving("a login request with invalid credentials")
            .path("/api/auth/login")
            .method("POST")
            .headers(Map.of("Content-Type", "application/json"))
            .body(new PactDslJsonBody()
                .stringType("email", "invalid@example.com")
                .stringType("password", "wrongpassword"))
            .willRespondWith()
            .status(401)
            .headers(Map.of("Content-Type", "application/json"))
            .body(new PactDslJsonBody()
                .stringType("error", "Invalid email or password"))
            .toPact(V4Pact.class);
    }

    @Pact(consumer = "user-service")
    public V4Pact registerSuccessPact(PactDslWithProvider builder) {
        return builder
            .given("email new@example.com is not registered")
            .uponReceiving("a registration request with valid data")
            .path("/api/auth/register")
            .method("POST")
            .headers(Map.of("Content-Type", "application/json"))
            .body(new PactDslJsonBody()
                .stringType("email", "new@example.com")
                .stringType("password", "password123")
                .stringType("firstName", "New")
                .stringType("lastName", "User"))
            .willRespondWith()
            .status(200)
            .headers(Map.of("Content-Type", "application/json"))
            .body(new PactDslJsonBody()
                .stringType("accessToken")
                .stringType("refreshToken")
                .stringValue("tokenType", "Bearer")
                .integerType("userId")
                .stringType("email")
                .stringType("role"))
            .toPact(V4Pact.class);
    }

    @Pact(consumer = "user-service")
    public V4Pact validateTokenPact(PactDslWithProvider builder) {
        return builder
            .given("valid JWT token exists")
            .uponReceiving("a token validation request")
            .path("/api/auth/validate")
            .method("GET")
            .headers(Map.of("Authorization", "Bearer valid-jwt-token"))
            .willRespondWith()
            .status(200)
            .body(new PactDslJsonBody()
                .booleanValue("valid", true))
            .toPact(V4Pact.class);
    }

    @Pact(consumer = "api-gateway")
    public V4Pact refreshTokenPact(PactDslWithProvider builder) {
        return builder
            .given("valid refresh token exists")
            .uponReceiving("a token refresh request")
            .path("/api/auth/refresh")
            .method("POST")
            .headers(Map.of("Content-Type", "application/json"))
            .body(new PactDslJsonBody()
                .stringType("refreshToken", "valid-refresh-token"))
            .willRespondWith()
            .status(200)
            .headers(Map.of("Content-Type", "application/json"))
            .body(new PactDslJsonBody()
                .stringType("accessToken")
                .stringType("refreshToken")
                .stringValue("tokenType", "Bearer")
                .integerType("expiresIn"))
            .toPact(V4Pact.class);
    }

    @Test
    @PactTestFor(pactMethod = "loginSuccessPact")
    void testLoginSuccess(MockServer mockServer) throws IOException {
        String requestBody = objectMapper.writeValueAsString(Map.of(
            "email", "test@example.com",
            "password", "password123"
        ));

        Request request = new Request.Builder()
            .url(mockServer.getUrl() + "/api/auth/login")
            .post(RequestBody.create(requestBody, MediaType.parse("application/json")))
            .build();

        try (Response response = client.newCall(request).execute()) {
            assertThat(response.code()).isEqualTo(200);
            
            Map<String, Object> body = objectMapper.readValue(response.body().string(), Map.class);
            assertThat(body).containsKey("accessToken");
            assertThat(body).containsKey("refreshToken");
            assertThat(body.get("tokenType")).isEqualTo("Bearer");
        }
    }

    @Test
    @PactTestFor(pactMethod = "loginFailurePact")
    void testLoginFailure(MockServer mockServer) throws IOException {
        String requestBody = objectMapper.writeValueAsString(Map.of(
            "email", "invalid@example.com",
            "password", "wrongpassword"
        ));

        Request request = new Request.Builder()
            .url(mockServer.getUrl() + "/api/auth/login")
            .post(RequestBody.create(requestBody, MediaType.parse("application/json")))
            .build();

        try (Response response = client.newCall(request).execute()) {
            assertThat(response.code()).isEqualTo(401);
        }
    }

    @Test
    @PactTestFor(pactMethod = "registerSuccessPact")
    void testRegisterSuccess(MockServer mockServer) throws IOException {
        String requestBody = objectMapper.writeValueAsString(Map.of(
            "email", "new@example.com",
            "password", "password123",
            "firstName", "New",
            "lastName", "User"
        ));

        Request request = new Request.Builder()
            .url(mockServer.getUrl() + "/api/auth/register")
            .post(RequestBody.create(requestBody, MediaType.parse("application/json")))
            .build();

        try (Response response = client.newCall(request).execute()) {
            assertThat(response.code()).isEqualTo(200);
            
            Map<String, Object> body = objectMapper.readValue(response.body().string(), Map.class);
            assertThat(body).containsKey("accessToken");
            assertThat(body).containsKey("userId");
        }
    }

    @Test
    @PactTestFor(pactMethod = "validateTokenPact")
    void testValidateToken(MockServer mockServer) throws IOException {
        Request request = new Request.Builder()
            .url(mockServer.getUrl() + "/api/auth/validate")
            .header("Authorization", "Bearer valid-jwt-token")
            .get()
            .build();

        try (Response response = client.newCall(request).execute()) {
            assertThat(response.code()).isEqualTo(200);
        }
    }

    @Test
    @PactTestFor(pactMethod = "refreshTokenPact")
    void testRefreshToken(MockServer mockServer) throws IOException {
        String requestBody = objectMapper.writeValueAsString(Map.of(
            "refreshToken", "valid-refresh-token"
        ));

        Request request = new Request.Builder()
            .url(mockServer.getUrl() + "/api/auth/refresh")
            .post(RequestBody.create(requestBody, MediaType.parse("application/json")))
            .build();

        try (Response response = client.newCall(request).execute()) {
            assertThat(response.code()).isEqualTo(200);
            
            Map<String, Object> body = objectMapper.readValue(response.body().string(), Map.class);
            assertThat(body).containsKey("accessToken");
            assertThat(body).containsKey("refreshToken");
        }
    }
}

