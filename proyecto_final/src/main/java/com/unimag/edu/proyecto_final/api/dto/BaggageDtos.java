package com.unimag.edu.proyecto_final.api.dto;

import java.io.Serializable;

public class BaggageDtos {
    public record BaggageCreateRequest(Long ticketId,Double weightKg, Double fee, String tagCode) implements Serializable {}
    public record BaggageResponse(Long id, Long ticketId, Double weightKg, Double fee, String tagCode) implements Serializable {}
    public record BaggageUpdateRequest(Double weightKg, Double fee) implements Serializable {}
}
