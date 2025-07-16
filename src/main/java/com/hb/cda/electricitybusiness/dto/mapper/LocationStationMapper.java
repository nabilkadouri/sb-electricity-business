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

    LocationStation convertToEntity(LocationStationRequest request);

    @Mapping(target = "id", ignore = true) // L'ID n'est pas modifiable via la requête
    @Mapping(target = "chargingStations", ignore = true) // La liste de bornes n'est pas modifiée ici
    void updateEntityFromDto(LocationStationRequest request, @MappingTarget LocationStation locationStation);

}
