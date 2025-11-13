package com.unimag.edu.proyecto_final.service.mappers;

import com.unimag.edu.proyecto_final.api.dto.SeatHoldDtos.*;
import com.unimag.edu.proyecto_final.domine.entities.SeatHold;
import com.unimag.edu.proyecto_final.domine.entities.enumera.StatusSeatHold;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface SeatHoldMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "trip", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "status", constant = "HOLD")
    @Mapping(target = "expirationDate", expression = "java(java.time.LocalDateTime.now().plusMinutes(10))")
    SeatHold toEntity(SeatHoldCreateRequest request);

    @Mapping(source = "expirationDate", target = "expiresAt")
    @Mapping(source = "status", target = "status", expression = "java(seatHold.getStatus() != null ? seatHold.getStatus().name() : null)")
    @Mapping(source = "trip.id", target = "tripId")
    @Mapping(source = "user.id", target = "userId")
    SeatHoldResponse toResponse(SeatHold seatHold);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "status", expression = "java(request.status() != null ? mapStatus(request.status()) : seatHold.getStatus())")
    void updateEntityFromStatusRequest(SeatHoldUpdateRequest request, @MappingTarget SeatHold seatHold);

    default StatusSeatHold mapStatus(String value) {
        if (value == null) return null;
        try {
            return StatusSeatHold.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException ex) {
            return StatusSeatHold.HOLD;
        }
    }
}
