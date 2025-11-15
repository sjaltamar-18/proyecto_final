package com.unimag.edu.proyecto_final.service.mappers;

import com.unimag.edu.proyecto_final.api.dto.StopDtos.*;
import com.unimag.edu.proyecto_final.domine.entities.Route;
import com.unimag.edu.proyecto_final.domine.entities.Stop;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

class StopMapperTest {

    private final StopMapper mapper = Mappers.getMapper(StopMapper.class);

    @Test
    void toEntity_shouldMapCreateRequest() {
        // given
        var request = new StopCreateRequest(
                1L,         // routeId
                "Terminal Norte",
                1,          // order
                4.7110,     // lat
                -74.0721    // lng
        );

        // when
        Stop entity = mapper.toEntity(request);

        // then
        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isNull(); // Ignorado por el mapper
        assertThat(entity.getRoute()).isNull();
        assertThat(entity.getStopName()).isEqualTo("Terminal Norte");
        assertThat(entity.getStopOrder()).isEqualTo(1);
        assertThat(entity.getLatitude()).isEqualTo(4.7110);
        assertThat(entity.getLongitude()).isEqualTo(-74.0721);
    }

    @Test
    void toResponse_shouldMapEntity() {
        var route = Route.builder().id(2L).build();
        var entity = Stop.builder()
                .id(10L)
                .route(route)
                .stopName("Paradero Central")
                .stopOrder(5)
                .latitude(5.0700)
                .longitude(-73.8500)
                .build();

        // when
        StopResponse dto = mapper.toResponse(entity);

        // then
        assertThat(dto).isNotNull();
        assertThat(dto.id()).isEqualTo(10L);
        assertThat(dto.routeId()).isEqualTo(2L);
        assertThat(dto.name()).isEqualTo("Paradero Central");
        assertThat(dto.order()).isEqualTo(5);
        assertThat(dto.lat()).isEqualTo(5.0700);
        assertThat(dto.lng()).isEqualTo(-73.8500);
    }

    @Test
    void updateEntityFromDto_shouldUpdateNonNullFields() {
        var route = Route.builder().id(1L).build();
        var entity = Stop.builder()
                .id(3L)
                .route(route)
                .stopName("Antiguo Paradero")
                .stopOrder(2)
                .latitude(4.6000)
                .longitude(-74.1000)
                .build();

        var update = new StopUpdateRequest(
                "Nuevo Paradero",
                null,
                4.6200,
                -74.1200
        );

        mapper.updateEntityFromDto(update, entity);


        assertThat(entity.getId()).isEqualTo(3L);

        assertThat(entity.getRoute().getId()).isEqualTo(1L);

        assertThat(entity.getStopName()).isEqualTo("Nuevo Paradero");
        assertThat(entity.getStopOrder()).isEqualTo(2);
        assertThat(entity.getLatitude()).isEqualTo(4.6200);
        assertThat(entity.getLongitude()).isEqualTo(-74.1200);
    }

}
