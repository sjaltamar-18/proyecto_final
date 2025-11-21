
package com.unimag.edu.proyecto_final.service.mappers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unimag.edu.proyecto_final.domine.entities.Bus;
import com.unimag.edu.proyecto_final.api.dto.BusDtos.*;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public interface BusMapper {



    @Mapping(target = "id", ignore = true)
    @Mapping(target = "trips", ignore = true)
    Bus toEntity(BusCreateRequest request);

    @Mapping(target = "status", expression = "java(bus.getStatus() != null ? bus.getStatus().name() : null)")
    BusResponse toResponse(Bus bus);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(BusUpdateRequest request, @MappingTarget Bus bus);
}
