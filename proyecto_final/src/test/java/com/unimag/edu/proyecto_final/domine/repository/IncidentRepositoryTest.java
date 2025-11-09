package com.unimag.edu.proyecto_final.domine.repository;

import com.unimag.edu.proyecto_final.domine.entities.Incident;
import com.unimag.edu.proyecto_final.domine.entities.enumera.EntityType;
import com.unimag.edu.proyecto_final.domine.entities.enumera.Type;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class IncidentRepositoryTest extends AbstractRepositoryIT {

    @Autowired
    IncidentRepository incidentRepository;

    @Test
    @DisplayName("Debe encontrar incidentes por tipo de entidad e ID")
    void findByEntityTypeAndEntityId() {
        Incident incident = incidentRepository.save(Incident.builder()
                .entityType(EntityType.TRIP)
                .entityId(10L)
                .type(Type.VEHICLE)
                .creationDate(LocalDateTime.now())
                .build());

        List<Incident> result = incidentRepository.findByEntityTypeAndEntityId(EntityType.TRIP, 10L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getType()).isEqualTo(Type.VEHICLE);
        assertThat(result.get(0).getEntityId()).isEqualTo(10L);
    }

    @Test
    @DisplayName("Debe encontrar incidentes por tipo de incidente")
    void findByType() {
        Incident incident = incidentRepository.save(Incident.builder()
                .entityType(EntityType.TRIP)
                .entityId(10L)
                .type(Type.VEHICLE)
                .creationDate(LocalDateTime.now())
                .build());

        List<Incident> result = incidentRepository.findByType(Type.VEHICLE);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getType()).isEqualTo(Type.VEHICLE);
        assertThat(result.get(0).getEntityType()).isEqualTo(EntityType.TRIP);

    }

    @Test
    @DisplayName("Debe encontrar incidentes entre dos fechas")
    void findBetweenDates() {
        LocalDateTime now = LocalDateTime.now();

        incidentRepository.save(Incident.builder()
                .creationDate(now.minusDays(2))
                .type(Type.DELIVERY_FAIL)
                .entityType(EntityType.TICKET)
                .entityId(1L)
                .build());

        incidentRepository.save(Incident.builder()
                .creationDate(now.minusDays(10))
                .type(Type.DELIVERY_FAIL)
                .entityType(EntityType.TICKET)
                .entityId(2L)
                .build());

        var result = incidentRepository.findBetweenDates(now.minusDays(5), now);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getEntityId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Debe contar incidentes por tipo en un rango de fechas")
    void countByTypeBetween() {
        LocalDateTime now = LocalDateTime.now();

        incidentRepository.save(Incident.builder()
                .creationDate(now.minusDays(1))
                .type(Type.OVERBOOK)
                .entityType(EntityType.TICKET)
                .entityId(1L)
                .build());

        incidentRepository.save(Incident.builder()
                .creationDate(now.minusDays(2))
                .type(Type.OVERBOOK)
                .entityType(EntityType.TICKET)
                .entityId(2L)
                .build());

        Long count = incidentRepository.countByTypeBetween(Type.OVERBOOK, now.minusDays(3), now);

        assertThat(count).isEqualTo(2L);
    }

    @Test
    @DisplayName("Debe encontrar incidentes de un viaje por tipo")
    void findTripIncidentsByType() {
        incidentRepository.save(Incident.builder()
                .entityType(EntityType.TRIP)
                .entityId(99L)
                .type(Type.VEHICLE)
                .creationDate(LocalDateTime.now())
                .build());

        List<Incident> result = incidentRepository.findTripIncidentsByType(99L, Type.VEHICLE);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getEntityType()).isEqualTo(EntityType.TRIP);

    }

    @Test
    @DisplayName("Debe eliminar incidentes más antiguos que la fecha dada")
    void deleteOlderThan() {
        LocalDateTime now = LocalDateTime.now();

        incidentRepository.save(Incident.builder()
                .creationDate(now.minusDays(10))
                .type(Type.VEHICLE)
                .entityType(EntityType.TRIP)
                .entityId(1L)
                .build());

        incidentRepository.save(Incident.builder()
                .creationDate(now.minusDays(1))
                .type(Type.VEHICLE)
                .entityType(EntityType.TRIP)
                .entityId(2L)
                .build());

        int deleted = incidentRepository.deleteOlderThan(now.minusDays(5));

        assertThat(deleted).isEqualTo(1);

    }
}