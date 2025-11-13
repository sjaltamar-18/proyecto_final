package com.unimag.edu.proyecto_final.service.mappers;

import com.unimag.edu.proyecto_final.api.dto.AssignmentDtos.*;
import org.mapstruct.*;
import com.unimag.edu.proyecto_final.domine.entities.Assignment;

@Mapper(componentModel = "spring")
public interface AssignmentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "assignedDate", expression = "java(java.time.LocalDateTime.now())")
    Assignment toEntity(AssignmentCreateRequest request);

    AssignmentResponse toResponse(Assignment assignment);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(AssignmentUpdateRequest request, @MappingTarget Assignment assignment);
}