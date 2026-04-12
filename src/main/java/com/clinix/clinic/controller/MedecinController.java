package com.clinix.clinic.controller;

import com.clinix.clinic.dto.request.MedecinRequest;
import com.clinix.clinic.dto.response.MedecinResponse;
import com.clinix.clinic.model.User;
import com.clinix.clinic.service.MedecinService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/medecins")
@RequiredArgsConstructor
@Tag(name = "Médecins", description = "Gestion des médecins")
@SecurityRequirement(name = "BearerAuth")
public class MedecinController {

    private final MedecinService medecinService;

    @GetMapping
    @Operation(summary = "Liste paginée de tous les médecins")
    public ResponseEntity<Page<MedecinResponse>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication auth) {
        Long clinicId = ((User) auth.getPrincipal()).getClinicId();
        return ResponseEntity.ok(medecinService.findAll(clinicId, page, size));
    }

    @GetMapping("/search")
    @Operation(summary = "Rechercher des médecins (nom, spécialité, matricule)")
    public ResponseEntity<Page<MedecinResponse>> search(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication auth) {
        Long clinicId = ((User) auth.getPrincipal()).getClinicId();
        return ResponseEntity.ok(medecinService.search(clinicId, keyword, page, size));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un médecin par ID")
    public ResponseEntity<MedecinResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(medecinService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Créer un nouveau médecin")
    public ResponseEntity<MedecinResponse> create(
            @Valid @RequestBody MedecinRequest request,
            Authentication auth) {
        Long clinicId = ((User) auth.getPrincipal()).getClinicId();
        return ResponseEntity.status(HttpStatus.CREATED).body(medecinService.create(clinicId, request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Mettre à jour un médecin")
    public ResponseEntity<MedecinResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody MedecinRequest request,
            Authentication auth) {
        Long clinicId = ((User) auth.getPrincipal()).getClinicId();
        return ResponseEntity.ok(medecinService.update(clinicId, id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Supprimer un médecin")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        medecinService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
