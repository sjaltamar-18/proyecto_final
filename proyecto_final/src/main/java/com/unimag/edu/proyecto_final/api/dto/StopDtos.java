package com.unimag.edu.proyecto_final.api.dto;

import java.io.Serializable;

public class StopDtos {
    public record StopCreateRequest(Long routeId, String name, Integer order, Double lat, Double lng) implements Serializable {}
    public record StopResponse(Long id, Long routeId, String name, Integer order, Double lat, Double lng) implements Serializable {}
    public record StopUpdateRequest(String name, Integer order, Double lat, Double lng) implements Serializable {}
}