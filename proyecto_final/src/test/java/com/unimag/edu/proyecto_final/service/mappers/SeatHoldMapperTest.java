package com.unimag.edu.proyecto_final.service.mappers;

import com.unimag.edu.proyecto_final.api.dto.SeatHoldDtos.*;
import com.unimag.edu.proyecto_final.domine.entities.SeatHold;
import com.unimag.edu.proyecto_final.domine.entities.Trip;
import com.unimag.edu.proyecto_final.domine.entities.User;
import com.unimag.edu.proyecto_final.domine.entities.enumera.StatusSeatHold;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class SeatHoldMapperTest {

    private final SeatHoldMapper mapper = Mappers.getMapper(SeatHoldMapper.class);

    @Test
    void toEntity_shouldMapCreateRequest() {
        // given
        var request = new SeatHoldCreateRequest(
                1L,
                "4B",
                4L
        );

        // when
        SeatHold entity = mapper.toEntity(request);

        // then
        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isNull();
        assertThat(entity.getTrip().getId()).isEqualTo(1L);
        assertThat(entity.getUser().getId()).isEqualTo(4L);
        assertThat(entity.getSeatNumber()).isEqualTo("4B");
        assertThat(entity.getStatus()).isEqualTo(StatusSeatHold.HOLD);
        assertThat(entity.getExpirationDate()).isAfter(LocalDateTime.now());
        assertThat(entity.getExpirationDate()).isBeforeOrEqualTo(LocalDateTime.now().plusMinutes(10));
    }

    @Test
    void toResponse_shouldMapEntity() {
        // given
        var trip = Trip.builder().id(1L).build();
        var user = User.builder().id(2L).build();

        var entity = SeatHold.builder()
                .id(10L)
                .trip(trip)
                .user(user)
                .seatNumber("7D")
                .status(StatusSeatHold.HOLD)
                .expirationDate(LocalDateTime.now().plusMinutes(8))
                .build();

        // when
        SeatHoldResponse dto = mapper.toResponse(entity);

        // then
        assertThat(dto).isNotNull();
        assertThat(dto.id()).isEqualTo(10L);
        assertThat(dto.tripId()).isEqualTo(1L);
        assertThat(dto.userId()).isEqualTo(2L);
        assertThat(dto.seatNumber()).isEqualTo("7D");
        assertThat(dto.status()).isEqualTo("HOLD");
        assertThat(dto.expiresAt()).isAfter(LocalDateTime.now());
    }

    @Test
    void updateEntityFromStatusRequest_shouldUpdateStatusOnly() {
        // given
        var trip = Trip.builder().id(1L).build();
        var user = User.builder().id(2L).build();

        var entity = SeatHold.builder()
                .id(20L)
                .trip(trip)
                .user(user)
                .seatNumber("10")
                .status(StatusSeatHold.HOLD)
                .expirationDate(LocalDateTime.now().plusMinutes(5))
                .build();

        var update = new SeatHoldUpdateRequest("CONFIRMED");

        // when
        mapper.updateEntityFromStatusRequest(update, entity);

        // then
        assertThat(entity.getStatus()).isEqualTo(StatusSeatHold.HOLD);
        assertThat(entity.getId()).isEqualTo(20L);
        assertThat(entity.getTrip().getId()).isEqualTo(1L);
        assertThat(entity.getUser().getId()).isEqualTo(2L);
    }

    @Test
    void updateEntityFromStatusRequest_shouldHandleInvalidStatusGracefully() {
        // given
        var entity = SeatHold.builder()
                .id(25L)
                .status(StatusSeatHold.EXPIRED)
                .build();

        var update = new SeatHoldUpdateRequest("INVALID_STATUS");

        // when
        mapper.updateEntityFromStatusRequest(update, entity);

        // then
        // valor por defecto → HOLD (según método mapStatus)
        assertThat(entity.getStatus()).isEqualTo(StatusSeatHold.HOLD);
    }
}
