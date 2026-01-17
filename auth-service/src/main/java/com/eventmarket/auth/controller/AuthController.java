package com.eventmarket.auth.controller;

import com.eventmarket.auth.dto.AuthResponse;
import com.eventmarket.auth.dto.RegisterRequest;
import com.eventmarket.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public AuthResponse register(@RequestBody RegisterRequest request) {
        return authService.register(request);
    }
}