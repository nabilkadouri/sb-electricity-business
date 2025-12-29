package com.hb.cda.electricitybusiness.controller.dto.mapper;

import com.hb.cda.electricitybusiness.controller.dto.ChargingStationBookingDTO;
import com.hb.cda.electricitybusiness.controller.dto.ChargingStationRequest;
import com.hb.cda.electricitybusiness.controller.dto.ChargingStationResponse;
import com.hb.cda.electricitybusiness.model.ChargingStation;
import org.mapstruct.*;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        uses = {
                LocationStationMapper.class,
                TimeslotMapper.class,
                PictureMapper.class
        }
)
public interface ChargingStationMapper {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(
            source = "picture",
            target = "picture",
            qualifiedByName = "toFullUrl"
    )
    ChargingStationResponse toResponse(ChargingStation chargingStation);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "plugType", ignore = true)
    @Mapping(target = "timeslots", ignore = true)
    @Mapping(target = "locationStation", ignore = true)
    @Mapping(target = "bookings", ignore = true)
    ChargingStation convertToEntity(ChargingStationRequest request);


    ChargingStationBookingDTO toBookingDTO(ChargingStation station);
}

