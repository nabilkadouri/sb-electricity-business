package com.hb.cda.electricitybusiness.dto.mapper;

import com.hb.cda.electricitybusiness.dto.ChargingStationRequest;
import com.hb.cda.electricitybusiness.dto.ChargingStationResponse;
import com.hb.cda.electricitybusiness.model.ChargingStation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        uses = {LocationStationMapper.class, UserMapper.class, TimeslotMapper.class})
public interface ChargingStationMapper {


    @Mapping(target = "picture", ignore = true)

    ChargingStationResponse ToResponse(ChargingStation chargingStation);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "locationStation", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "plugType", ignore = true)
    @Mapping(target = "picture", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "isAvailable", ignore = true)
    @Mapping(target = "timeslots", ignore = true)
    ChargingStation convertToEntity(ChargingStationRequest request);


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "locationStation", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "plugType", ignore = true)
    @Mapping(target = "timeslots", ignore = true)
    void updateEntityFromDto(ChargingStationRequest request, @MappingTarget ChargingStation entity);
}
