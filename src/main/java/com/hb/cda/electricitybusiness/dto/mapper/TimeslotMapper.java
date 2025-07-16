package com.hb.cda.electricitybusiness.dto.mapper;

import com.hb.cda.electricitybusiness.dto.TimeslotRequest;
import com.hb.cda.electricitybusiness.dto.TimeslotResponse;
import com.hb.cda.electricitybusiness.model.Timeslot;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {ChargingStationMapper.class})
public interface TimeslotMapper {

    @Mapping(target = "chargingStation", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isAvailable", ignore = true)
    Timeslot toEntity(TimeslotRequest request);

    @Mapping(source = "chargingStation.id", target = "chargingStationId")
    TimeslotResponse toResponse(Timeslot timeslot);

    @Mapping(target = "chargingStation", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isAvailable", ignore = true)
    void updateEntityFromRequest(TimeslotRequest request, @MappingTarget Timeslot timeslot);

}
