package com.unimag.edu.proyecto_final.service.mappers;

import com.unimag.edu.proyecto_final.api.dto.TicketDtos.*;
import com.unimag.edu.proyecto_final.domine.entities.Stop;
import com.unimag.edu.proyecto_final.domine.entities.Ticket;
import com.unimag.edu.proyecto_final.domine.entities.Trip;
import com.unimag.edu.proyecto_final.domine.entities.User;
import com.unimag.edu.proyecto_final.domine.entities.enumera.PaymentMethod;
import com.unimag.edu.proyecto_final.domine.entities.enumera.StatusTicket;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

class TicketMapperTest {

    private final TicketMapper mapper = Mappers.getMapper(TicketMapper.class);
    @Test
    void toEntity_shouldMapCreateRequest() {

        var request = new TicketCreateRequest(
                1L,
                2L,
                "A5",
                10L,
                20L,
                60000.0,
                "CARD",
                true
        );


        Ticket entity = mapper.toEntity(request);


        assertThat(entity).isNotNull();
        assertThat(entity.getTrip().getId()).isEqualTo(1L);
        assertThat(entity.getPassenger().getId()).isEqualTo(2L);
        assertThat(entity.getSeatNumber()).isEqualTo("A5");
        assertThat(entity.getFromStop().getId()).isEqualTo(10L);
        assertThat(entity.getToStop().getId()).isEqualTo(20L);
        assertThat(entity.getPrice()).isEqualTo(60000.0);
        assertThat(entity.getPaymentMethod()).isEqualTo(PaymentMethod.CARD);
        assertThat(entity.getStatusTicket()).isEqualTo(StatusTicket.SOLD);
    }

    @Test
    void toResponse_shouldMapEntity() {

        var trip = Trip.builder().id(1l).build();
        var user = User.builder().id(2l).build();
        var stop = Stop.builder().id(3l).build();
        var stop2 = Stop.builder().id(4l).build();
        var entity = Ticket.builder()
                .id(10L)
                .trip(trip)
                .passenger(user)
                .seatNumber("B10")
                .fromStop(stop)
                .toStop(stop2)
                .price(45000.0)
                .paymentMethod(PaymentMethod.CASH)
                .statusTicket(StatusTicket.SOLD)
                .build();


        TicketResponse dto = mapper.toResponse(entity);


        assertThat(dto).isNotNull();
        assertThat(dto.id()).isEqualTo(10L);
        assertThat(dto.tripId()).isEqualTo(1L);
        assertThat(dto.passengerId()).isEqualTo(user.getId());
        assertThat(dto.seatNumber()).isEqualTo("B10");
        assertThat(dto.fromStopId()).isEqualTo(stop.getId());
        assertThat(dto.toStopId()).isEqualTo(stop2.getId());
        assertThat(dto.price()).isEqualTo(45000.0);
        assertThat(dto.paymentMethod()).isEqualTo("CASH");
        assertThat(dto.status()).isEqualTo("SOLD");

    }

    @Test
    void updateEntityFromStatusRequest_shouldUpdateStatusAndPaymentMethod() {

        var trip = Trip.builder().id(1l).build();
        var user = User.builder().id(2l).build();
        var entity = Ticket.builder()
                .id(15L)
                .trip(trip)
                .passenger(user)
                .seatNumber("C2")
                .price(55000.0)
                .paymentMethod(PaymentMethod.CARD)
                .statusTicket(StatusTicket.SOLD)
                .build();

        var update = new TicketUpdateRequest(
                "CANCELLED",
                "CASH"
        );


        mapper.updateEntityFromStatusRequest(update, entity);


        assertThat(entity.getId()).isEqualTo(15L);
        assertThat(entity.getStatusTicket()).isEqualTo(StatusTicket.CANCELLED);
        assertThat(entity.getPaymentMethod()).isEqualTo(PaymentMethod.CASH);
    }

    @Test
    void updateEntityFromStatusRequest_shouldHandleInvalidEnumsGracefully() {
        // given
        var entity = Ticket.builder()
                .id(20L)
                .paymentMethod(PaymentMethod.CARD)
                .statusTicket(StatusTicket.SOLD)
                .build();

        var update = new TicketUpdateRequest(
                "INVALID_STATUS",
                "INVALID_METHOD"
        );


        mapper.updateEntityFromStatusRequest(update, entity);

        assertThat(entity.getStatusTicket()).isEqualTo(StatusTicket.SOLD);
        assertThat(entity.getPaymentMethod()).isEqualTo(PaymentMethod.CASH);
    }
}