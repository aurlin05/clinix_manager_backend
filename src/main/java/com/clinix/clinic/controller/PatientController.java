package com.clinix.clinic.controller;

import com.clinix.clinic.dto.request.PatientRequest;
import com.clinix.clinic.dto.response.PatientResponse;
import com.clinix.clinic.model.User;
import com.clinix.clinic.model.enums.Role;
import com.clinix.clinic.service.PatientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
@Tag(name = "Patients", description = "Gestion des patients")
@SecurityRequirement(name = "BearerAuth")
public class PatientController {

    private final PatientService patientService;

    @GetMapping
    @Operation(summary = "Liste paginée de tous les patients")
    public ResponseEntity<Page<PatientResponse>> findAll(
            @Parameter(description = "Numéro de page (0-based)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Nombre d'éléments par page")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Champ de tri")
            @RequestParam(defaultValue = "nom") String sortBy,
            Authentication auth) {
        User currentUser = (User) auth.getPrincipal();
        Long clinicId = currentUser.getClinicId();
        if (currentUser.getRole() == Role.MEDECIN) {
            Long medecinId = currentUser.getMedecinId() != null ? currentUser.getMedecinId() : -1L;
            return ResponseEntity.ok(patientService.findByMedecin(clinicId, medecinId, page, size, sortBy));
        }
        return ResponseEntity.ok(patientService.findAll(clinicId, page, size, sortBy));
    }

    @GetMapping("/search")
    @Operation(summary = "Rechercher des patients par mot-clé (nom, prénom, CIN, email)")
    public ResponseEntity<Page<PatientResponse>> search(
            @Parameter(description = "Mot-clé de recherche", required = true)
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication auth) {
        User currentUser = (User) auth.getPrincipal();
        Long clinicId = currentUser.getClinicId();
        if (currentUser.getRole() == Role.MEDECIN) {
            Long medecinId = currentUser.getMedecinId() != null ? currentUser.getMedecinId() : -1L;
            return ResponseEntity.ok(patientService.searchByMedecin(clinicId, medecinId, keyword, page, size));
        }
        return ResponseEntity.ok(patientService.search(clinicId, keyword, page, size));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un patient par ID")
    public ResponseEntity<PatientResponse> findById(@PathVariable Long id, Authentication auth) {
        Long clinicId = ((User) auth.getPrincipal()).getClinicId();
        return ResponseEntity.ok(patientService.findById(clinicId, id));
    }

    @PostMapping
    @Operation(summary = "Créer un nouveau patient")
    public ResponseEntity<PatientResponse> create(
            @Valid @RequestBody PatientRequest request,
            Authentication auth) {
        Long clinicId = ((User) auth.getPrincipal()).getClinicId();
        return ResponseEntity.status(HttpStatus.CREATED).body(patientService.create(clinicId, request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour un patient")
    public ResponseEntity<PatientResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody PatientRequest request,
            Authentication auth) {
        Long clinicId = ((User) auth.getPrincipal()).getClinicId();
        return ResponseEntity.ok(patientService.update(clinicId, id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un patient")
    public ResponseEntity<Void> delete(@PathVariable Long id, Authentication auth) {
        Long clinicId = ((User) auth.getPrincipal()).getClinicId();
        patientService.delete(clinicId, id);
        return ResponseEntity.noContent().build();
    }
}
