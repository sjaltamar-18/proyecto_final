package com.unimag.edu.proyecto_final.service;

import com.unimag.edu.proyecto_final.api.dto.TripDtos;
import com.unimag.edu.proyecto_final.domine.entities.Bus;
import com.unimag.edu.proyecto_final.domine.entities.Route;
import com.unimag.edu.proyecto_final.domine.entities.Trip;
import com.unimag.edu.proyecto_final.domine.entities.enumera.StatusBus;
import com.unimag.edu.proyecto_final.domine.entities.enumera.StatusTrip;
import com.unimag.edu.proyecto_final.domine.repository.BusRepository;
import com.unimag.edu.proyecto_final.domine.repository.RouteRepository;
import com.unimag.edu.proyecto_final.domine.repository.TripRepository;
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
}
