package com.clinix.clinic.controller;

import com.clinix.clinic.dto.response.DashboardStatsResponse;
import com.clinix.clinic.dto.response.RdvDistributionResponse;
import com.clinix.clinic.dto.response.TopMedecinResponse;
import com.clinix.clinic.model.User;
import com.clinix.clinic.model.enums.Role;
import com.clinix.clinic.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard", description = "Statistiques globales de la clinique")
@SecurityRequirement(name = "BearerAuth")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/stats")
    @Operation(summary = "Statistiques : totaux, RDV par statut, RDV du jour")
    public ResponseEntity<DashboardStatsResponse> getStats(Authentication auth) {
        User currentUser = (User) auth.getPrincipal();
        Long clinicId = currentUser.getClinicId();
        boolean isMedecinRole = currentUser.getRole() == Role.MEDECIN;
        Long medecinId = isMedecinRole ? currentUser.getMedecinId() : null;
        return ResponseEntity.ok(dashboardService.getStats(clinicId, isMedecinRole, medecinId));
    }

    @GetMapping("/rdv-distribution")
    @Operation(summary = "Répartition des RDV par statut")
    public ResponseEntity<List<RdvDistributionResponse>> getRdvDistribution(Authentication auth) {
        User currentUser = (User) auth.getPrincipal();
        Long clinicId = currentUser.getClinicId();
        List<RdvDistributionResponse> distribution = dashboardService.getRdvDistribution(clinicId);
        if (distribution.isEmpty()) {
            return ResponseEntity.ok(List.of(RdvDistributionResponse.builder()
                    .statut("Aucune donnée disponible")
                    .count(0)
                    .percentage(0)
                    .build()));
        }
        return ResponseEntity.ok(distribution);
    }

    @GetMapping("/top-medecins")
    @Operation(summary = "Top 5 des médecins avec le plus de RDV")
    public ResponseEntity<List<TopMedecinResponse>> getTopMedecins(Authentication auth) {
        User currentUser = (User) auth.getPrincipal();
        Long clinicId = currentUser.getClinicId();
        List<TopMedecinResponse> topMedecins = dashboardService.getTopMedecins(clinicId);
        return ResponseEntity.ok(topMedecins);
    }

    @GetMapping("/growth")
    @Operation(summary = "Croissance des RDV (cette semaine vs semaine dernière)")
    public ResponseEntity<Double> getGrowth(Authentication auth) {
        User currentUser = (User) auth.getPrincipal();
        Long clinicId = currentUser.getClinicId();
        return ResponseEntity.ok(dashboardService.getGrowthPercentage(clinicId));
    }
}
