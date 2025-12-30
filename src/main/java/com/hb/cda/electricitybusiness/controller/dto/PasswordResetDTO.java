package com.hb.cda.electricitybusiness.controller.dto;

import lombok.Data;

@Data
public class PasswordResetDTO {
    private String token;
    private String newPassword;
}

