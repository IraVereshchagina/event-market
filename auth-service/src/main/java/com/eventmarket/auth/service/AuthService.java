package com.eventmarket.auth.service;

import com.eventmarket.auth.dto.AuthResponse;
import com.eventmarket.auth.dto.RegisterRequest;
import com.eventmarket.auth.entity.Role;
import com.eventmarket.auth.entity.User;
import com.eventmarket.auth.exception.UserAlreadyExistsException;
import com.eventmarket.auth.repository.RoleRepository;
import com.eventmarket.auth.repository.UserRepository;
import com.eventmarket.auth.security.JwtService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional
    public AuthResponse register(RegisterRequest request){
        if (userRepository.existsByEmail(request.getEmail())){
            throw new UserAlreadyExistsException("User with email " + request.getEmail() + " already exists");
        }

        String roleName = request.getRole() == null ? "ROLE_USER" : request.getRole();
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .companyName(request.getCompanyName())
                .inn(request.getInn())
                .roles(Set.of(role))
                .build();

        userRepository.save(user);

        String token = jwtService.generateToken(user.getEmail(), role.getName());
        return new AuthResponse(token);
    }
}
