package com.unimag.edu.proyecto_final.api.dto;

import java.io.Serializable;

public class RouteDtos {
    public record RouteCreateRequest(String code, String name, String origin, String destination, Double distanceKm, Integer durationMin) implements Serializable {}
    public record RouteResponse(Long id, String code, String name, String origin, String destination, Double distanceKm, Integer durationMin) implements Serializable {}
    public record RouteUpdateRequest(String name, String origin, String destination, Double distanceKm, Integer durationMin) implements Serializable {}
}
