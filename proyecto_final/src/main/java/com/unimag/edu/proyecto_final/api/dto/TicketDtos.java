package com.unimag.edu.proyecto_final.api.dto;

import java.io.Serializable;

public class TicketDtos {
    public record TicketCreateRequest(Long tripId, Long passengerId, String seatNumber, Long fromStopId, Long toStopId, Double price, String paymentMethod,Boolean approvedByDispatcher) implements Serializable {}
    public record TicketResponse(Long id, Long tripId, Long passengerId, String seatNumber, Long fromStopId, Long toStopId, Double price, String paymentMethod, String status, String qrCode) implements Serializable {}
    public record TicketUpdateRequest(String status, String paymentMethod) implements Serializable {}
}