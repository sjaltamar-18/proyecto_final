package com.unimag.edu.proyecto_final.service;

import com.unimag.edu.proyecto_final.api.dto.TicketDtos;
import com.unimag.edu.proyecto_final.domine.entities.Stop;
import com.unimag.edu.proyecto_final.domine.entities.Ticket;
import com.unimag.edu.proyecto_final.domine.entities.Trip;
import com.unimag.edu.proyecto_final.domine.entities.User;
import com.unimag.edu.proyecto_final.domine.entities.enumera.StatusTicket;
import com.unimag.edu.proyecto_final.domine.repository.StopRepository;
import com.unimag.edu.proyecto_final.domine.repository.TicketRepository;
import com.unimag.edu.proyecto_final.domine.repository.TripRepository;
import com.unimag.edu.proyecto_final.domine.repository.UserRepository;
import com.unimag.edu.proyecto_final.exception.NotFoundException;
import com.unimag.edu.proyecto_final.service.mappers.TicketMapper;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional

public class TicketServicelmpl implements TicketService {

    private final TicketRepository ticketRepository;
    private final TripRepository tripRepository;
    private final UserRepository userRepository;
    private final StopRepository stopRepository;
    private final TicketMapper ticketMapper;

    @Override
    public TicketDtos.TicketResponse create(TicketDtos.TicketCreateRequest request) {
        Trip trip = tripRepository.findById(request.tripId())
                .orElseThrow(()-> new NotFoundException("trip not found"));

        User passenger = userRepository.findById(request.passengerId())
                .orElseThrow(()-> new NotFoundException("passenger not found"));

        Stop fromStop = stopRepository.findById(request.fromStopId())
                .orElseThrow(()-> new NotFoundException("stop not found"));

        Stop toStop = stopRepository.findById(request.toStopId())
                .orElseThrow(()-> new NotFoundException("stop not found"));

        Boolean ocupado = ticketRepository.isSeatOccupied(
                trip.getId(),
                request.seatNumber(),
                fromStop.getStopOrder(),
                toStop.getStopOrder()
        );

        if (ocupado) {
            throw new IllegalStateException("seat "+ request.seatNumber()+" already occupied");
        }
        Ticket ticket = ticketMapper.toEntity(request);
        ticket.setTrip(trip);
        ticket.setPassenger(passenger);
        ticket.setFromStop(fromStop);
        ticket.setToStop(toStop);
        ticket.setStatusTicket(StatusTicket.SOLD);

        Ticket saved = ticketRepository.save(ticket);
        return ticketMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public TicketDtos.TicketResponse get(Long id) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(()-> new NotFoundException("ticket not found"));

        return ticketMapper.toResponse(ticket);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TicketDtos.TicketResponse> listByTrip(Long tripId) {
        List<Ticket> tickets = ticketRepository.findByTripId(tripId);
        return tickets.stream().map(ticketMapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TicketDtos.TicketResponse> listByPassenger(Long passengerId) {
        List<Ticket> tickets = ticketRepository.findByPassengerIdOrderByCreatedAtDesc(passengerId);
        return tickets.stream().map(ticketMapper::toResponse).toList();
    }

    @Override
    public TicketDtos.TicketResponse update(Long id, TicketDtos.TicketUpdateRequest request) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(()-> new NotFoundException("ticket not found"));

        ticketMapper.updateEntityFromStatusRequest(request,ticket);
        Ticket saved = ticketRepository.save(ticket);
        return ticketMapper.toResponse(saved);
    }

    @Override
    public void cancel(Long id) {
        if (!ticketRepository.existsById(id)) {
            throw new NotFoundException("ticket not found");
        }
        ticketRepository.markAsCancelled(id);

    }

    @Override
    @Transactional(readOnly = true)
    public TicketDtos.TicketResponse getByQrCode(String qrCode) {
        Ticket ticket = ticketRepository.findByQrCode(qrCode)
                .orElseThrow(()-> new NotFoundException("QR not found"));
        return ticketMapper.toResponse(ticket);
    }
}
