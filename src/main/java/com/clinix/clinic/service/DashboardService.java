package com.clinix.clinic.service;

import com.clinix.clinic.dto.response.DashboardStatsResponse;
import com.clinix.clinic.dto.response.RdvDistributionResponse;
import com.clinix.clinic.dto.response.TopMedecinResponse;
import com.clinix.clinic.model.enums.StatutRDV;
import com.clinix.clinic.repository.MedecinRepository;
import com.clinix.clinic.repository.PatientRepository;
import com.clinix.clinic.repository.RendezVousRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final PatientRepository patientRepository;
    private final MedecinRepository medecinRepository;
    private final RendezVousRepository rdvRepository;

    @Transactional(readOnly = true)
    public DashboardStatsResponse getStats(Long clinicId, boolean isMedecinRole, Long medecinId) {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay   = startOfDay.plusDays(1).minusNanos(1);

        // Médecin sans profil lié → tout à zéro
        if (isMedecinRole && medecinId == null) {
            return DashboardStatsResponse.builder()
                    .totalPatients(0).totalMedecins(0)
                    .rdvAujourdhui(0).rdvEnAttente(0)
                    .rdvConfirmes(0).rdvAnnules(0).rdvTermines(0)
                    .build();
        }

        // Vue filtrée pour MEDECIN (ses propres RDV)
        if (isMedecinRole) {
            return DashboardStatsResponse.builder()
                    .totalPatients(0).totalMedecins(0)
                    .rdvAujourdhui(rdvRepository.countTodayRdvByMedecin(clinicId, startOfDay, endOfDay, medecinId))
                    .rdvEnAttente(rdvRepository.countByStatutAndMedecinId(clinicId, StatutRDV.EN_ATTENTE, medecinId))
                    .rdvConfirmes(rdvRepository.countByStatutAndMedecinId(clinicId, StatutRDV.CONFIRME, medecinId))
                    .rdvAnnules(rdvRepository.countByStatutAndMedecinId(clinicId, StatutRDV.ANNULE, medecinId))
                    .rdvTermines(rdvRepository.countByStatutAndMedecinId(clinicId, StatutRDV.TERMINE, medecinId))
                    .build();
        }

        // Vue complète pour ADMIN et USER — isolée par clinique
        return DashboardStatsResponse.builder()
                .totalPatients(patientRepository.countByClinicId(clinicId))
                .totalMedecins(medecinRepository.countByClinicId(clinicId))
                .rdvAujourdhui(rdvRepository.countTodayRdv(clinicId, startOfDay, endOfDay))
                .rdvEnAttente(rdvRepository.countByStatutAndClinicId(StatutRDV.EN_ATTENTE, clinicId))
                .rdvConfirmes(rdvRepository.countByStatutAndClinicId(StatutRDV.CONFIRME, clinicId))
                .rdvAnnules(rdvRepository.countByStatutAndClinicId(StatutRDV.ANNULE, clinicId))
                .rdvTermines(rdvRepository.countByStatutAndClinicId(StatutRDV.TERMINE, clinicId))
                .build();
    }

    @Transactional(readOnly = true)
    public List<RdvDistributionResponse> getRdvDistribution(Long clinicId) {
        List<Object[]> results = rdvRepository.getRdvDistributionByStatut(clinicId);
        long totalRdv = rdvRepository.countTotalRdv(clinicId);

        if (totalRdv == 0) {
            return new ArrayList<>();
        }

        return results.stream()
                .map(row -> {
                    StatutRDV statut = (StatutRDV) row[0];
                    long count = ((Number) row[1]).longValue();
                    double percentage = totalRdv > 0 ? (count * 100.0) / totalRdv : 0;
                    return RdvDistributionResponse.builder()
                            .statut(statut.toString())
                            .count(count)
                            .percentage(percentage)
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TopMedecinResponse> getTopMedecins(Long clinicId) {
        List<Object[]> results = medecinRepository.findTop5MedecinsByRdvCount(clinicId);

        return results.stream()
                .map(row -> TopMedecinResponse.builder()
                        .id((Long) row[0])
                        .nom((String) row[1])
                        .prenom((String) row[2])
                        .specialite((String) row[3])
                        .nombreRdv(((Number) row[4]).longValue())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public double getGrowthPercentage(Long clinicId) {
        // Croissance des RDV cette semaine vs semaine dernière
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfThisWeek = now.minusWeeks(1).minusDays(now.getDayOfWeek().getValue() - 1).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfThisWeek = startOfThisWeek.plusWeeks(1).minusNanos(1);

        LocalDateTime startOfLastWeek = startOfThisWeek.minusWeeks(1);
        LocalDateTime endOfLastWeek = startOfThisWeek.minusNanos(1);

        long thisWeekRdv = rdvRepository.countByClinicIdAndDateTimeBetween(clinicId, startOfThisWeek, endOfThisWeek);
        long lastWeekRdv = rdvRepository.countByClinicIdAndDateTimeBetween(clinicId, startOfLastWeek, endOfLastWeek);

        if (lastWeekRdv == 0) {
            return thisWeekRdv > 0 ? 100 : 0;
        }

        return ((thisWeekRdv - lastWeekRdv) * 100.0) / lastWeekRdv;
    }
}
