package com.unimag.edu.proyecto_final.api.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

public class SeatHoldDtos {
    public record SeatHoldCreateRequest(Long tripId, String seatNumber, Long userId,Long fromStopId, Long toStopId ) implements Serializable {}
    public record SeatHoldResponse(Long id, Long tripId, String seatNumber, Long userId, LocalDateTime expiresAt, String status,Long fromStopId, Long toStopId) implements Serializable {}
    public record SeatHoldUpdateRequest(String status) implements Serializable {}
}