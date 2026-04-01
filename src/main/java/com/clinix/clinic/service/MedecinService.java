package com.clinix.clinic.service;

import com.clinix.clinic.dto.request.MedecinRequest;
import com.clinix.clinic.dto.response.MedecinResponse;
import com.clinix.clinic.exception.ResourceNotFoundException;
import com.clinix.clinic.model.Medecin;
import com.clinix.clinic.repository.MedecinRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MedecinService {

    private final MedecinRepository medecinRepository;

    public Page<MedecinResponse> findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("nom"));
        return medecinRepository.findAll(pageable).map(this::toResponse);
    }

    public Page<MedecinResponse> search(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return medecinRepository.searchByKeyword(keyword, pageable).map(this::toResponse);
    }

    public MedecinResponse findById(Long id) {
        return toResponse(getMedecinOrThrow(id));
    }

    public MedecinResponse create(MedecinRequest request) {
        if (medecinRepository.existsByMatricule(request.getMatricule())) {
            throw new IllegalArgumentException("Matricule déjà utilisé : " + request.getMatricule());
        }
        return toResponse(medecinRepository.save(toEntity(request)));
    }

    public MedecinResponse update(Long id, MedecinRequest request) {
        Medecin medecin = getMedecinOrThrow(id);

        if (!request.getMatricule().equals(medecin.getMatricule())
                && medecinRepository.existsByMatricule(request.getMatricule())) {
            throw new IllegalArgumentException("Matricule déjà utilisé : " + request.getMatricule());
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

    public void delete(Long id) {
        if (!medecinRepository.existsById(id)) {
            throw new ResourceNotFoundException("Médecin", id);
        }
        medecinRepository.deleteById(id);
    }

    private Medecin toEntity(MedecinRequest r) {
        return Medecin.builder()
                .nom(r.getNom())
                .prenom(r.getPrenom())
                .specialite(r.getSpecialite())
                .email(r.getEmail())
                .telephone(r.getTelephone())
                .matricule(r.getMatricule())
                .disponible(r.isDisponible())
                .build();
    }

    private MedecinResponse toResponse(Medecin m) {
        return MedecinResponse.builder()
                .id(m.getId())
                .nom(m.getNom())
                .prenom(m.getPrenom())
                .specialite(m.getSpecialite())
                .email(m.getEmail())
                .telephone(m.getTelephone())
                .matricule(m.getMatricule())
                .disponible(m.isDisponible())
                .build();
    }

    private Medecin getMedecinOrThrow(Long id) {
        return medecinRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Médecin", id));
    }
}
