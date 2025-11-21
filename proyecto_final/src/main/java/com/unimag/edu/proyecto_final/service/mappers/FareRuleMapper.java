
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
    @Mapping(target = "discountPrice", source = "discounts")
    @Mapping(target = "dynamicPricing", qualifiedByName = "booleanToDynamic")
    FareRule toEntity(FareRuleCreateRequest request);


    @Mapping(source = "route.id", target = "routeId")
    @Mapping(source = "fromStop.id", target = "fromStopId")
    @Mapping(source = "toStop.id", target = "toStopId")
    @Mapping(target = "discounts", source = "discountPrice")
    @Mapping(target = "dynamicPricing", qualifiedByName = "dynamicToBoolean")
    FareRuleResponse toResponse(FareRule fareRule);


    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "discountPrice", source = "discounts")
    @Mapping(target = "dynamicPricing", qualifiedByName = "booleanToDynamic")
    void updateEntityFromDto(FareRuleUpdateRequest request, @MappingTarget FareRule fareRule);


    @Named("booleanToDynamic")
    default DynamicPricing booleanToDynamic(Boolean dynamic) {
        if (dynamic == null) return DynamicPricing.OFF;
        return dynamic ? DynamicPricing.ON : DynamicPricing.OFF;
    }


    @Named("dynamicToBoolean")
    default Boolean dynamicToBoolean(DynamicPricing dynamic) {
        return dynamic == DynamicPricing.ON;
    }
}
