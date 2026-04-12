package com.clinix.clinic.repository;

import com.clinix.clinic.model.Medecin;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MedecinRepository extends JpaRepository<Medecin, Long> {

    Page<Medecin> findByClinicId(Long clinicId, Pageable pageable);

    @Query("SELECT m FROM Medecin m WHERE m.clinicId = :clinicId AND (" +
           "LOWER(m.nom) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(m.prenom) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(m.specialite) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(m.matricule) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Medecin> searchByKeyword(@Param("clinicId") Long clinicId,
                                  @Param("keyword") String keyword,
                                  Pageable pageable);

    long countByClinicId(Long clinicId);
    java.util.Optional<Medecin> findByIdAndClinicId(Long id, Long clinicId);
    boolean existsByMatriculeAndClinicId(String matricule, Long clinicId);
    boolean existsByEmailAndClinicId(String email, Long clinicId);

    @org.springframework.data.jpa.repository.Modifying
    @Query("UPDATE Medecin m SET m.clinicId = :clinicId WHERE m.clinicId IS NULL")
    int assignClinicToUnassigned(@Param("clinicId") Long clinicId);

    @Query(value = "SELECT m.id, m.nom, m.prenom, m.specialite, COUNT(r.id) as nombreRdv " +
           "FROM medecin m LEFT JOIN rendez_vous r ON m.id = r.medecin_id " +
           "WHERE m.clinic_id = :clinicId GROUP BY m.id ORDER BY nombreRdv DESC LIMIT 5",
           nativeQuery = true)
    List<Object[]> findTop5MedecinsByRdvCount(@Param("clinicId") Long clinicId);
}
