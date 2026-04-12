package com.clinix.clinic.repository;

import com.clinix.clinic.model.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PatientRepository extends JpaRepository<Patient, Long> {

    Page<Patient> findByClinicId(Long clinicId, Pageable pageable);

    @Query("SELECT p FROM Patient p WHERE p.clinicId = :clinicId AND (" +
           "LOWER(p.nom) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.prenom) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.cin) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.email) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Patient> searchByKeyword(@Param("clinicId") Long clinicId,
                                  @Param("keyword") String keyword,
                                  Pageable pageable);

    // Patients d'un médecin (via ses rendez-vous), filtrés par clinique
    @Query("SELECT DISTINCT p FROM Patient p JOIN p.rendezVous r " +
           "WHERE p.clinicId = :clinicId AND r.medecin.id = :medecinId")
    Page<Patient> findByMedecinId(@Param("clinicId") Long clinicId,
                                  @Param("medecinId") Long medecinId,
                                  Pageable pageable);

    @Query("SELECT DISTINCT p FROM Patient p JOIN p.rendezVous r " +
           "WHERE p.clinicId = :clinicId AND r.medecin.id = :medecinId AND (" +
           "LOWER(p.nom) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.prenom) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.cin) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.email) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Patient> searchByMedecinId(@Param("clinicId") Long clinicId,
                                    @Param("medecinId") Long medecinId,
                                    @Param("keyword") String keyword,
                                    Pageable pageable);

    long countByClinicId(Long clinicId);

    java.util.Optional<Patient> findByIdAndClinicId(Long id, Long clinicId);

    boolean existsByCinAndClinicId(String cin, Long clinicId);
    boolean existsByEmailAndClinicId(String email, Long clinicId);

    @org.springframework.data.jpa.repository.Modifying
    @Query("UPDATE Patient p SET p.clinicId = :clinicId WHERE p.clinicId IS NULL")
    int assignClinicToUnassigned(@Param("clinicId") Long clinicId);
}
