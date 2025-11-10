package com.unimag.edu.proyecto_final.service.mappers;

import com.unimag.edu.proyecto_final.api.dto.TripDtos.*;
import com.unimag.edu.proyecto_final.domine.entities.Trip;
import com.unimag.edu.proyecto_final.domine.entities.enumera.StatusTrip;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface TripMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", constant = "SCHEDULED")
    Trip toEntity(TripCreateRequest request);

    @Mapping(target = "status", expression = "java(trip.getStatus() != null ? trip.getStatus().name() : null)")
    TripResponse toResponse(Trip trip);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "status", expression = "java(request.status() != null ? mapStatus(request.status()) : trip.getStatus())")
    void updateEntityFromStatusRequest(TripUpdateRequest request, @MappingTarget Trip trip);

    default StatusTrip mapStatus(String value) {
        if (value == null) return null;
        try {
            return StatusTrip.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException ex) {
            return StatusTrip.SCHEDULED; // valor por defecto
        }
    }
}
