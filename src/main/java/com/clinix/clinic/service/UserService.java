package com.clinix.clinic.service;

import com.clinix.clinic.dto.request.UserRequest;
import com.clinix.clinic.dto.response.UserResponse;
import com.clinix.clinic.exception.ResourceNotFoundException;
import com.clinix.clinic.model.User;
import com.clinix.clinic.model.enums.Role;
import com.clinix.clinic.repository.MedecinRepository;
import com.clinix.clinic.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final MedecinRepository medecinRepository;
    private final PasswordEncoder passwordEncoder;

    public List<UserResponse> findAll(Long clinicId) {
        return userRepository.findByClinicId(clinicId).stream().map(this::toResponse).toList();
    }

    @Transactional
    public UserResponse create(UserRequest request, Long clinicId) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Nom d'utilisateur déjà pris : " + request.getUsername());
        }
        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new IllegalArgumentException("Le mot de passe est obligatoire à la création.");
        }
        Long medecinId = null;
        if (request.getRole() == Role.MEDECIN) {
            if (request.getMedecinId() == null) {
                throw new IllegalArgumentException("Le champ medecinId est obligatoire pour un utilisateur médecin.");
            }
            medecinRepository.findByIdAndClinicId(request.getMedecinId(), clinicId)
                    .orElseThrow(() -> new IllegalArgumentException("Médecin introuvable pour cette clinique : " + request.getMedecinId()));
            medecinId = request.getMedecinId();
        }

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .medecinId(medecinId)
                .clinicId(clinicId)
                .build();
        return toResponse(userRepository.save(user));
    }

    @Transactional
    public UserResponse update(Long id, UserRequest request, Long clinicId) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur", id));
        if (user.getClinicId() != null && !user.getClinicId().equals(clinicId)) {
            throw new ResourceNotFoundException("Utilisateur", id);
        }

        if (!user.getUsername().equals(request.getUsername())
                && userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Nom d'utilisateur déjà pris : " + request.getUsername());
        }

        user.setUsername(request.getUsername());
        user.setRole(request.getRole());
        if (request.getRole() == Role.MEDECIN) {
            if (request.getMedecinId() == null) {
                throw new IllegalArgumentException("Le champ medecinId est obligatoire pour un utilisateur médecin.");
            }
            medecinRepository.findByIdAndClinicId(request.getMedecinId(), clinicId)
                    .orElseThrow(() -> new IllegalArgumentException("Médecin introuvable pour cette clinique : " + request.getMedecinId()));
            user.setMedecinId(request.getMedecinId());
        } else {
            user.setMedecinId(null);
        }

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        return toResponse(userRepository.save(user));
    }

    @Transactional
    public void delete(Long id, Long clinicId) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur", id));
        if (user.getClinicId() != null && !user.getClinicId().equals(clinicId)) {
            throw new ResourceNotFoundException("Utilisateur", id);
        }
        userRepository.delete(user);
    }

    private UserResponse toResponse(User u) {
        String medecinNom = null;
        if (u.getMedecinId() != null) {
            medecinNom = medecinRepository.findById(u.getMedecinId())
                    .map(m -> "Dr. " + m.getPrenom() + " " + m.getNom())
                    .orElse(null);
        }
        return UserResponse.builder()
                .id(u.getId())
                .username(u.getUsername())
                .role(u.getRole())
                .medecinId(u.getMedecinId())
                .medecinNom(medecinNom)
                .build();
    }
}
