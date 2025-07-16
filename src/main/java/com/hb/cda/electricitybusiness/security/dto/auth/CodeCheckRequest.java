package com.hb.cda.electricitybusiness.security.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CodeCheckRequest {
    @NotBlank(message = "Le code est obligatoire")
    @Size(message = "Le code doit contenir 6 chiffres")
    private String codeCheck;
}
