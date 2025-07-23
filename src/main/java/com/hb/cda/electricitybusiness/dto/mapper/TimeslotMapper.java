package com.hb.cda.electricitybusiness.dto.mapper;

import com.hb.cda.electricitybusiness.dto.TimeslotRequest;
import com.hb.cda.electricitybusiness.dto.TimeslotResponse;
import com.hb.cda.electricitybusiness.model.Timeslot;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring", uses = {ChargingStationMapper.class})
public interface TimeslotMapper {

    @Mapping(source = "chargingStation.id", target = "chargingStationId")
    TimeslotResponse toResponse(Timeslot timeslot);

    @Mapping(target = "chargingStation", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isAvailable", constant = "true")
    Timeslot convertToEntity(TimeslotRequest request);

    @Mapping(target = "chargingStation", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isAvailable", ignore = true)
    void updateEntityFromRequest(TimeslotRequest request, @MappingTarget Timeslot timeslot);

    List<Timeslot> convertToEntityList(List<TimeslotRequest> timeslots);
    List<TimeslotResponse> toResponseList(List<Timeslot> timeslots);

}
