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

    /** Optionnel : créer un compte d'accès lié à ce médecin */
    @Size(min = 3, max = 50, message = "Le nom d'utilisateur doit avoir entre 3 et 50 caractères")
    private String username;

    @Size(min = 6, message = "Le mot de passe doit avoir au moins 6 caractères")
    private String password;
}
