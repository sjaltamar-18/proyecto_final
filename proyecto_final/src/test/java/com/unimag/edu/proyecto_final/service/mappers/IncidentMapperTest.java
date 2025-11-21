package com.unimag.edu.proyecto_final.service.mappers;

import com.unimag.edu.proyecto_final.api.dto.IncidentDtos.*;
import com.unimag.edu.proyecto_final.domine.entities.Incident;
import com.unimag.edu.proyecto_final.domine.entities.enumera.EntityType;
import com.unimag.edu.proyecto_final.domine.entities.enumera.Type;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class IncidentMapperTest {

    private final IncidentMapper mapper = Mappers.getMapper(IncidentMapper.class);

    @Test
    void toEntity_shouldMapCreateRequest() {

        var request = new IncidentCreateRequest(
                "TRIP",              // entityType
                1L,                  // entityId
                "DELIVERY_FAIL",     // type
                "El paquete no fue entregado por error de dirección" // note
        );

        Incident entity = mapper.toEntity(request);


        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isNull(); // ignored by mapper
        assertThat(entity.getEntityType()).isEqualTo(EntityType.TRIP);
        assertThat(entity.getEntityId()).isEqualTo(1L);
        assertThat(entity.getType()).isEqualTo(Type.DELIVERY_FAIL);
        assertThat(entity.getNote()).isEqualTo("El paquete no fue entregado por error de dirección");
        assertThat(entity.getCreationDate()).isNotNull();
        assertThat(entity.getCreationDate()).isBeforeOrEqualTo(LocalDateTime.now());
    }

    @Test
    void toResponse_shouldMapEntity() {

        var entity = Incident.builder()
                .id(10L)
                .entityType(EntityType.PARCEL)
                .entityId(5L)
                .type(Type.SECURITY)
                .note("Intento de apertura de puerta no autorizado")
                .creationDate(LocalDateTime.now().minusHours(2))
                .build();


        IncidentResponse dto = mapper.toResponse(entity);


        assertThat(dto).isNotNull();
        assertThat(dto.id()).isEqualTo(10L);
        assertThat(dto.entityType()).isEqualTo("PARCEL");
        assertThat(dto.entityId()).isEqualTo(5L);
        assertThat(dto.type()).isEqualTo("SECURITY");
        assertThat(dto.note()).contains("no autorizado");
        assertThat(dto.createdAt()).isBeforeOrEqualTo(LocalDateTime.now());
    }

    @Test
    void updateEntityFromDto_shouldUpdateNonNullFields() {
        // given
        var entity = Incident.builder()
                .id(20L)
                .entityType(EntityType.TICKET)
                .entityId(99L)
                .type(Type.SECURITY)
                .note("Anomalía detectada en taquilla")
                .creationDate(LocalDateTime.now().minusDays(1))
                .build();

        var update = new IncidentUpdateRequest("Reasignación de asiento por sobreventa","OVERBOOK");

        // when
        mapper.updateEntityFromDto(update, entity);

        // then
        assertThat(entity.getId()).isEqualTo(20L);
        assertThat(entity.getEntityType()).isEqualTo(EntityType.TICKET); // no cambia
        assertThat(entity.getType()).isEqualTo(Type.OVERBOOK); // actualizado
        assertThat(entity.getNote()).isEqualTo("Reasignación de asiento por sobreventa");
        assertThat(entity.getCreationDate()).isBeforeOrEqualTo(LocalDateTime.now());
    }

    @Test
    void toEntity_shouldHandleInvalidEnumValuesGracefully() {
        // given
        var request = new IncidentCreateRequest(
                "INVALID_TYPE",  // no existe en EntityType
                100L,
                "UNKNOWN_TYPE",  // no existe en Type
                "Error genérico desconocido"
        );

        // when
        Incident entity = mapper.toEntity(request);

        // then
        // valores por defecto definidos en los métodos mapEntityType y mapType
        assertThat(entity.getEntityType()).isEqualTo(EntityType.TRIP);
        assertThat(entity.getType()).isEqualTo(Type.SECURITY);
    }
}
