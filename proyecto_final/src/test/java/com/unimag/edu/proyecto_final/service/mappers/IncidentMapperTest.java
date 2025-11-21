package com.unimag.edu.proyecto_final.service.mappers;

import com.unimag.edu.proyecto_final.api.dto.IncidentDtos.*;
import com.unimag.edu.proyecto_final.domine.entities.Incident;
import com.unimag.edu.proyecto_final.domine.entities.enumera.EntityType;
import com.unimag.edu.proyecto_final.domine.entities.enumera.Type;
import org.assertj.core.api.AbstractComparableAssert;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class IncidentMapperTest {

    private final IncidentMapper mapper = Mappers.getMapper(IncidentMapper.class);

    @Test
    void toEntity_shouldMapCreateRequest() {

        var request = new IncidentCreateRequest(
                EntityType.TICKET,
                1L,
                Type.DELIVERY_FAIL,
                "El paquete no fue entregado por error de dirección"
        );

        Incident entity = mapper.toEntity(request);


        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isNull();
        assertThat(entity.getEntityType()).isEqualTo(EntityType.TICKET);
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
        assertThat(dto.entityType()).isEqualTo(EntityType.PARCEL);
        assertThat(dto.entityId()).isEqualTo(5L);
        assertThat(dto.type()).isEqualTo(Type.SECURITY);
        assertThat(dto.note()).contains("no autorizado");
        assertThat(dto.createdAt()).isBeforeOrEqualTo(LocalDateTime.now());
    }

    @Test
    void updateEntityFromDto_shouldUpdateNonNullFields() {

        var entity = Incident.builder()
                .id(20L)
                .entityType(EntityType.TICKET)
                .entityId(99L)
                .type(Type.SECURITY)
                .note("Anomalía detectada en taquilla")
                .creationDate(LocalDateTime.now().minusDays(1))
                .build();

        var update = new IncidentUpdateRequest("Reasignación de asiento por sobreventa","OVERBOOK");


        mapper.updateEntityFromDto(update, entity);


        assertThat(entity.getId()).isEqualTo(20L);
        assertThat(entity.getEntityType()).isEqualTo(EntityType.TICKET);
        AbstractComparableAssert<?, Type> equalTo = assertThat(entity.getType()).isEqualTo(Type.OVERBOOK);
        assertThat(entity.getNote()).isEqualTo("Reasignación de asiento por sobreventa");
        assertThat(entity.getCreationDate()).isBeforeOrEqualTo(LocalDateTime.now());
    }


}