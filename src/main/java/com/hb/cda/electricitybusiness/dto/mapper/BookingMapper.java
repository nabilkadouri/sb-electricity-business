package com.hb.cda.electricitybusiness.dto.mapper;

import com.hb.cda.electricitybusiness.dto.BookingRequest;
import com.hb.cda.electricitybusiness.dto.BookingResponse;
import com.hb.cda.electricitybusiness.model.Booking;
import org.mapstruct.*;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface BookingMapper {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "chargingStation.id", target = "chargingStationId")
    BookingResponse ToResponse(Booking booking);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "totalAmount", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "chargingStation", ignore = true)
    Booking convertToEntity(BookingRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "totalAmount", ignore = true)
    @Mapping(target = "user",ignore = true)
    @Mapping(target = "chargingStation", ignore = true)
    void updateEntityFromRequest(BookingRequest request, @MappingTarget Booking booking);

}
