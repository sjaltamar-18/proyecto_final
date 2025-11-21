

package com.unimag.edu.proyecto_final.service;

import com.unimag.edu.proyecto_final.api.dto.TicketDtos;
import com.unimag.edu.proyecto_final.domine.entities.*;
import com.unimag.edu.proyecto_final.domine.entities.enumera.StatusSeatHold;
import com.unimag.edu.proyecto_final.domine.entities.enumera.StatusTicket;
import com.unimag.edu.proyecto_final.domine.repository.*;
import com.unimag.edu.proyecto_final.exception.NotFoundException;
import com.unimag.edu.proyecto_final.service.mappers.TicketMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
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
    private final SeatHoldRepository seatHoldRepository;

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
        if (seatHoldRepository.isSeatOnHoldByTramo(
                trip.getId(),
                request.seatNumber(),
                fromStop.getStopOrder(),
                toStop.getStopOrder()
        )) {
            throw new IllegalStateException("Seat is temporarily reserved by hold in this stop");
        }

        if (ocupado) {
            throw new IllegalStateException("seat "+ request.seatNumber()+" already occupied");
        }

        double ocupacion = (double) ticketRepository.countSoldSeats(trip.getId())
                / trip.getBus().getCapacity();

        long minutosRestantes = java.time.Duration.between(
                java.time.LocalDateTime.now(),
                trip.getDepartureAt()
        ).toMinutes();
        if (ocupacion > 0.95 && minutosRestantes < 30) {
            if (!Boolean.TRUE.equals(request.approvedByDispatcher())) {
                throw new IllegalStateException("Overbooking requires dispatcher approval");
            }
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
    @Transactional
    public TicketDtos.TicketResponse cancel(Long id) {

        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("ticket not found"));

        if (ticket.getStatusTicket() == StatusTicket.CANCELLED) {
            throw new IllegalStateException("ticket already cancelled");
        }

        Trip trip = ticket.getTrip();
        LocalDateTime departure = trip.getDepartureAt();
        LocalDateTime now = LocalDateTime.now();

        long hoursBefore = Duration.between(now, departure).toHours();

        BigDecimal refund;

        BigDecimal price = BigDecimal.valueOf(ticket.getPrice());

        if (hoursBefore > 24) {
            refund = price.multiply(BigDecimal.valueOf(0.90));
        } else if (hoursBefore >= 12) {
            refund = price.multiply(BigDecimal.valueOf(0.50));
        } else {
            refund = BigDecimal.ZERO;
        }

        ticket.setStatusTicket(StatusTicket.CANCELLED);
        ticket.setCancelledAt(now);
        ticket.setRefundAmount(refund);

        Ticket saved = ticketRepository.save(ticket);

        return ticketMapper.toResponse(saved);
    }


    @Override
    public TicketDtos.TicketResponse confirmSeatHold(Long holdId) {

        SeatHold hold = seatHoldRepository.findById(holdId)
                .orElseThrow(() -> new NotFoundException("Hold not found"));

        if (hold.getExpirationDate().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Hold has expired");
        }

        if (hold.getStatus() != StatusSeatHold.HOLD) {
            throw new IllegalStateException("Hold is not ACTIVE");
        }

        Trip trip = hold.getTrip();
        User user = hold.getUser();

        boolean ocupado = ticketRepository.isSeatOccupied(
                trip.getId(),
                hold.getSeatNumber(),
                hold.getFromStop().getStopOrder(),
                hold.getToStop().getStopOrder()
        );

        if (ocupado) {
            throw new IllegalStateException("Seat already sold or occupied");
        }

        Ticket ticket = new Ticket();
        ticket.setTrip(trip);
        ticket.setPassenger(user);
        ticket.setSeatNumber(hold.getSeatNumber());
        ticket.setStatusTicket(StatusTicket.SOLD);
        ticket.setCreatedAt(LocalDateTime.now());

        Ticket saved = ticketRepository.save(ticket);

        // actualizar hold
        hold.setStatus(StatusSeatHold.SOLD);
        seatHoldRepository.save(hold);

        return ticketMapper.toResponse(saved);
    }

    @Override
    public  List<Ticket> findNoShowTickets(Long tripId) {

        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(()-> new NotFoundException("trip not found"));

        LocalDateTime limit = trip.getDepartureAt().minusMinutes(5);

        return ticketRepository.findNoShowTickets(
                tripId, StatusTicket.SOLD,
                limit
        );
    }

    @Override
    @Transactional(readOnly = true)
    public TicketDtos.TicketResponse getByQrCode(String qrCode) {
        Ticket ticket = ticketRepository.findByQrCode(qrCode)
                .orElseThrow(()-> new NotFoundException("QR not found"));
        return ticketMapper.toResponse(ticket);
    }
}
