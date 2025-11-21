
package com.unimag.edu.proyecto_final.service;

import com.unimag.edu.proyecto_final.api.dto.SeatHoldDtos;
import com.unimag.edu.proyecto_final.domine.entities.SeatHold;
import com.unimag.edu.proyecto_final.domine.entities.Stop;
import com.unimag.edu.proyecto_final.domine.entities.Trip;
import com.unimag.edu.proyecto_final.domine.entities.User;
import com.unimag.edu.proyecto_final.domine.entities.enumera.StatusSeatHold;
import com.unimag.edu.proyecto_final.domine.repository.*;
import com.unimag.edu.proyecto_final.exception.NotFoundException;
import com.unimag.edu.proyecto_final.service.mappers.SeatHoldMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class SeatHoldServicelmpl implements  SeatHoldService {

    private final SeatHoldRepository seatHoldRepository;
    private final TripRepository tripRepository;
    private final UserRepository userRepository;
    private final SeatHoldMapper seatHoldMapper;
    private final TicketRepository ticketRepository;
    private final StopRepository stopRepository;


    @Override
    public SeatHoldDtos.SeatHoldResponse create(SeatHoldDtos.SeatHoldCreateRequest request) {
        Trip trip  = tripRepository.findById(request.tripId())
                .orElseThrow(() -> new NotFoundException("trip not found"));

        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new NotFoundException("user not found"));

        Stop fromStop = stopRepository.findById(request.fromStopId())
                .orElseThrow(() -> new NotFoundException("fromStop not found"));

        Stop toStop = stopRepository.findById(request.toStopId())
                .orElseThrow(() -> new NotFoundException("toStop not found"));

        if (seatHoldRepository.isSeatOnHoldByTramo(
                trip.getId(),
                request.seatNumber(),
                fromStop.getStopOrder(),
                toStop.getStopOrder()
        )) {
            throw new IllegalStateException("Seat is temporarily hold in this tramo");
        }
        if (ticketRepository.isSeatSold(request.tripId(), request.seatNumber())){
            throw new IllegalStateException("the seat " +request.seatNumber()+" is already sold");
        }
        if (trip.getDepartureAt().isBefore(LocalDateTime.now())){
            throw new IllegalStateException("Trip alredy departed");
        }
        SeatHold seatHold = seatHoldMapper.toEntity(request);
        seatHold.setTrip(trip);
        seatHold.setUser(user);
        seatHold.setFromStop(fromStop);
        seatHold.setToStop(toStop);

        seatHold.setExpirationDate(LocalDateTime.now().plusMinutes(10));
        seatHold.setStatus(StatusSeatHold.HOLD);

        SeatHold saved = seatHoldRepository.save(seatHold);
        return seatHoldMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public SeatHoldDtos.SeatHoldResponse get(Long id) {
        SeatHold seatHold = seatHoldRepository.findById(id)
                .orElseThrow(()-> new NotFoundException("seatHold not found"));
        return seatHoldMapper.toResponse(seatHold);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SeatHoldDtos.SeatHoldResponse> listActiveByTrip(Long tripId) {
        List<SeatHold> holds = seatHoldRepository.findByTripIdAndStatus(tripId, StatusSeatHold.HOLD);
        return holds.stream().map(seatHoldMapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SeatHoldDtos.SeatHoldResponse> listByUser(Long userId) {
        List<SeatHold> holds = seatHoldRepository.findByUserIdAndStatus(userId, StatusSeatHold.HOLD);
        return holds.stream().map(seatHoldMapper::toResponse).toList();
    }

    @Override
    public SeatHoldDtos.SeatHoldResponse update(Long id, SeatHoldDtos.SeatHoldUpdateRequest request) {
        SeatHold seatHold = seatHoldRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("seatHold not found"));

        seatHoldMapper.updateEntityFromStatusRequest(request,seatHold);
        SeatHold saved = seatHoldRepository.save(seatHold);
        return seatHoldMapper.toResponse(saved);
    }

    @Override @Scheduled(fixedRate = 60000)
    public int expireHolds() {
        int expired = seatHoldRepository.expireHolds();
        if (expired > 0) log.info("Expired {} seat holds", expired);
        return seatHoldRepository.expireHolds();
    }

    @Override
    public int cleanOldExpired() {
        LocalDateTime limit = LocalDateTime.now().minusDays(1);
        return seatHoldRepository.deleteExpiredOlderThan(limit);
    }

    @Override
    public void delete(Long id) {
        SeatHold seatHold = seatHoldRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("seatHold not found"));
        seatHoldRepository.delete(seatHold);

    }
}
