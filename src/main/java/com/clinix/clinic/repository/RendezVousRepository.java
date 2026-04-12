package com.clinix.clinic.repository;

import com.clinix.clinic.model.RendezVous;
import com.clinix.clinic.model.enums.StatutRDV;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface RendezVousRepository extends JpaRepository<RendezVous, Long> {

    // Filtrage multi-critères isolé par clinique
    @Query("SELECT r FROM RendezVous r WHERE r.clinicId = :clinicId AND " +
           "(:statut IS NULL OR r.statut = :statut) AND " +
           "(:medecinId IS NULL OR r.medecin.id = :medecinId)")
    Page<RendezVous> findByFilters(
            @Param("clinicId") Long clinicId,
            @Param("statut") StatutRDV statut,
            @Param("medecinId") Long medecinId,
            Pageable pageable);

    // Comptages dashboard — vue clinique complète
    @Query("SELECT COUNT(r) FROM RendezVous r WHERE r.clinicId = :clinicId AND r.dateHeure BETWEEN :start AND :end")
    long countTodayRdv(@Param("clinicId") Long clinicId,
                       @Param("start") LocalDateTime start,
                       @Param("end") LocalDateTime end);

    long countByStatutAndClinicId(StatutRDV statut, Long clinicId);

    // Comptages dashboard — vue médecin
    @Query("SELECT COUNT(r) FROM RendezVous r WHERE r.clinicId = :clinicId AND r.medecin.id = :medecinId AND r.dateHeure BETWEEN :start AND :end")
    long countTodayRdvByMedecin(@Param("clinicId") Long clinicId,
                                @Param("start") LocalDateTime start,
                                @Param("end") LocalDateTime end,
                                @Param("medecinId") Long medecinId);

    @Query("SELECT COUNT(r) FROM RendezVous r WHERE r.clinicId = :clinicId AND r.statut = :statut AND r.medecin.id = :medecinId")
    long countByStatutAndMedecinId(@Param("clinicId") Long clinicId,
                                   @Param("statut") StatutRDV statut,
                                   @Param("medecinId") Long medecinId);

    @org.springframework.data.jpa.repository.Modifying
    @Query("UPDATE RendezVous r SET r.clinicId = :clinicId WHERE r.clinicId IS NULL")
    int assignClinicToUnassigned(@Param("clinicId") Long clinicId);

    java.util.Optional<RendezVous> findByIdAndClinicId(Long id, Long clinicId);

    @Query("SELECT r.statut, COUNT(r) FROM RendezVous r WHERE r.clinicId = :clinicId GROUP BY r.statut")
    List<Object[]> getRdvDistributionByStatut(@Param("clinicId") Long clinicId);

    @Query("SELECT COUNT(r) FROM RendezVous r WHERE r.clinicId = :clinicId")
    long countTotalRdv(@Param("clinicId") Long clinicId);

    @Query("SELECT COUNT(r) FROM RendezVous r WHERE r.clinicId = :clinicId AND r.dateHeure BETWEEN :start AND :end")
    long countByClinicIdAndDateTimeBetween(@Param("clinicId") Long clinicId,
                                           @Param("start") LocalDateTime start,
                                           @Param("end") LocalDateTime end);
}
