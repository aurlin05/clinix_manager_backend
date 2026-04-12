package com.clinix.clinic.service;

import com.clinix.clinic.dto.request.LoginRequest;
import com.clinix.clinic.dto.request.RegisterRequest;
import com.clinix.clinic.dto.response.AuthResponse;
import com.clinix.clinic.model.Clinic;
import com.clinix.clinic.model.User;
import com.clinix.clinic.model.enums.Role;
import com.clinix.clinic.repository.ClinicRepository;
import com.clinix.clinic.repository.UserRepository;
import com.clinix.clinic.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final ClinicRepository clinicRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    /**
     * Inscription publique → crée une nouvelle clinique isolée + premier admin.
     * Chaque inscription = une nouvelle clinique indépendante.
     */
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException(
                    "Le nom d'utilisateur '" + request.getUsername() + "' est déjà utilisé.");
        }

        // Nouvelle clinique
        Clinic clinic = clinicRepository.save(
                Clinic.builder().nom("Clinique de " + request.getUsername()).build()
        );

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.ADMIN)
                .clinicId(clinic.getId())
                .build();

        userRepository.save(user);

        String token = jwtUtil.generateToken(user);
        return AuthResponse.builder()
                .token(token)
                .username(user.getUsername())
                .role(user.getRole())
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        User user = userRepository.findByUsername(request.getUsername()).orElseThrow();
        String token = jwtUtil.generateToken(user);

        return AuthResponse.builder()
                .token(token)
                .username(user.getUsername())
                .role(user.getRole())
                .build();
    }
}
