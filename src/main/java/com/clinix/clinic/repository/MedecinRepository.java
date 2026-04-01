package com.clinix.clinic.repository;

import com.clinix.clinic.model.Medecin;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MedecinRepository extends JpaRepository<Medecin, Long> {

    @Query("SELECT m FROM Medecin m WHERE " +
           "LOWER(m.nom) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(m.prenom) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(m.specialite) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(m.matricule) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Medecin> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    boolean existsByMatricule(String matricule);
    boolean existsByEmail(String email);
}
