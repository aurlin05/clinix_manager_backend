package com.clinix.clinic.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class DashboardStatsResponse {
    private long totalPatients;
    private long totalMedecins;
    private long rdvAujourdhui;
    private Map<String, Long> rdvParStatut;
}
