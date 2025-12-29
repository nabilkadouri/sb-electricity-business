package com.hb.cda.electricitybusiness.controller.dto.mapper;

import com.hb.cda.electricitybusiness.controller.dto.RegisterRequest;
import com.hb.cda.electricitybusiness.controller.dto.UserBookingDTO;
import com.hb.cda.electricitybusiness.controller.dto.UserResponse;
import com.hb.cda.electricitybusiness.controller.dto.UserUpdateRequest;
import com.hb.cda.electricitybusiness.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        uses = { PictureMapper.class, ChargingStationMapper.class },
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface UserMapper {

    @Mapping(target = "role", source = "roles")
    @Mapping(
            source = "profilePicture",
            target = "profilePicture",
            qualifiedByName = "toFullUrl"
    )
    UserResponse userToUserResponse(User user);

    User convertToEntity(RegisterRequest dto);

    User updateUserFromRequest(
            UserUpdateRequest userUpdateRequest,
            @MappingTarget User user
    );

    UserBookingDTO toBookingDTO(User user);
}
