
package com.unimag.edu.proyecto_final.service.mappers;

import com.unimag.edu.proyecto_final.api.dto.ConfiDtos.*;
import com.unimag.edu.proyecto_final.domine.entities.Confi;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

class ConfiMapperTest {

    private final ConfiMapper mapper = Mappers.getMapper(ConfiMapper.class);

    @Test
    void toEntity_shouldMapCreateRequest() {

        var request = new ConfigCreateRequest("max_overbooking", "5");


        Confi entity = mapper.toEntity(request);

        assertThat(entity).isNotNull();
        assertThat(entity.getKey()).isEqualTo("max_overbooking");
        assertThat(entity.getValue()).isEqualTo("5");
    }

    @Test
    void toResponse_shouldMapEntity() {

        var entity = Confi.builder()
                .id(10L)
                .key("seat_hold_time")
                .value("10")
                .build();


        ConfigResponse dto = mapper.toResponse(entity);

        assertThat(dto).isNotNull();
        assertThat(dto.id()).isEqualTo(10L);
        assertThat(dto.key()).isEqualTo("seat_hold_time");
        assertThat(dto.value()).isEqualTo("10");
    }

    @Test
    void updateEntityFromDto_shouldUpdateNonNullFields() {

        var entity = Confi.builder()
                .id(10L)
                .key("discount_rate")
                .value("0.10")
                .build();

        var update = new ConfigUpdateRequest("0.15");


        mapper.updateEntityFromDto(update, entity);


        assertThat(entity.getValue()).isEqualTo("0.15");
        assertThat(entity.getKey()).isEqualTo("discount_rate");
    }
}
