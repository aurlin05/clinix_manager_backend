package com.clinix.clinic.service;

import com.clinix.clinic.dto.response.DashboardStatsResponse;
import com.clinix.clinic.model.enums.StatutRDV;
import com.clinix.clinic.repository.MedecinRepository;
import com.clinix.clinic.repository.PatientRepository;
import com.clinix.clinic.repository.RendezVousRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final PatientRepository patientRepository;
    private final MedecinRepository medecinRepository;
    private final RendezVousRepository rdvRepository;

    public DashboardStatsResponse getStats() {
        // Comptage RDV par statut
        Map<String, Long> rdvParStatut = Arrays.stream(StatutRDV.values())
                .collect(Collectors.toMap(
                        Enum::name,
                        rdvRepository::countByStatut
                ));

        // RDV d'aujourd'hui
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay   = startOfDay.plusDays(1).minusNanos(1);
        long rdvAujourdhui = rdvRepository.findTodayRdv(startOfDay, endOfDay).size();

        return DashboardStatsResponse.builder()
                .totalPatients(patientRepository.count())
                .totalMedecins(medecinRepository.count())
                .rdvAujourdhui(rdvAujourdhui)
                .rdvParStatut(rdvParStatut)
                .build();
    }
}
