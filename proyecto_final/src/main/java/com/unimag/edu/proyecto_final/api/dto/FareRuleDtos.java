package com.unimag.edu.proyecto_final.api.dto;

import java.io.Serializable;
import java.util.Map;

public class FareRuleDtos {
    public record FareRuleCreateRequest(Long routeId, Long fromStopId, Long toStopId, Double basePrice,String discounts, Boolean dynamicPricing)
            implements Serializable {}
    public record FareRuleResponse(Long id, Long routeId, Long fromStopId, Long toStopId, Double basePrice, String discounts, Boolean dynamicPricing)
            implements Serializable {}
    public record FareRuleUpdateRequest(Double basePrice, String discounts, Boolean dynamicPricing) implements Serializable {}
}
