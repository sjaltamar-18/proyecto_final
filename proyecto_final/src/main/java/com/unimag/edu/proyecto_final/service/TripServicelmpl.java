
package com.unimag.edu.proyecto_final.service;

import com.unimag.edu.proyecto_final.api.dto.TripDtos;
import com.unimag.edu.proyecto_final.domine.entities.*;
import com.unimag.edu.proyecto_final.domine.entities.enumera.BoardingStatus;
import com.unimag.edu.proyecto_final.domine.entities.enumera.StatusBus;
import com.unimag.edu.proyecto_final.domine.entities.enumera.StatusTrip;
import com.unimag.edu.proyecto_final.domine.repository.*;
import com.unimag.edu.proyecto_final.exception.NotFoundException;
import com.unimag.edu.proyecto_final.service.mappers.TripMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional

public class TripServicelmpl implements  TripService {

    private final TripRepository tripRepository;
    private final RouteRepository routeRepository;
    private final BusRepository busRepository;
    private final TripMapper tripMapper;
    private final AssignmentRepository assignmentRepository;
    private final UserRepository userRepository;

    @Override
    public TripDtos.TripResponse create(TripDtos.TripCreateRequest request) {
        Route route = routeRepository.findById(request.routeId())
                .orElseThrow(() -> new NotFoundException("Route not found"));

        Bus bus = busRepository.findById(request.busId())
                .orElseThrow(() -> new NotFoundException("Bus not found"));

        if (bus.getStatus() != StatusBus.AVAILABLE){
            throw new IllegalStateException("Bus not available");
        }
        List<Trip> activeTrips = tripRepository.findByStatusTrip(StatusTrip.SCHEDULED);
        boolean busAlreadyInTrip = activeTrips.stream()
                .anyMatch(t -> t.getBus().getId().equals(bus.getId()));
        if(busAlreadyInTrip){
            throw new IllegalStateException("Bus already in trip");
        }
        Trip trip = tripMapper.toEntity(request);
        trip.setRoute(route);
        trip.setBus(bus);
        trip.setStatusTrip(StatusTrip.SCHEDULED);
        Trip saved = tripRepository.save(trip);

        bus.setStatus(StatusBus.ACTIVE);
        busRepository.save(bus);

        return tripMapper.toResponse(saved);
    }



    @Override
    @Transactional(readOnly = true)
    public TripDtos.TripResponse get(Long id) {
        Trip trip =  tripRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Trip not found"));
        return tripMapper.toResponse(trip);
    }

    @Override
    @Transactional
    public TripDtos.TripResponse openBoarding(Long tripId) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new NotFoundException("Trip not found"));

        if (trip.getBoardingStatus() == BoardingStatus.BOARDING_OPEN) {
            throw new IllegalStateException("Boarding already open");
        }

        trip.setBoardingStatus(BoardingStatus.BOARDING_OPEN);
        Trip saved = tripRepository.save(trip);

        return tripMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public TripDtos.TripResponse closeBoarding(Long tripId) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new NotFoundException("Trip not found"));

        if (trip.getBoardingStatus() == BoardingStatus.BOARDING_CLOSED) {
            throw new IllegalStateException("Boarding already closed");
        }

        trip.setBoardingStatus(BoardingStatus.BOARDING_CLOSED);
        Trip saved = tripRepository.save(trip);

        return tripMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TripDtos.TripResponse> listByRoute(Long routeId, LocalDate date) {
        List<Trip> trips = tripRepository.findByRoute(routeId, date);
        return trips.stream().map(tripMapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TripDtos.TripResponse> listUpcoming() {
        List<Trip> trips = tripRepository.findUpcomingTrips(LocalDateTime.now());
        return trips.stream().map(tripMapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TripDtos.TripResponse> listBetween(LocalDateTime start, LocalDateTime end) {
        List<Trip> trips = tripRepository.findBetween(start, end);
        return trips.stream().map(tripMapper::toResponse).toList();
    }

    @Override
    public TripDtos.TripResponse update(Long id, TripDtos.TripUpdateRequest request) {
        Trip trip = tripRepository.findById(id)
                .orElseThrow(()-> new NotFoundException("Trip not found"));

        tripMapper.updateEntityFromStatusRequest(request, trip);
        Trip saved = tripRepository.save(trip);
        return tripMapper.toResponse(saved);
    }

    @Override
    public void delete(Long id) {
        Trip trip = tripRepository.findById(id)
                .orElseThrow(()-> new NotFoundException("Trip not found"));

        if (trip.getStatusTrip() == StatusTrip.DEPARTED ||
                trip.getStatusTrip() == StatusTrip.ARRIVED){
            throw new IllegalStateException("Trip has already been departed");
        }

        Bus bus = trip.getBus();
        bus.setStatus(StatusBus.AVAILABLE);
        busRepository.save(bus);

        tripRepository.delete(trip);

    }
    @Override
    @Transactional
    public TripDtos.TripResponse authorizeDeparture(Long tripId, Long driverId) {

        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new NotFoundException("trip not found"));


        if (trip.getDepartureReal() != null) {
            throw new IllegalStateException("Trip already departed");
        }

        Assignment assignment = assignmentRepository.findByTripId(tripId)
                .orElseThrow(() -> new NotFoundException("assignment not found"));


        User driver = userRepository.findById(driverId)
                .orElseThrow(() -> new NotFoundException("driver not found"));

        if (!driver.getId().equals(assignment.getDriver().getId())) {
            throw new IllegalStateException("Driver not assigned to this trip");
        }


        Bus bus = assignment.getTrip().getBus();
        LocalDate today = LocalDate.now();

        if (bus.getSoatExp() == null || bus.getSoatExp().isBefore(today)) {
            throw new IllegalStateException("SOAT expired");
        }

        if (bus.getRevisionExp() == null || bus.getRevisionExp().isBefore(today)) {
            throw new IllegalStateException("Technical inspection expired");
        }

        trip.setDepartureReal(LocalDateTime.now());
        trip.setStatusTrip(StatusTrip.DEPARTED);

        Trip saved = tripRepository.save(trip);

        return tripMapper.toResponse(saved);
    }


}
