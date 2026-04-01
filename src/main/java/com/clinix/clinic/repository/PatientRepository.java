package com.clinix.clinic.repository;

import com.clinix.clinic.model.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PatientRepository extends JpaRepository<Patient, Long> {

    @Query("SELECT p FROM Patient p WHERE " +
           "LOWER(p.nom) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.prenom) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.cin) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.email) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Patient> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    boolean existsByCin(String cin);
    boolean existsByEmail(String email);
}
