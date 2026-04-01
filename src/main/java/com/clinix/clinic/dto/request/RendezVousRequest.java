package com.clinix.clinic.dto.request;

import com.clinix.clinic.model.enums.StatutRDV;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RendezVousRequest {

    @NotNull(message = "La date et heure sont obligatoires")
    @FutureOrPresent(message = "La date doit être présente ou future")
    private LocalDateTime dateHeure;

    private StatutRDV statut = StatutRDV.EN_ATTENTE;

    @NotBlank(message = "Le motif est obligatoire")
    private String motif;

    private String notes;

    @NotNull(message = "L'ID du patient est obligatoire")
    private Long patientId;

    @NotNull(message = "L'ID du médecin est obligatoire")
    private Long medecinId;
}
