package com.unimag.edu.proyecto_final.api.dto;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Map;

public class BusDtos {
    public record BusCreateRequest(String plate, Integer capacity, String amenities) implements Serializable {}
    public record BusResponse(Long id, String plate, Integer capacity, String amenities, String status, LocalDate soatExp, LocalDate revisionExp) implements Serializable {}
    public record BusUpdateRequest(Integer capacity, String amenities, String status) implements Serializable {}
}