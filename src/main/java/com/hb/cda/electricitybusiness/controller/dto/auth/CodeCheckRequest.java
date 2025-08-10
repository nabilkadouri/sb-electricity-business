package com.hb.cda.electricitybusiness.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CodeCheckRequest {
    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Le format de l'email est invalide") // Validation du format de l'email
    private String email;

    @NotBlank(message = "Le code est obligatoire")
    @Size(message = "Le code doit contenir 6 chiffres")
    private String codeCheck;
}
