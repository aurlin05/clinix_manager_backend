package com.clinix.clinic.dto.response;

import com.clinix.clinic.model.enums.StatutRDV;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class RendezVousResponse {
    private Long id;
    private LocalDateTime dateHeure;
    private StatutRDV statut;
    private String motif;
    private String notes;
    // Patient info (partiel, pas l'entité entière)
    private Long patientId;
    private String patientNom;
    private String patientPrenom;
    // Médecin info (partiel)
    private Long medecinId;
    private String medecinNom;
    private String medecinPrenom;
    private String medecinSpecialite;
}
