package com.clinix.clinic.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "medecins", uniqueConstraints = {
    @UniqueConstraint(name = "uk_medecin_clinic_matricule", columnNames = {"clinic_id", "matricule"}),
    @UniqueConstraint(name = "uk_medecin_clinic_email", columnNames = {"clinic_id", "email"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Medecin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String prenom;

    @Column(nullable = false)
    private String specialite;

    @Column
    private String email;

    @Column(length = 20)
    private String telephone;

    @Column(length = 20)
    private String matricule;

    @Column(nullable = false)
    @Builder.Default
    private boolean disponible = true;

    @Column(name = "clinic_id")
    private Long clinicId;

    @OneToMany(mappedBy = "medecin", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<RendezVous> rendezVous;
}
