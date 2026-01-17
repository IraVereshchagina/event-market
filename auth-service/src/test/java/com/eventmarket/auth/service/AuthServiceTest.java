package com.eventmarket.auth.service;

import com.eventmarket.auth.entity.Role;
import com.eventmarket.auth.entity.User;
import com.eventmarket.auth.dto.AuthResponse;
import com.eventmarket.auth.dto.RegisterRequest;
import com.eventmarket.auth.repository.RoleRepository;
import com.eventmarket.auth.repository.UserRepository;
import com.eventmarket.auth.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.eventmarket.auth.exception.UserAlreadyExistsException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private RoleRepository roleRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    @Test
    void register_ShouldRegisterUser_WhenRequestIsValid() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@example.com");
        request.setPassword("12345");
        request.setRole("ROLE_USER");

        Role role = new Role();
        role.setName("ROLE_USER");

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(role));
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encoded_pass");
        when(jwtService.generateToken(anyString(), anyString())).thenReturn("mock_token");

        AuthResponse response = authService.register(request);

        assertNotNull(response);
        assertEquals("mock_token", response.getToken());

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void register_ShouldThrowException_WhenEmailExists() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("existing@example.com");

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> authService.register(request));

        verify(userRepository, never()).save(any());
    }
}