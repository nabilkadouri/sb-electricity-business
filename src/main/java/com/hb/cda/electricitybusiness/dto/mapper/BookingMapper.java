package com.hb.cda.electricitybusiness.dto.mapper;

import com.hb.cda.electricitybusiness.dto.BookingRequest;
import com.hb.cda.electricitybusiness.dto.BookingResponse;
import com.hb.cda.electricitybusiness.model.Booking;
import org.mapstruct.Mapper;

import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = {ChargingStationMapper.class, UserMapper.class})
public interface BookingMapper {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "totalAmount", target = "totalAmount")
    BookingResponse ToResponse(Booking booking);

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "chargingStation", ignore = true)
    @Mapping(target = "totalAmount", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "status", ignore = true)
    Booking toEntity(BookingRequest request);

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "chargingStation", ignore = true)
    @Mapping(target= "totalAmount", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "status", ignore = true)
    void updateEntityFromRequest(BookingRequest request, @MappingTarget Booking booking);
}
