package com.clinix.clinic.controller;

import com.clinix.clinic.dto.request.RendezVousRequest;
import com.clinix.clinic.dto.response.RendezVousResponse;
import com.clinix.clinic.model.enums.StatutRDV;
import com.clinix.clinic.service.RendezVousService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rdv")
@RequiredArgsConstructor
@Tag(name = "Rendez-vous", description = "Gestion des rendez-vous")
@SecurityRequirement(name = "BearerAuth")
public class RendezVousController {

    private final RendezVousService rdvService;

    // GET /api/rdv?page=0&size=10
    // GET /api/rdv?statut=EN_ATTENTE&medecinId=1&page=0
    @GetMapping
    @Operation(summary = "Liste paginée des rendez-vous avec filtres optionnels")
    public ResponseEntity<Page<RendezVousResponse>> findAll(
            @RequestParam(required = false) StatutRDV statut,
            @RequestParam(required = false) Long medecinId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(rdvService.findAll(statut, medecinId, page, size));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un rendez-vous par ID")
    public ResponseEntity<RendezVousResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(rdvService.findById(id));
    }

    @PostMapping
    @Operation(summary = "Créer un nouveau rendez-vous")
    public ResponseEntity<RendezVousResponse> create(@Valid @RequestBody RendezVousRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(rdvService.create(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour un rendez-vous")
    public ResponseEntity<RendezVousResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody RendezVousRequest request) {
        return ResponseEntity.ok(rdvService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un rendez-vous")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        rdvService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
