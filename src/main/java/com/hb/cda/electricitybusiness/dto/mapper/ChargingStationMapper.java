package com.hb.cda.electricitybusiness.dto.mapper;

import com.hb.cda.electricitybusiness.dto.ChargingStationRequest;
import com.hb.cda.electricitybusiness.dto.ChargingStationResponse;
import com.hb.cda.electricitybusiness.dto.PictureDetailsDTO;
import com.hb.cda.electricitybusiness.model.ChargingStation;
import org.mapstruct.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        uses = {LocationStationMapper.class, UserMapper.class, TimeslotMapper.class, BookingMapper.class})
public interface ChargingStationMapper {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(target = "picture", expression = "java(mapPictureDetailsToFullUrl(chargingStation.getPicture()))")
    ChargingStationResponse ToResponse(ChargingStation chargingStation);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "plugType", ignore = true)
    @Mapping(target = "picture", ignore = true)
    @Mapping(target = "timeslots", ignore = true)
    @Mapping(target = "locationStation", ignore = true)
    @Mapping(target = "bookings", ignore = true)
    ChargingStation convertToEntity(ChargingStationRequest request);


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "plugType", ignore = true)
    @Mapping(target = "picture", ignore = true)
    @Mapping(target = "locationStation", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "timeslots", ignore = true)
    @Mapping(target = "bookings", ignore = true)
    void updateEntityFromDto(ChargingStationRequest request, @MappingTarget ChargingStation entity);

    default PictureDetailsDTO mapPictureDetailsToFullUrl(PictureDetailsDTO pictureDetails) {
        if (pictureDetails == null) {
            return null;
        }

        String src = pictureDetails.getSrc();
        String fullSrc;


        if (src != null && src.startsWith("images/default_")) {
            fullSrc = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/images/")
                    .path(src.substring(src.lastIndexOf('/') + 1))
                    .toUriString();
        } else {
            fullSrc = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/uploads/")
                    .path(src)
                    .toUriString();
        }

        return new PictureDetailsDTO(pictureDetails.getAlt(), fullSrc, pictureDetails.isMain());
    }
}
