package com.clinix.clinic.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MedecinResponse {
    private Long id;
    private String nom;
    private String prenom;
    private String specialite;
    private String email;
    private String telephone;
    private String matricule;
    private boolean disponible;
}
