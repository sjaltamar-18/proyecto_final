package com.unimag.edu.proyecto_final.api.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

public class IncidentDtos {
    public record IncidentCreateRequest(String entityType, Long entityId, String type, String note) implements Serializable {}
    public record IncidentResponse(Long id, String entityType, Long entityId, String type, String note, LocalDateTime createdAt) implements Serializable {}
    public record IncidentUpdateRequest(String note, String type) implements Serializable {}
}
