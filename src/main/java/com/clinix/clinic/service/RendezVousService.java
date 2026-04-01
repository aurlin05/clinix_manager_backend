package com.clinix.clinic.service;

import com.clinix.clinic.dto.request.RendezVousRequest;
import com.clinix.clinic.dto.response.RendezVousResponse;
import com.clinix.clinic.exception.ResourceNotFoundException;
import com.clinix.clinic.model.Medecin;
import com.clinix.clinic.model.Patient;
import com.clinix.clinic.model.RendezVous;
import com.clinix.clinic.model.enums.StatutRDV;
import com.clinix.clinic.repository.MedecinRepository;
import com.clinix.clinic.repository.PatientRepository;
import com.clinix.clinic.repository.RendezVousRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RendezVousService {

    private final RendezVousRepository rdvRepository;
    private final PatientRepository patientRepository;
    private final MedecinRepository medecinRepository;

    public Page<RendezVousResponse> findAll(StatutRDV statut, Long medecinId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("dateHeure").descending());
        return rdvRepository.findByFilters(statut, medecinId, pageable).map(this::toResponse);
    }

    public RendezVousResponse findById(Long id) {
        return toResponse(getRdvOrThrow(id));
    }

    public RendezVousResponse create(RendezVousRequest request) {
        Patient patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient", request.getPatientId()));
        Medecin medecin = medecinRepository.findById(request.getMedecinId())
                .orElseThrow(() -> new ResourceNotFoundException("Médecin", request.getMedecinId()));

        RendezVous rdv = RendezVous.builder()
                .dateHeure(request.getDateHeure())
                .statut(request.getStatut() != null ? request.getStatut() : StatutRDV.EN_ATTENTE)
                .motif(request.getMotif())
                .notes(request.getNotes())
                .patient(patient)
                .medecin(medecin)
                .build();

        return toResponse(rdvRepository.save(rdv));
    }

    public RendezVousResponse update(Long id, RendezVousRequest request) {
        RendezVous rdv = getRdvOrThrow(id);

        Patient patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient", request.getPatientId()));
        Medecin medecin = medecinRepository.findById(request.getMedecinId())
                .orElseThrow(() -> new ResourceNotFoundException("Médecin", request.getMedecinId()));

        rdv.setDateHeure(request.getDateHeure());
        rdv.setStatut(request.getStatut());
        rdv.setMotif(request.getMotif());
        rdv.setNotes(request.getNotes());
        rdv.setPatient(patient);
        rdv.setMedecin(medecin);

        return toResponse(rdvRepository.save(rdv));
    }

    public void delete(Long id) {
        if (!rdvRepository.existsById(id)) {
            throw new ResourceNotFoundException("RendezVous", id);
        }
        rdvRepository.deleteById(id);
    }

    private RendezVousResponse toResponse(RendezVous r) {
        return RendezVousResponse.builder()
                .id(r.getId())
                .dateHeure(r.getDateHeure())
                .statut(r.getStatut())
                .motif(r.getMotif())
                .notes(r.getNotes())
                .patientId(r.getPatient().getId())
                .patientNom(r.getPatient().getNom())
                .patientPrenom(r.getPatient().getPrenom())
                .medecinId(r.getMedecin().getId())
                .medecinNom(r.getMedecin().getNom())
                .medecinPrenom(r.getMedecin().getPrenom())
                .medecinSpecialite(r.getMedecin().getSpecialite())
                .build();
    }

    private RendezVous getRdvOrThrow(Long id) {
        return rdvRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RendezVous", id));
    }
}
