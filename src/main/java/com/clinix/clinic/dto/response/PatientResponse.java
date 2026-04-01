package com.clinix.clinic.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class PatientResponse {
    private Long id;
    private String nom;
    private String prenom;
    private LocalDate dateNaissance;
    private String cin;
    private String email;
    private String telephone;
    private String sexe;
    private String groupeSanguin;
    private String antecedents;
}
