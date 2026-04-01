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

    // Filtrage multi-critères (statut et/ou medecinId)
    @Query("SELECT r FROM RendezVous r WHERE " +
           "(:statut IS NULL OR r.statut = :statut) AND " +
           "(:medecinId IS NULL OR r.medecin.id = :medecinId)")
    Page<RendezVous> findByFilters(
            @Param("statut") StatutRDV statut,
            @Param("medecinId") Long medecinId,
            Pageable pageable);

    // RDV d'aujourd'hui
    @Query("SELECT r FROM RendezVous r WHERE r.dateHeure BETWEEN :start AND :end")
    List<RendezVous> findTodayRdv(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    // Comptage par statut (pour le dashboard)
    long countByStatut(StatutRDV statut);
}
