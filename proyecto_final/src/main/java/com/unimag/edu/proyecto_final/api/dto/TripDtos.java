package com.unimag.edu.proyecto_final.api.dto;

import com.unimag.edu.proyecto_final.domine.entities.enumera.BoardingStatus;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class TripDtos {
    public record TripCreateRequest(Long routeId, Long busId, LocalDate date, LocalDateTime departureAt, LocalDateTime arrivalEta, BoardingStatus boardingStatus) implements Serializable {}
    public record TripResponse(Long id, Long routeId, Long busId, LocalDate date, LocalDateTime departureAt, LocalDateTime arrivalEta, LocalDateTime departureReal, String status,BoardingStatus boardingStatus) implements Serializable {}
    public record TripUpdateRequest(LocalDateTime departureAt, LocalDateTime arrivalEta, String status) implements Serializable {}
}