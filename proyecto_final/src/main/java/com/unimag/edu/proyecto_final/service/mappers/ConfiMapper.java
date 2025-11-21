package com.unimag.edu.proyecto_final.service.mappers;

import com.unimag.edu.proyecto_final.domine.entities.Confi;
import com.unimag.edu.proyecto_final.api.dto.ConfiDtos.*;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ConfiMapper {


    @Mapping(target = "key", source = "key")
    @Mapping(target = "value", source = "value")
    Confi toEntity(ConfigCreateRequest request);


    ConfigResponse toResponse(Confi config);


    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(ConfigUpdateRequest request, @MappingTarget Confi config);
}