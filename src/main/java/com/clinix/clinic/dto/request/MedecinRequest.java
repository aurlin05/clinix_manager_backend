package com.clinix.clinic.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class MedecinRequest {

    @NotBlank(message = "Le nom est obligatoire")
    private String nom;

    @NotBlank(message = "Le prénom est obligatoire")
    private String prenom;

    @NotBlank(message = "La spécialité est obligatoire")
    private String specialite;

    @Email(message = "Format email invalide")
    private String email;

    @Pattern(regexp = "^[0-9+\\-\\s]{8,15}$", message = "Format téléphone invalide")
    private String telephone;

    @NotBlank(message = "Le matricule est obligatoire")
    private String matricule;

    private boolean disponible = true;
}
