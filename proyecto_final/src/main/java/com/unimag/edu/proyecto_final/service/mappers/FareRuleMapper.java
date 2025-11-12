package com.unimag.edu.proyecto_final.service.mappers;

import com.unimag.edu.proyecto_final.domine.entities.FareRule;
import com.unimag.edu.proyecto_final.api.dto.FareRuleDtos.*;
import com.unimag.edu.proyecto_final.domine.entities.enumera.DynamicPricing;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface FareRuleMapper {


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "route", ignore = true)
    @Mapping(target = "fromStop", ignore = true)
    @Mapping(target = "toStop", ignore = true)
    @Mapping(target = "dynamicPricing", expression = "java(mapDynamicPricing(request.dynamicPricing()))")
    FareRule toEntity(FareRuleCreateRequest request);

    @Mapping(source = "route.id", target = "routeId")
    @Mapping(source = "fromStop.id", target = "fromStopId")
    @Mapping(source = "toStop.id", target = "toStopId")
    @Mapping(target = "dynamicPricing", expression = "java(fareRule.getDynamicPricing() == com.unimag.edu.proyecto_final.domine.entities.enumera.DynamicPricing.ON)")
    FareRuleResponse toResponse(FareRule fareRule);


    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "dynamicPricing", expression = "java(request.dynamicPricing() != null ? mapDynamicPricing(request.dynamicPricing()) : fareRule.getDynamicPricing())")
    void updateEntityFromDto(FareRuleUpdateRequest request, @MappingTarget FareRule fareRule);


    default DynamicPricing mapDynamicPricing(Boolean dynamic) {
        if (dynamic == null) return DynamicPricing.OFF;
        return dynamic ? DynamicPricing.ON : DynamicPricing.OFF;
    }

}
