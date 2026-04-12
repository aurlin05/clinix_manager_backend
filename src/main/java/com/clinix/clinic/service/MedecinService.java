package com.clinix.clinic.service;

import com.clinix.clinic.dto.request.MedecinRequest;
import com.clinix.clinic.dto.response.MedecinResponse;
import com.clinix.clinic.exception.ResourceNotFoundException;
import com.clinix.clinic.model.Medecin;
import com.clinix.clinic.model.User;
import com.clinix.clinic.model.enums.Role;
import com.clinix.clinic.repository.MedecinRepository;
import com.clinix.clinic.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MedecinService {

    private final MedecinRepository medecinRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public Page<MedecinResponse> findAll(Long clinicId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("nom"));
        return medecinRepository.findByClinicId(clinicId, pageable).map(this::toResponse);
    }

    public Page<MedecinResponse> search(Long clinicId, String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return medecinRepository.searchByKeyword(clinicId, keyword, pageable).map(this::toResponse);
    }

    public MedecinResponse findById(Long clinicId, Long id) {
        return toResponse(getMedecinOrThrow(clinicId, id));
    }

    @Transactional
    public MedecinResponse create(Long clinicId, MedecinRequest request) {
        // Préparer le matricule
        String matricule = request.getMatricule();
        if (matricule == null || matricule.isBlank()) {
            // Générer un matricule automatique si non fourni
            matricule = generateUniqueMatricule(clinicId);
        } else {
            // Vérifier que le matricule n'existe pas déjà pour cette clinique
            if (medecinRepository.existsByMatriculeAndClinicId(matricule, clinicId)) {
                throw new IllegalArgumentException("Matricule déjà utilisé pour cette clinique : " + matricule);
            }
        }

        if (request.getEmail() != null && medecinRepository.existsByEmailAndClinicId(request.getEmail(), clinicId)) {
            throw new IllegalArgumentException("Un médecin avec l'email " + request.getEmail() + " existe déjà.");
        }

        Medecin saved = medecinRepository.save(toEntity(request, clinicId, matricule));

        // Créer le compte d'accès si demandé
        String username = request.getUsername();
        if (username != null && !username.isBlank()) {
            if (userRepository.existsByUsername(username)) {
                throw new IllegalArgumentException("Nom d'utilisateur déjà pris : " + username);
            }
            if (request.getPassword() == null || request.getPassword().isBlank()) {
                throw new IllegalArgumentException("Le mot de passe est obligatoire pour créer un compte médecin.");
            }
            userRepository.save(User.builder()
                    .username(username)
                    .password(passwordEncoder.encode(request.getPassword()))
                    .role(Role.MEDECIN)
                    .medecinId(saved.getId())
                    .clinicId(clinicId)
                    .build());
        }

        return toResponse(saved);
    }

    @Transactional
    public MedecinResponse update(Long clinicId, Long id, MedecinRequest request) {
        Medecin medecin = getMedecinOrThrow(clinicId, id);

        if (!request.getMatricule().equals(medecin.getMatricule())
                && medecinRepository.existsByMatriculeAndClinicId(request.getMatricule(), clinicId)) {
            throw new IllegalArgumentException("Matricule déjà utilisé : " + request.getMatricule());
        }

        if (request.getEmail() != null && !request.getEmail().equals(medecin.getEmail())
                && medecinRepository.existsByEmailAndClinicId(request.getEmail(), clinicId)) {
            throw new IllegalArgumentException("Un médecin avec l'email " + request.getEmail() + " existe déjà.");
        }

        medecin.setNom(request.getNom());
        medecin.setPrenom(request.getPrenom());
        medecin.setSpecialite(request.getSpecialite());
        medecin.setEmail(request.getEmail());
        medecin.setTelephone(request.getTelephone());
        medecin.setMatricule(request.getMatricule());
        medecin.setDisponible(request.isDisponible());

        return toResponse(medecinRepository.save(medecin));
    }

    @Transactional
    public void delete(Long clinicId, Long id) {
        Medecin medecin = getMedecinOrThrow(clinicId, id);
        medecinRepository.delete(medecin);
    }

    private Medecin toEntity(MedecinRequest r, Long clinicId) {
        return Medecin.builder()
                .nom(r.getNom()).prenom(r.getPrenom()).specialite(r.getSpecialite())
                .email(r.getEmail()).telephone(r.getTelephone()).matricule(r.getMatricule())
                .disponible(r.isDisponible()).clinicId(clinicId)
                .build();
    }

    private Medecin toEntity(MedecinRequest r, Long clinicId, String matricule) {
        return Medecin.builder()
                .nom(r.getNom()).prenom(r.getPrenom()).specialite(r.getSpecialite())
                .email(r.getEmail()).telephone(r.getTelephone()).matricule(matricule)
                .disponible(r.isDisponible()).clinicId(clinicId)
                .build();
    }

    private String generateUniqueMatricule(Long clinicId) {
        // Générer un matricule unique basé sur le timestamp et UUID court
        long count = medecinRepository.countByClinicId(clinicId) + 1;
        String matricule = String.format("MED-%d-%04d", System.currentTimeMillis() / 1000, count);
        
        // Vérifier l'unicité (très rare qu'il y ait collision)
        while (medecinRepository.existsByMatriculeAndClinicId(matricule, clinicId)) {
            matricule = String.format("MED-%s", UUID.randomUUID().toString().substring(0, 12));
        }
        
        return matricule;
    }

    private MedecinResponse toResponse(Medecin m) {
        return MedecinResponse.builder()
                .id(m.getId()).nom(m.getNom()).prenom(m.getPrenom())
                .specialite(m.getSpecialite()).email(m.getEmail())
                .telephone(m.getTelephone()).matricule(m.getMatricule())
                .disponible(m.isDisponible())
                .build();
    }

    private Medecin getMedecinOrThrow(Long clinicId, Long id) {
        return medecinRepository.findByIdAndClinicId(id, clinicId)
                .orElseThrow(() -> new ResourceNotFoundException("Médecin", id));
    }
}
