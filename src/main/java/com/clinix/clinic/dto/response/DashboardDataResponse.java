package com.clinix.clinic.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardDataResponse {
    private DashboardStatsResponse stats;
    private List<RdvDistributionResponse> rdvDistribution;
    private List<TopMedecinResponse> topMedecins;
    private double growthPercentage;
}
