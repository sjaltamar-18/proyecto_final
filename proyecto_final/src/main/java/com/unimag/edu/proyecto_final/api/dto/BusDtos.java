package com.unimag.edu.proyecto_final.api.dto;

import java.io.Serializable;
import java.util.Map;

public class BusDtos {
    public record BusCreateRequest(String plate, Integer capacity, String amenities) implements Serializable {}
    public record BusResponse(Long id, String plate, Integer capacity, String amenities, String status) implements Serializable {}
    public record BusUpdateRequest(Integer capacity, String amenities, String status) implements Serializable {}
}
