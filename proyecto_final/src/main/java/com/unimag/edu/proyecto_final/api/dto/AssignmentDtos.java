package com.unimag.edu.proyecto_final.api.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

public class AssignmentDtos {
    public record AssignmentCreateRequest(Long tripId, Long driverId, Long dispatcherId, boolean checklistOk) implements Serializable {}
    public record AssignmentResponse(Long id, Long tripId, Long driverId, Long dispatcherId, boolean checklistOk, LocalDateTime assignedAt) implements Serializable {}
    public record AssignmentUpdateRequest(boolean checklistOk) implements Serializable {}
}
