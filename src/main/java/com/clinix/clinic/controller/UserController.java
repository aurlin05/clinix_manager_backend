package com.clinix.clinic.controller;

import com.clinix.clinic.dto.request.UserRequest;
import com.clinix.clinic.dto.response.UserResponse;
import com.clinix.clinic.model.User;
import com.clinix.clinic.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Utilisateurs", description = "Gestion des comptes utilisateurs — réservé à l'admin")
@SecurityRequirement(name = "BearerAuth")
public class UserController {

    private final UserService userService;

    @GetMapping
    @Operation(summary = "Lister tous les utilisateurs de la clinique")
    public ResponseEntity<List<UserResponse>> findAll(Authentication auth) {
        Long clinicId = ((User) auth.getPrincipal()).getClinicId();
        return ResponseEntity.ok(userService.findAll(clinicId));
    }

    @PostMapping
    @Operation(summary = "Créer un utilisateur dans la clinique")
    public ResponseEntity<UserResponse> create(
            @Valid @RequestBody UserRequest request,
            Authentication auth) {
        Long clinicId = ((User) auth.getPrincipal()).getClinicId();
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.create(request, clinicId));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier un utilisateur (rôle, mot de passe)")
    public ResponseEntity<UserResponse> update(@PathVariable Long id,
                                               @Valid @RequestBody UserRequest request,
                                               Authentication auth) {
        Long clinicId = ((User) auth.getPrincipal()).getClinicId();
        return ResponseEntity.ok(userService.update(id, request, clinicId));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un utilisateur")
    public ResponseEntity<Void> delete(@PathVariable Long id, Authentication auth) {
        Long clinicId = ((User) auth.getPrincipal()).getClinicId();
        userService.delete(id, clinicId);
        return ResponseEntity.noContent().build();
    }
}
