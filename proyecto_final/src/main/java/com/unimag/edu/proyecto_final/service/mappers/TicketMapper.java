package com.unimag.edu.proyecto_final.service.mappers;

import com.unimag.edu.proyecto_final.api.dto.TicketDtos.*;
import com.unimag.edu.proyecto_final.domine.entities.Ticket;
import com.unimag.edu.proyecto_final.domine.entities.enumera.PaymentMethod;
import com.unimag.edu.proyecto_final.domine.entities.enumera.StatusTicket;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface TicketMapper {


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "statusTicket", constant = "SOLD") // Estado por defecto
    @Mapping(target = "trip", ignore = true)
    @Mapping(target = "passenger", ignore = true)
    @Mapping(target = "fromStop", ignore = true)
    @Mapping(target = "toStop", ignore = true)
    @Mapping(target = "qrCode", expression = "java(generateQrCode(request))")
    @Mapping(target = "paymentMethod", expression = "java(mapPaymentMethod(request.paymentMethod()))")
    Ticket toEntity(TicketCreateRequest request);

    @Mapping(target = "status", expression = "java(ticket.getStatusTicket() != null ? ticket.getStatusTicket().name() : null)")
    @Mapping(target = "paymentMethod", expression = "java(ticket.getPaymentMethod() != null ? ticket.getPaymentMethod().name() : null)")
    @Mapping(source = "trip.id", target = "tripId")
    @Mapping(source = "passenger.id", target = "passengerId")
    @Mapping(source = "fromStop.id", target = "fromStopId")
    @Mapping(source = "toStop.id", target = "toStopId")
    TicketResponse toResponse(Ticket ticket);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "statusTicket", expression = "java(request.status() != null ? mapStatus(request.status()) : ticket.getStatusTicket())")
    void updateEntityFromStatusRequest(TicketUpdateRequest request, @MappingTarget Ticket ticket);

    // Métodos auxiliares
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
