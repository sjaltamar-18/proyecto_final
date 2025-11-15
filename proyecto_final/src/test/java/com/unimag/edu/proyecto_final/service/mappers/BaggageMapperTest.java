package com.unimag.edu.proyecto_final.service.mappers;

import com.unimag.edu.proyecto_final.api.dto.BaggageDtos.*;
import com.unimag.edu.proyecto_final.domine.entities.Baggage;
import com.unimag.edu.proyecto_final.domine.entities.Ticket;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class BaggageMapperTest {

    private final BaggageMapper mapper = Mappers.getMapper(BaggageMapper.class);

    @Test
    void toEntity_shouldMapCreateRequest() {
        // given
        var request = new BaggageCreateRequest(1L, 15.5, 5000.0, "TAG001");

        // when
        Baggage entity = mapper.toEntity(request);

        // then
        assertThat(entity).isNotNull();
        assertThat(entity.getWeight()).isEqualTo(15.5);
        assertThat(entity.getFee()).isEqualTo(5000.0);
        assertThat(entity.getTagCode()).isEqualTo("TAG001");
    }

    @Test
    void toResponse_shouldMapEntity() {
        // given
        var ticket = Ticket.builder().id(1L).build();
        var entity = Baggage.builder()
                .id(10L)
                .ticket(ticket)
                .weight(20.0)
                .fee(BigDecimal.valueOf(8000.0))
                .tagCode("TAG999")
                .build();

        // when
        BaggageResponse dto = mapper.toResponse(entity);

        // then
        assertThat(dto).isNotNull();
        assertThat(dto.id()).isEqualTo(10L);
        assertThat(dto.ticketId()).isEqualTo(1L);
        assertThat(dto.weightKg()).isEqualTo(20.0);
        assertThat(dto.fee()).isEqualTo(8000.0);
        assertThat(dto.tagCode()).isEqualTo("TAG999");
    }

    @Test
    void updateEntityFromDto_shouldUpdateNonNullFields() {
        // given
        var ticket = Ticket.builder().id(1L).build();
        var entity = Baggage.builder()
                .id(10L)
                .ticket(ticket)
                .weight(10.0)
                .fee(BigDecimal.valueOf(4000.0))
                .tagCode("TAG123")
                .build();

        var update = new BaggageUpdateRequest(12.5, 6000.0);


        mapper.updateEntityFromDto(update, entity);


        assertThat(entity.getWeight()).isEqualTo(12.5);
        assertThat(entity.getFee()).isEqualTo(6000.0);
        assertThat(entity.getTagCode()).isEqualTo("TAG123");
        assertThat(entity.getId()).isEqualTo(10L);
        assertThat(entity.getTicket().getId()).isEqualTo(1L);
    }
}
