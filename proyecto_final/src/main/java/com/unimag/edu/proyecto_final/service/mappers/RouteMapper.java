package com.unimag.edu.proyecto_final.service.mappers;

import com.unimag.edu.proyecto_final.api.dto.RouteDtos.*;
import com.unimag.edu.proyecto_final.domine.entities.Route;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface RouteMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "code", target = "routeCode")
    @Mapping(source = "name", target = "routeName")
    @Mapping(source = "origin", target = "originName")
    @Mapping(source = "destination", target = "destinationName")
    @Mapping(source = "distanceKm", target = "distance")
    @Mapping(source = "durationMin", target = "time")
    Route toEntity(RouteCreateRequest request);

    @Mapping(source = "routeCode", target = "code")
    @Mapping(source = "routeName", target = "name")
    @Mapping(source = "originName", target = "origin")
    @Mapping(source = "destinationName", target = "destination")
    @Mapping(source = "distance", target = "distanceKm")
    @Mapping(source = "time", target = "durationMin")
    RouteResponse toResponse(Route route);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(source = "name", target = "routeName")
    @Mapping(source = "origin", target = "originName")
    @Mapping(source = "destination", target = "destinationName")
    @Mapping(source = "distanceKm", target = "distance")
    @Mapping(source = "durationMin", target = "time")
    void updateEntityFromDto(RouteUpdateRequest request, @MappingTarget Route route);
}