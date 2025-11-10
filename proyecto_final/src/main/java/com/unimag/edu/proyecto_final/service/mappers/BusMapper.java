package com.unimag.edu.proyecto_final.service.mappers;

import com.unimag.edu.proyecto_final.domine.entities.Bus;
import com.unimag.edu.proyecto_final.api.dto.BusDtos.*;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface BusMapper {

    @Mapping(target = "id", ignore = true)
    Bus toEntity(BusCreateRequest request);

    BusResponse toResponse(Bus bus);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(BusUpdateRequest request, @MappingTarget Bus bus);
}
