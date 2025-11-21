
package com.unimag.edu.proyecto_final.service.mappers;

import com.unimag.edu.proyecto_final.api.dto.StopDtos.*;
import com.unimag.edu.proyecto_final.domine.entities.Stop;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface StopMapper {


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "route", ignore = true)
    @Mapping(source = "name", target = "stopName")
    @Mapping(source = "order", target = "stopOrder")
    @Mapping(source = "lat", target = "latitude")
    @Mapping(source = "lng", target = "longitude")
    Stop toEntity(StopCreateRequest request);


    @Mapping(target = "routeId", expression = "java(stop.getRoute().getId())")
    @Mapping(source = "stopName", target = "name")
    @Mapping(source = "stopOrder", target = "order")
    @Mapping(source = "latitude", target = "lat")
    @Mapping(source = "longitude", target = "lng")
    StopResponse toResponse(Stop stop);


    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "route", ignore = true)   // no se toca route
    @Mapping(source = "name", target = "stopName")
    @Mapping(source = "order", target = "stopOrder")
    @Mapping(source = "lat", target = "latitude")
    @Mapping(source = "lng", target = "longitude")
    void updateEntityFromDto(StopUpdateRequest request, @MappingTarget Stop stop);
}
