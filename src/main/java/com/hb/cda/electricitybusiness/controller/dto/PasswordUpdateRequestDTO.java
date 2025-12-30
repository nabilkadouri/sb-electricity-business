package com.hb.cda.electricitybusiness.controller.dto;

import lombok.Data;

@Data
public class PasswordUpdateRequestDTO {
    private String oldPassword;
    private String newPassword;
}
