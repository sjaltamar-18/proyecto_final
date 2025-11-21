package com.unimag.edu.proyecto_final.service.mappers;

import com.unimag.edu.proyecto_final.api.dto.TripDtos.*;
import com.unimag.edu.proyecto_final.domine.entities.Bus;
import com.unimag.edu.proyecto_final.domine.entities.Route;
import com.unimag.edu.proyecto_final.domine.entities.Trip;
import com.unimag.edu.proyecto_final.domine.entities.enumera.BoardingStatus;
import com.unimag.edu.proyecto_final.domine.entities.enumera.StatusTrip;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class TripMapperTest {

    private final TripMapper mapper = Mappers.getMapper(TripMapper.class);

    @Test
    void toEntity_shouldMapCreateRequest() {
        // given
        var request = new TripCreateRequest(
                1L, // routeId
                2L, // busId
                LocalDate.of(2025, 5, 1),
                LocalDateTime.of(2025, 5, 1, 8, 0),
                LocalDateTime.of(2025, 5, 1, 12, 0),
                BoardingStatus.BOARDING_OPEN
        );

        // when
        Trip entity = mapper.toEntity(request);

        // then
        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isNull();   // ignored by mapper

        assertThat(entity.getRoute()).isNotNull();
        assertThat(entity.getRoute().getId()).isEqualTo(1L);

        assertThat(entity.getBus()).isNotNull();
        assertThat(entity.getBus().getId()).isEqualTo(2L);

        assertThat(entity.getDate()).isEqualTo(LocalDate.of(2025, 5, 1));
        assertThat(entity.getDepartureAt()).isEqualTo(LocalDateTime.of(2025, 5, 1, 8, 0));
        assertThat(entity.getArrivalAt()).isEqualTo(LocalDateTime.of(2025, 5, 1, 12, 0));

    }


    @Test
    void toResponse_shouldMapEntity() {
        // given
        var route = Route.builder().id(1L).build();
        var bus = Bus.builder().id(2L).build();
        var entity = Trip.builder()
                .id(10L)
                .route(route)
                .bus(bus)
                .date(LocalDate.of(2025, 5, 10))
                .departureAt(LocalDateTime.of(2025, 5, 10, 7, 30))
                .arrivalAt(LocalDateTime.of(2025, 5, 10, 12, 0))
                .statusTrip(StatusTrip.DEPARTED)
                .build();

        // when
        TripResponse dto = mapper.toResponse(entity);

        // then
        assertThat(dto).isNotNull();
        assertThat(dto.id()).isEqualTo(10L);
        assertThat(dto.routeId()).isEqualTo(1L);
        assertThat(dto.busId()).isEqualTo(2L);
        assertThat(dto.date()).isEqualTo(LocalDate.of(2025, 5, 10));
        assertThat(dto.departureAt()).isEqualTo(LocalDateTime.of(2025, 5, 10, 7, 30));
        assertThat(dto.arrivalEta()).isEqualTo(LocalDateTime.of(2025, 5, 10, 12, 0));
        assertThat(dto.status()).isEqualTo("DEPARTED");
    }

    @Test
    void updateEntityFromStatusRequest_shouldUpdateStatusOnly() {

        var route = Route.builder().id(20L).build();
        var bus = Bus.builder().id(8L).build();
        // given
        var entity = Trip.builder()
                .id(20L)
                .route(route)
                .bus(bus)
                .date(LocalDate.of(2025,5,20))
                .departureAt(LocalDateTime.of(2025, 5, 20, 9, 0))
                .arrivalAt(LocalDateTime.of(2025, 5, 20, 13, 30))
                .statusTrip(StatusTrip.SCHEDULED)
                .build();

        var update = new TripUpdateRequest(LocalDateTime.of(2025, 5, 20, 9, 0),LocalDateTime.of(2025, 5, 20, 13, 30),"CANCELLED");

        // when
        mapper.updateEntityFromStatusRequest(update, entity);

        // then
        assertThat(entity.getStatusTrip()).isEqualTo(StatusTrip.CANCELLED);
        assertThat(entity.getId()).isEqualTo(20L);
        assertThat(entity.getRoute().getId()).isEqualTo(20L);
        assertThat(entity.getBus().getId()).isEqualTo(8L);
    }

    @Test
    void updateEntityFromStatusRequest_shouldHandleInvalidStatusGracefully() {
        // given
        var entity = Trip.builder()
                .id(25L)
                .departureAt(LocalDateTime.of(2025, 5, 20, 9, 0))
                .arrivalAt(LocalDateTime.of(2025, 5, 20, 13, 30))
                .statusTrip(StatusTrip.BOARDING)
                .build();

        var update = new TripUpdateRequest(LocalDateTime.of(2025, 5, 20, 9, 0),LocalDateTime.of(2025, 5, 20, 13, 30),"INVALID_STATUS");

        mapper.updateEntityFromStatusRequest(update, entity);


        assertThat(entity.getStatusTrip()).isEqualTo(StatusTrip.SCHEDULED);
    }
}
