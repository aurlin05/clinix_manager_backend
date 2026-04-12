package com.clinix.clinic.dto.response;

import com.clinix.clinic.model.enums.Role;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponse {
    private Long id;
    private String username;
    private Role role;
    private Long medecinId;
    /** Nom complet du médecin lié (si role = MEDECIN) */
    private String medecinNom;
}
