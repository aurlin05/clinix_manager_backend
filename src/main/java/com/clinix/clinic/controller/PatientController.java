package com.clinix.clinic.controller;

import com.clinix.clinic.dto.request.PatientRequest;
import com.clinix.clinic.dto.response.PatientResponse;
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
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
@Tag(name = "Patients", description = "Gestion des patients")
@SecurityRequirement(name = "BearerAuth")
public class PatientController {

    private final PatientService patientService;

    // ── GET /api/patients?page=0&size=10&sortBy=nom ────────────────────────
    @GetMapping
    @Operation(summary = "Liste paginée de tous les patients")
    public ResponseEntity<Page<PatientResponse>> findAll(
            @Parameter(description = "Numéro de page (0-based)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Nombre d'éléments par page")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Champ de tri")
            @RequestParam(defaultValue = "nom") String sortBy) {
        return ResponseEntity.ok(patientService.findAll(page, size, sortBy));
    }

    // ── GET /api/patients/search?keyword=ali&page=0 ───────────────────────
    @GetMapping("/search")
    @Operation(summary = "Rechercher des patients par mot-clé (nom, prénom, CIN, email)")
    public ResponseEntity<Page<PatientResponse>> search(
            @Parameter(description = "Mot-clé de recherche", required = true)
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(patientService.search(keyword, page, size));
    }

    // ── GET /api/patients/{id} ────────────────────────────────────────────
    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un patient par ID")
    public ResponseEntity<PatientResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(patientService.findById(id));
    }

    // ── POST /api/patients ────────────────────────────────────────────────
    @PostMapping
    @Operation(summary = "Créer un nouveau patient")
    public ResponseEntity<PatientResponse> create(@Valid @RequestBody PatientRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(patientService.create(request));
    }

    // ── PUT /api/patients/{id} ────────────────────────────────────────────
    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour un patient")
    public ResponseEntity<PatientResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody PatientRequest request) {
        return ResponseEntity.ok(patientService.update(id, request));
    }

    // ── DELETE /api/patients/{id} ─────────────────────────────────────────
    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un patient")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        patientService.delete(id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }
}
