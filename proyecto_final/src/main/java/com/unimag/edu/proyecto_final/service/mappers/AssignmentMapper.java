package com.unimag.edu.proyecto_final.service.mappers;

import com.unimag.edu.proyecto_final.api.dto.AssignmentDtos.*;
import org.mapstruct.*;
import org.mapstruct.ap.internal.model.common.Assignment;

@Mapper(componentModel = "spring")
public interface AssignmentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "assignedAt", expression = "java(java.time.LocalDateTime.now())")
    Assignment toEntity(AssignmentCreateRequest request);

    AssignmentResponse toResponse(Assignment assignment);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(AssignmentUpdateRequest request, @MappingTarget Assignment assignment);
}