package com.unimag.edu.proyecto_final.api.dto;

import com.unimag.edu.proyecto_final.domine.entities.enumera.EntityType;
import com.unimag.edu.proyecto_final.domine.entities.enumera.Type;

import java.io.Serializable;
import java.time.LocalDateTime;

public class IncidentDtos {
    public record IncidentCreateRequest(EntityType entityType, Long entityId, Type type, String note) implements Serializable {}
    public record IncidentResponse(Long id, EntityType entityType, Long entityId, Type type, String note, LocalDateTime createdAt) implements Serializable {}
    public record IncidentUpdateRequest(String note, String type) implements Serializable {}
}
