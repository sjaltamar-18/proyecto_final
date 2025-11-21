package com.unimag.edu.proyecto_final.service.mappers;

import com.unimag.edu.proyecto_final.api.dto.TicketDtos.*;
import com.unimag.edu.proyecto_final.domine.entities.Stop;
import com.unimag.edu.proyecto_final.domine.entities.Ticket;
import com.unimag.edu.proyecto_final.domine.entities.Trip;
import com.unimag.edu.proyecto_final.domine.entities.User;
import com.unimag.edu.proyecto_final.domine.entities.enumera.PaymentMethod;
import com.unimag.edu.proyecto_final.domine.entities.enumera.StatusTicket;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface TicketMapper {


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "trip", source = "tripId")
    @Mapping(target = "passenger", source = "passengerId")
    @Mapping(target = "fromStop", source = "fromStopId")
    @Mapping(target = "toStop", source = "toStopId")
    @Mapping(target = "paymentMethod",
            expression = "java(com.unimag.edu.proyecto_final.domine.entities.enumera.PaymentMethod.valueOf(request.paymentMethod()))")
    @Mapping(target = "statusTicket",
            expression = "java(com.unimag.edu.proyecto_final.domine.entities.enumera.StatusTicket.SOLD)")
    Ticket toEntity(TicketCreateRequest request);
    default Trip mapTrip(Long id) {
        return id == null ? null : Trip.builder().id(id).build();
    }

    default User mapUser(Long id) {
        return id == null ? null : User.builder().id(id).build();
    }

    default Stop mapStop(Long id) {
        return id == null ? null : Stop.builder().id(id).build();
    }
    default Long map(Trip trip) {
        return trip == null ? null : trip.getId();
    }

    default Long map(User user) {
        return user == null ? null : user.getId();
    }

    default Long map(Stop stop) {
        return stop == null ? null : stop.getId();
    }
    @Mapping(source = "trip", target = "tripId")
    @Mapping(source = "passenger", target = "passengerId")
    @Mapping(source = "fromStop", target = "fromStopId")
    @Mapping(source = "toStop", target = "toStopId")
    @Mapping(target = "paymentMethod", expression = "java(ticket.getPaymentMethod() != null ? ticket.getPaymentMethod().name() : null)")
    @Mapping(target = "status", expression = "java(ticket.getStatusTicket() != null ? ticket.getStatusTicket().name() : null)")
    TicketResponse toResponse(Ticket ticket);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "statusTicket", expression = "java(request.status() != null ? mapStatus(request.status()) : ticket.getStatusTicket())")
    void updateEntityFromStatusRequest(TicketUpdateRequest request, @MappingTarget Ticket ticket);


    default PaymentMethod mapPaymentMethod(String value) {
        if (value == null) return null;
        try {
            return PaymentMethod.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException ex) {
            return PaymentMethod.CASH;
        }
    }

    default StatusTicket mapStatus(String value) {
        if (value == null) return null;
        try {
            return StatusTicket.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException ex) {
            return StatusTicket.SOLD;
        }
    }

    default String generateQrCode(TicketCreateRequest request) {
        return "QR-" + request.tripId() + "-" + request.seatNumber() + "-" + System.currentTimeMillis();
    }
}