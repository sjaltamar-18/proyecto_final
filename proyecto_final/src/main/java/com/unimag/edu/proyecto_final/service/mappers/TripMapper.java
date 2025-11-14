package com.unimag.edu.proyecto_final.service.mappers;

import com.unimag.edu.proyecto_final.api.dto.TripDtos.*;
import com.unimag.edu.proyecto_final.domine.entities.Bus;
import com.unimag.edu.proyecto_final.domine.entities.Route;
import com.unimag.edu.proyecto_final.domine.entities.Trip;
import com.unimag.edu.proyecto_final.domine.entities.enumera.StatusTrip;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface TripMapper {


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "route", expression = "java(toRoute(request.routeId()))")
    @Mapping(target = "bus", expression = "java(toBus(request.busId()))")
    @Mapping(target = "departureAt", source = "departureAt")
    @Mapping(target = "arrivalAt", source = "arrivalEta")
    Trip toEntity(TripCreateRequest request);


    @Mapping(target = "status", expression = "java(trip.getStatusTrip() != null ? trip.getStatusTrip().name() : null)")
    @Mapping(source = "route.id", target = "routeId")
    @Mapping(source = "bus.id", target = "busId")
    @Mapping(source = "arrivalAt", target = "arrivalEta")
    TripResponse toResponse(Trip trip);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "statusTrip", expression = "java(request.status() != null ? mapStatus(request.status()) : trip.getStatusTrip())")
    void updateEntityFromStatusRequest(TripUpdateRequest request, @MappingTarget Trip trip);

    default StatusTrip mapStatus(String value) {
        if (value == null) return null;
        try {
            return StatusTrip.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException ex) {
            return StatusTrip.SCHEDULED; // valor por defecto
        }
    }
    default Route toRoute(Long id) {
        return id == null ? null : Route.builder().id(id).build();
    }

    default Bus toBus(Long id) {
        return id == null ? null : Bus.builder().id(id).build();
    }
}
