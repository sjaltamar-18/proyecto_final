package com.unimag.edu.proyecto_final.service.mappers;

import com.unimag.edu.proyecto_final.domine.entities.FareRule;
import com.unimag.edu.proyecto_final.api.dto.FareRuleDtos.*;
import com.unimag.edu.proyecto_final.domine.entities.enumera.DynamicPricing;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface FareRuleMapper {


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "dynamicPricing", expression = "java(mapDynamicPricing(request.dynamicPricing()))")
    FareRule toEntity(FareRuleCreateRequest request);


    @Mapping(target = "dynamicPricing", expression = "java(fareRule.getDynamicPricing() != null ? fareRule.getDynamicPricing().name() : null)")
    FareRuleResponse toResponse(FareRule fareRule);


    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "dynamicPricing", expression = "java(request.dynamicPricing() != null ? mapDynamicPricing(request.dynamicPricing()) : fareRule.getDynamicPricing())")
    void updateEntityFromDto(FareRuleUpdateRequest request, @MappingTarget FareRule fareRule);


    default DynamicPricing mapDynamicPricing(String value) {
        if (value == null) return null;
        try {
            return DynamicPricing.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException ex) {
            return DynamicPricing.OFF;
        }
    }
}
