package com.unimag.edu.proyecto_final.service.mappers;

import com.unimag.edu.proyecto_final.api.dto.TicketDtos.*;
import com.unimag.edu.proyecto_final.domine.entities.Ticket;
import com.unimag.edu.proyecto_final.domine.entities.enumera.PaymentMethod;
import com.unimag.edu.proyecto_final.domine.entities.enumera.StatusTicket;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface TicketMapper {


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", constant = "SOLD") // por defecto cuando se crea
    @Mapping(target = "qrCode", expression = "java(generateQrCode(request))")
    @Mapping(target = "paymentMethod", expression = "java(mapPaymentMethod(request.paymentMethod()))")
    Ticket toEntity(TicketCreateRequest request);


    @Mapping(target = "status", expression = "java(ticket.getStatus() != null ? ticket.getStatus().name() : null)")
    @Mapping(target = "paymentMethod", expression = "java(ticket.getPaymentMethod() != null ? ticket.getPaymentMethod().name() : null)")
    TicketResponse toResponse(Ticket ticket);


    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "status", expression = "java(request.status() != null ? mapStatus(request.status()) : ticket.getStatus())")
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
