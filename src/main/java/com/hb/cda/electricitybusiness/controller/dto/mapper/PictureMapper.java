package com.hb.cda.electricitybusiness.controller.dto.mapper;

import com.hb.cda.electricitybusiness.controller.dto.PictureDetailsDTO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PictureMapper {

    @Named("toFullUrl")
    default PictureDetailsDTO toFullUrl(PictureDetailsDTO picture) {

        if (picture == null || picture.getSrc() == null) {
            return null;
        }

        String src = picture.getSrc();
        String fullUrl;

        if (src.startsWith("images/") || src.startsWith("uploads/")) {
            fullUrl = ServletUriComponentsBuilder
                    .fromCurrentContextPath()
                    .path("/")
                    .path(src)
                    .toUriString();
        }
        else if (src.startsWith("http")) {
            fullUrl = src;
        }
        else {
            fullUrl = ServletUriComponentsBuilder
                    .fromCurrentContextPath()
                    .path("/")
                    .path(src)
                    .toUriString();
        }

        return new PictureDetailsDTO(
                picture.getAlt(),
                fullUrl,
                picture.isMain()
        );
    }
}
