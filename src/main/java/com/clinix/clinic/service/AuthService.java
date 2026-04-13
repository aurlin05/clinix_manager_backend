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
        if (request.getEmail() != null && !request.getEmail().isBlank()
                && userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException(
                    "L'adresse email '" + request.getEmail() + "' est déjà utilisée.");
        }

        // Nouvelle clinique
        Clinic clinic = clinicRepository.save(
                Clinic.builder().nom("Clinique de " + request.getUsername()).build()
        );

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail() != null && !request.getEmail().isBlank()
                        ? request.getEmail() : null)
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
        // Résoudre l'identifiant : email → username si nécessaire
        String identifier = request.getUsernameOrEmail().trim();
        String resolvedUsername = resolveUsername(identifier);

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(resolvedUsername, request.getPassword())
        );

        User user = userRepository.findByUsername(resolvedUsername).orElseThrow();
        String token = jwtUtil.generateToken(user);

        return AuthResponse.builder()
                .token(token)
                .username(user.getUsername())
                .role(user.getRole())
                .build();
    }

    private String resolveUsername(String identifier) {
        // Si l'identifiant contient un @, on cherche par email
        if (identifier.contains("@")) {
            return userRepository.findByEmail(identifier)
                    .map(User::getUsername)
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Aucun compte associé à cet email."));
        }
        return identifier;
    }
}
