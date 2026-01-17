package com.eventmarket.auth.controller;

import com.eventmarket.auth.AbstractIntegrationTest;
import com.eventmarket.auth.dto.AuthResponse;
import com.eventmarket.auth.dto.RegisterRequest;
import com.eventmarket.auth.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

class AuthControllerIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldRegisterUserSuccessfully() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("integration@test.com");
        request.setPassword("securePass123");
        request.setFirstName("Integration");
        request.setLastName("User");
        request.setRole("ROLE_USER");

        ResponseEntity<AuthResponse> response = restTemplate.postForEntity(
                "/api/v1/auth/register",
                request,
                AuthResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getToken()).isNotEmpty();

        var savedUser = userRepository.findByEmail("integration@test.com");
        assertThat(savedUser).isPresent();
        assertThat(savedUser.get().getFirstName()).isEqualTo("Integration");

        assertThat(savedUser.get().getRoles()).hasSize(1);
        Assertions.assertEquals("ROLE_USER", savedUser.get().getRoles().iterator().next().getName());
    }

    @Test
    void shouldReturnConflict_WhenUserAlreadyExists() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("duplicate@test.com");
        request.setPassword("123");
        request.setRole("ROLE_USER");

        restTemplate.postForEntity("/api/v1/auth/register", request, AuthResponse.class);

        ResponseEntity<String> errorResponse = restTemplate.postForEntity(
                "/api/v1/auth/register",
                request,
                String.class
        );

        assertThat(errorResponse.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(errorResponse.getBody()).contains("User with email duplicate@test.com already exists");
    }
}