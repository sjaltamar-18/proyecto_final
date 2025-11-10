package com.unimag.edu.proyecto_final.service.mappers;

import com.unimag.edu.proyecto_final.api.dto.RouteDtos.*;
import com.unimag.edu.proyecto_final.domine.entities.Route;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface RouteMapper {


    @Mapping(target = "id", ignore = true)
    Route toEntity(RouteCreateRequest request);

    RouteResponse toResponse(Route route);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(RouteUpdateRequest request, @MappingTarget Route route);
}
