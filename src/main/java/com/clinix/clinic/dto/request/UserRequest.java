package com.clinix.clinic.dto.request;

import com.clinix.clinic.model.enums.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRequest {

    @NotBlank(message = "Le nom d'utilisateur est obligatoire")
    @Size(min = 3, max = 50)
    private String username;

    /** Obligatoire à la création, ignoré à la mise à jour si vide */
    @Size(min = 6, message = "Le mot de passe doit avoir au moins 6 caractères")
    private String password;

    @NotNull(message = "Le rôle est obligatoire")
    private Role role;

    /** Requis si role = MEDECIN */
    private Long medecinId;
}
