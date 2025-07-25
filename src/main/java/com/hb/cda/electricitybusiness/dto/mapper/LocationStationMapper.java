package com.hb.cda.electricitybusiness.dto.mapper;

import com.hb.cda.electricitybusiness.dto.LocationStationRequest;
import com.hb.cda.electricitybusiness.dto.LocationStationResponse;
import com.hb.cda.electricitybusiness.model.LocationStation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface LocationStationMapper {


    LocationStationResponse toResponse(LocationStation locationStation);


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "chargingStations", ignore = true)
    LocationStation convertToEntity(LocationStationRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "chargingStations", ignore = true)
    void updateEntityFromDto(LocationStationRequest request, @MappingTarget LocationStation locationStation);

}
