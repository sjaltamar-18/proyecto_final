package com.unimag.edu.proyecto_final.service.mappers;

import com.unimag.edu.proyecto_final.domine.entities.Baggage;
import com.unimag.edu.proyecto_final.api.dto.BaggageDtos.*;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface BaggageMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ticket.id", source = "ticketId")
    @Mapping(source = "weightKg", target = "weight")

    Baggage toEntity(BaggageCreateRequest request);

    @Mapping(source = "ticket.id", target = "ticketId")
    @Mapping(source = "weight", target = "weightKg")
    BaggageResponse toResponse(Baggage baggage);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(BaggageUpdateRequest request, @MappingTarget Baggage baggage);
}
