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
        // given
        var request = new TicketCreateRequest(
                1L,             // tripId
                2L,             // passengerId
                "A5",           // seatNumber
                10L,            // fromStopId
                20L,            // toStopId
                60000.0,        // price
                "CARD"   // paymentMethod
        );

        // when
        Ticket entity = mapper.toEntity(request);

        // then
        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isNull(); // ignorado por el mapper
        assertThat(entity.getTrip()).isEqualTo(1L);
        assertThat(entity.getPassenger()).isEqualTo(2L);
        assertThat(entity.getSeatNumber()).isEqualTo("A5");
        assertThat(entity.getFromStop()).isEqualTo(10L);
        assertThat(entity.getToStop()).isEqualTo(20L);
        assertThat(entity.getPrice()).isEqualTo(60000.0);
        assertThat(entity.getPaymentMethod()).isEqualTo(PaymentMethod.CARD);
        assertThat(entity.getStatusTicket()).isEqualTo(StatusTicket.SOLD); // constante por defecto
        assertThat(entity.getQrCode()).isNotNull(); // generado
        assertThat(entity.getQrCode()).contains("A5"); // QR personalizado según lógica del mapper
    }

    @Test
    void toResponse_shouldMapEntity() {
        // given
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

        // when
        TicketResponse dto = mapper.toResponse(entity);

        // then
        assertThat(dto).isNotNull();
        assertThat(dto.id()).isEqualTo(10L);
        assertThat(dto.tripId()).isEqualTo(1L);
        assertThat(dto.passengerId()).isEqualTo(2L);
        assertThat(dto.seatNumber()).isEqualTo("B10");
        assertThat(dto.fromStopId()).isEqualTo(5L);
        assertThat(dto.toStopId()).isEqualTo(6L);
        assertThat(dto.price()).isEqualTo(45000.0);
        assertThat(dto.paymentMethod()).isEqualTo("CASH");
        assertThat(dto.status()).isEqualTo("USED");

    }

    @Test
    void updateEntityFromStatusRequest_shouldUpdateStatusAndPaymentMethod() {
        // given
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

        // when
        mapper.updateEntityFromStatusRequest(update, entity);

        // then
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

        // when
        mapper.updateEntityFromStatusRequest(update, entity);

        // then
        // mapStatus y mapPaymentMethod devuelven valores por defecto
        assertThat(entity.getStatusTicket()).isEqualTo(StatusTicket.SOLD);
        assertThat(entity.getPaymentMethod()).isEqualTo(PaymentMethod.CASH);
    }
}
