package com.clinix.clinic.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DashboardStatsResponse {
    private long totalPatients;
    private long totalMedecins;
    private long rdvAujourdhui;
    // Champs à plat pour éviter le mapping côté frontend
    private long rdvEnAttente;
    private long rdvConfirmes;
    private long rdvAnnules;
    private long rdvTermines;
}
