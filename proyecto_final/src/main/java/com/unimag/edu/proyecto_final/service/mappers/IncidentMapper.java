package com.unimag.edu.proyecto_final.service.mappers;

import com.unimag.edu.proyecto_final.api.dto.IncidentDtos.*;
import com.unimag.edu.proyecto_final.domine.entities.Incident;
import com.unimag.edu.proyecto_final.domine.entities.enumera.EntityType;
import com.unimag.edu.proyecto_final.domine.entities.enumera.Type;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface IncidentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "entityType", expression = "java(mapEntityType(request.entityType()))")
    @Mapping(target = "type", expression = "java(mapType(request.type()))")
    @Mapping(target = "creationDate", expression = "java(java.time.LocalDateTime.now())")
    Incident toEntity(IncidentCreateRequest request);

    @Mapping(target = "entityType", expression = "java(incident.getEntityType() != null ? incident.getEntityType().name() : null)")
    @Mapping(target = "type", expression = "java(incident.getType() != null ? incident.getType().name() : null)")
    @Mapping(source = "creationDate", target = "createdAt")
    IncidentResponse toResponse(Incident incident);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "type", expression = "java(request.type() != null ? mapType(request.type()) : incident.getType())")
    void updateEntityFromDto(IncidentUpdateRequest request, @MappingTarget Incident incident);



    default EntityType mapEntityType(String value) {
        if (value == null) return null;
        try {
            return EntityType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException ex) {
            return EntityType.TRIP;
        }
    }

    default Type mapType(String value) {
        if (value == null) return null;
        try {
            return Type.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException ex) {
            return Type.SECURITY;
        }
    }
}