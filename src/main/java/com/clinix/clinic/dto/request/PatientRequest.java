package com.clinix.clinic.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PatientRequest {

    @NotBlank(message = "Le nom est obligatoire")
    private String nom;

    @NotBlank(message = "Le prénom est obligatoire")
    private String prenom;

    private LocalDate dateNaissance;

    @Pattern(regexp = "^(?i)[A-Z]{1,2}[0-9]{6}$", message = "Format CIN invalide (ex: AB123456)")
    private String cin;

    @Email(message = "Format email invalide")
    private String email;

    @Pattern(regexp = "^[0-9+\\-\\s]{8,15}$", message = "Format téléphone invalide")
    private String telephone;

    @Pattern(regexp = "^(M|F|Masculin|Féminin)$", message = "Sexe doit être M ou F")
    private String sexe;

    @Pattern(regexp = "^(A|B|AB|O)[+-]$", message = "Groupe sanguin invalide (ex: A+, O-)")
    private String groupeSanguin;

    private String antecedents;
}
