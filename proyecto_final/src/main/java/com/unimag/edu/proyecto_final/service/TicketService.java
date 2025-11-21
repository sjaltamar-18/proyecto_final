
package com.unimag.edu.proyecto_final.service;

import com.unimag.edu.proyecto_final.api.dto.TicketDtos;
import com.unimag.edu.proyecto_final.api.dto.TicketDtos.*;
import com.unimag.edu.proyecto_final.domine.entities.Ticket;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TicketService {

    TicketResponse create(TicketCreateRequest request);

    TicketResponse get(Long id);

    List<TicketResponse> listByTrip(Long tripId);

    List<TicketResponse> listByPassenger(Long passengerId);

    TicketResponse update(Long id, TicketUpdateRequest request);

    TicketResponse cancel(Long id);


    TicketResponse confirmSeatHold(Long holdId);

    List<Ticket> findNoShowTickets(Long tripId);

    TicketResponse getByQrCode(String qrCode);
}