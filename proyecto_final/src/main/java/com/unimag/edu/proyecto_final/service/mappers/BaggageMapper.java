package com.unimag.edu.proyecto_final.service.mappers;

import com.unimag.edu.proyecto_final.domine.entities.Baggage;
import com.unimag.edu.proyecto_final.api.dto.BaggageDtos.*;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface BaggageMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ticket", ignore = true)
    Baggage toEntity(BaggageCreateRequest request);

    @Mapping(source = "ticket.id",target = "ticketId")
    BaggageResponse toResponse(Baggage baggage);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(BaggageUpdateRequest request, @MappingTarget Baggage baggage);
}