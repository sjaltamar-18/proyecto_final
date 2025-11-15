package com.unimag.edu.proyecto_final.service.mappers;

import com.unimag.edu.proyecto_final.api.dto.RouteDtos.*;
import com.unimag.edu.proyecto_final.domine.entities.Route;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

class RouteMapperTest {

    private final RouteMapper mapper = Mappers.getMapper(RouteMapper.class);

    @Test
    void toEntity_shouldMapCreateRequest() {
        // given
        var request = new RouteCreateRequest(
                "R001",           // code
                "Bogotá - Tunja", // name
                "Bogotá",         // origin
                "Tunja",          // destination
                120.5,            // distanceKm
                150               // durationMin
        );

        // when
        Route entity = mapper.toEntity(request);

        // then
        assertThat(entity).isNotNull();
        assertThat(entity.getRouteCode()).isEqualTo("R001");
        assertThat(entity.getRouteName()).isEqualTo("Bogotá - Tunja");
        assertThat(entity.getOriginName()).isEqualTo("Bogotá");
        assertThat(entity.getDestinationName()).isEqualTo("Tunja");
        assertThat(entity.getDistance()).isEqualTo(120.5);
        assertThat(entity.getTime()).isEqualTo(150);
    }

    @Test
    void toResponse_shouldMapEntity() {
        // given
        var entity = Route.builder()
                .id(10L)
                .routeCode("R002")
                .routeName("Tunja - Duitama")
                .originName("Tunja")
                .destinationName("Duitama")
                .distance(45.7)
                .time(60)
                .build();

        // when
        RouteResponse dto = mapper.toResponse(entity);

        // then
        assertThat(dto).isNotNull();
        assertThat(dto.id()).isEqualTo(10L);
        assertThat(dto.code()).isEqualTo("R002");
        assertThat(dto.name()).isEqualTo("Tunja - Duitama");
        assertThat(dto.origin()).isEqualTo("Tunja");
        assertThat(dto.destination()).isEqualTo("Duitama");
        assertThat(dto.distanceKm()).isEqualTo(45.7);
        assertThat(dto.durationMin()).isEqualTo(60);
    }

    @Test
    void updateEntityFromDto_shouldUpdateNonNullFields() {
        // given
        var entity = Route.builder()
                .id(5L)
                .routeCode("R003")
                .routeName("Bogotá - Medellín")
                .originName("Bogotá")
                .destinationName("Medellín")
                .distance(420.0)
                .time(480)
                .build();

        var update = new RouteUpdateRequest(
                "Bogotá - Cali",
                "Bogotá",
                "Cali",
                460.0,
                500
        );

        // when
        mapper.updateEntityFromDto(update, entity);

        // then
        assertThat(entity.getId()).isEqualTo(5L);
        assertThat(entity.getRouteCode()).isEqualTo("R003"); // key field should remain unchanged
        assertThat(entity.getRouteName()).isEqualTo("Bogotá - Cali");
        assertThat(entity.getOriginName()).isEqualTo("Bogotá");
        assertThat(entity.getDestinationName()).isEqualTo("Cali");
        assertThat(entity.getDistance()).isEqualTo(460.0);
        assertThat(entity.getTime()).isEqualTo(500);
        }
}
