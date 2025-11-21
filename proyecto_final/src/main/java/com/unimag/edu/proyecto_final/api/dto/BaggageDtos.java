package com.unimag.edu.proyecto_final.api.dto;

import java.io.Serializable;
import java.math.BigDecimal;

public class BaggageDtos {
    public record BaggageCreateRequest(Long ticketId,Double weightKg, BigDecimal fee, String tagCode) implements Serializable {}
    public record BaggageResponse(Long id, Long ticketId, Double weightKg, BigDecimal fee, String tagCode) implements Serializable {}
    public record BaggageUpdateRequest(Double weight, BigDecimal fee) implements Serializable {}
}
