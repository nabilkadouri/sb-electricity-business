package com.hb.cda.electricitybusiness.dto;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class PictureDetailsDTO {
    private String alt;
    private String src;
    private boolean main;


}
