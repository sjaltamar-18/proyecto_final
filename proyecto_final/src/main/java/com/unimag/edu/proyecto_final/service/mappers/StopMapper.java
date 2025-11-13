package com.unimag.edu.proyecto_final.service.mappers;

import com.unimag.edu.proyecto_final.api.dto.StopDtos.*;
import com.unimag.edu.proyecto_final.domine.entities.Stop;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface StopMapper {
    @Mapping(target = "id", ignore = true)
    Stop toEntity(StopCreateRequest request);
    StopResponse toResponse(Stop stop);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(StopUpdateRequest request, @MappingTarget Stop stop);
}
