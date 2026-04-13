package com.clinix.clinic.repository;

import com.clinix.clinic.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    java.util.List<User> findByClinicId(Long clinicId);
    java.util.List<User> findByClinicIdIsNull();

    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.data.jpa.repository.Query("UPDATE User u SET u.clinicId = :clinicId WHERE u.clinicId IS NULL")
    int assignClinicToUnassigned(@Param("clinicId") Long clinicId);
}
