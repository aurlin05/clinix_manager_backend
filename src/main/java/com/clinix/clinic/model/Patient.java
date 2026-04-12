package com.clinix.clinic.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "patients")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String prenom;

    @Column(name = "date_naissance")
    private LocalDate dateNaissance;

    @Column(unique = true, length = 20)
    private String cin;

    @Column(unique = true)
    private String email;

    @Column(length = 20)
    private String telephone;

    @Column(length = 10)
    private String sexe;

    @Column(name = "groupe_sanguin", length = 5)
    private String groupeSanguin;

    @Column(columnDefinition = "TEXT")
    private String antecedents;

    @Column(name = "clinic_id")
    private Long clinicId;

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<RendezVous> rendezVous;
}
