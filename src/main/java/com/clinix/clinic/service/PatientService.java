package com.clinix.clinic.service;

import com.clinix.clinic.dto.request.PatientRequest;
import com.clinix.clinic.dto.response.PatientResponse;
import com.clinix.clinic.exception.ResourceNotFoundException;
import com.clinix.clinic.model.Patient;
import com.clinix.clinic.repository.PatientRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;

    public Page<PatientResponse> findAll(Long clinicId, int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return patientRepository.findByClinicId(clinicId, pageable).map(this::toResponse);
    }

    public Page<PatientResponse> search(Long clinicId, String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return patientRepository.searchByKeyword(clinicId, keyword, pageable).map(this::toResponse);
    }

    public Page<PatientResponse> findByMedecin(Long clinicId, Long medecinId, int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return patientRepository.findByMedecinId(clinicId, medecinId, pageable).map(this::toResponse);
    }

    public Page<PatientResponse> searchByMedecin(Long clinicId, Long medecinId, String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return patientRepository.searchByMedecinId(clinicId, medecinId, keyword, pageable).map(this::toResponse);
    }

    public PatientResponse findById(Long id) {
        return toResponse(getPatientOrThrow(id));
    }

    @Transactional
    public PatientResponse create(Long clinicId, PatientRequest request) {
        if (request.getCin() != null && patientRepository.existsByCinAndClinicId(request.getCin(), clinicId)) {
            throw new IllegalArgumentException("Un patient avec le CIN " + request.getCin() + " existe déjà.");
        }
        if (request.getEmail() != null && patientRepository.existsByEmailAndClinicId(request.getEmail(), clinicId)) {
            throw new IllegalArgumentException("Un patient avec l'email " + request.getEmail() + " existe déjà.");
        }
        Patient patient = toEntity(request);
        patient.setClinicId(clinicId);
        return toResponse(patientRepository.save(patient));
    }

    @Transactional
    public PatientResponse update(Long clinicId, Long id, PatientRequest request) {
        Patient patient = getPatientOrThrow(id);

        if (request.getCin() != null && !request.getCin().equals(patient.getCin())
                && patientRepository.existsByCinAndClinicId(request.getCin(), clinicId)) {
            throw new IllegalArgumentException("Un patient avec le CIN " + request.getCin() + " existe déjà.");
        }

        patient.setNom(request.getNom());
        patient.setPrenom(request.getPrenom());
        patient.setDateNaissance(request.getDateNaissance());
        patient.setCin(request.getCin());
        patient.setEmail(request.getEmail());
        patient.setTelephone(request.getTelephone());
        patient.setSexe(request.getSexe());
        patient.setGroupeSanguin(request.getGroupeSanguin());
        patient.setAntecedents(request.getAntecedents());

        return toResponse(patientRepository.save(patient));
    }

    @Transactional
    public void delete(Long id) {
        if (!patientRepository.existsById(id)) {
            throw new ResourceNotFoundException("Patient", id);
        }
        patientRepository.deleteById(id);
    }

    private Patient toEntity(PatientRequest r) {
        return Patient.builder()
                .nom(r.getNom()).prenom(r.getPrenom()).dateNaissance(r.getDateNaissance())
                .cin(r.getCin()).email(r.getEmail()).telephone(r.getTelephone())
                .sexe(r.getSexe()).groupeSanguin(r.getGroupeSanguin()).antecedents(r.getAntecedents())
                .build();
    }

    private PatientResponse toResponse(Patient p) {
        return PatientResponse.builder()
                .id(p.getId()).nom(p.getNom()).prenom(p.getPrenom())
                .dateNaissance(p.getDateNaissance()).cin(p.getCin()).email(p.getEmail())
                .telephone(p.getTelephone()).sexe(p.getSexe())
                .groupeSanguin(p.getGroupeSanguin()).antecedents(p.getAntecedents())
                .build();
    }

    private Patient getPatientOrThrow(Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient", id));
    }
}
