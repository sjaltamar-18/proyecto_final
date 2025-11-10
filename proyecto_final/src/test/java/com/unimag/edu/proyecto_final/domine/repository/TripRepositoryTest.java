package com.unimag.edu.proyecto_final.domine.repository;

import com.unimag.edu.proyecto_final.domine.entities.Bus;
import com.unimag.edu.proyecto_final.domine.entities.Route;
import com.unimag.edu.proyecto_final.domine.entities.Trip;
import com.unimag.edu.proyecto_final.domine.entities.enumera.StatusTrip;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class TripRepositoryTest extends AbstractRepositoryIT {
    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private BusRepository  busRepository;

    @Test
    @DisplayName("Debe encontrar viajes por ruta y fecha")
    void findByRoute() {
        Route route = routeRepository.save(Route.builder().routeCode("ASV123")
                .routeName("Cartagena-santa marta").build());
        Bus bus = busRepository.save(Bus.builder().plate("000123").build());

        Trip trip = tripRepository.save(Trip.builder()
                .bus(bus)
                .route(route)
                .departureAt(LocalDateTime.now().plusHours(2))
                .statusTrip(StatusTrip.SCHEDULED)
                .build());

        List<Trip> result = tripRepository.findByRoute(route.getId(), LocalDate.now());

        assertThat(result).isNotEmpty();
        assertThat(result.get(0).getRoute().getId()).isEqualTo(route.getId());
    }

    @Test
    @DisplayName("Debe encontrar viajes por estado")
    void findByStatusTrip() {
        Route route = routeRepository.save(Route.builder().routeCode("ASV123")
                .routeName("Cartagena-santa marta").build());
        Bus bus = busRepository.save(Bus.builder().plate("000123").build());

        Trip trip = tripRepository.save(Trip.builder()
                .bus(bus)
                .route(route)
                .departureAt(LocalDateTime.now().plusHours(1))
                .statusTrip(StatusTrip.SCHEDULED)
                .build());

        List<Trip> result = tripRepository.findByStatusTrip(StatusTrip.SCHEDULED);

        assertThat(result).contains(trip);
    }

    @Test
    @DisplayName("Debe recuperar viajes próximos o activos")
    void findUpcomingTrips() {
        Route route = routeRepository.save(Route.builder().routeCode("ASV123")
                .routeName("Cartagena-santa marta").build());
        Bus bus = busRepository.save(Bus.builder().plate("000123").build());

        tripRepository.save(Trip.builder()
                .route(route)
                .bus(bus)
                .departureAt(LocalDateTime.now().minusDays(1))
                .statusTrip(StatusTrip.ARRIVED)
                .build());

        Trip upcoming = tripRepository.save(Trip.builder()
                .route(route)
                .bus(bus)
                .departureAt(LocalDateTime.now().plusHours(3))
                .statusTrip(StatusTrip.BOARDING)
                .build());

        List<Trip> result = tripRepository.findUpcomingTrips(LocalDateTime.now());

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(upcoming.getId());
    }

    @Test
    @DisplayName("Debe encontrar viajes entre dos fechas")
    void findBetween() {
        Route route = routeRepository.save(Route.builder().routeCode("ASV123")
                .routeName("Cartagena-santa marta").build());
        Bus bus = busRepository.save(Bus.builder().plate("000123").build());

        Trip t1 = tripRepository.save(Trip.builder()
                .route(route)
                .bus(bus)
                .departureAt(LocalDateTime.now().plusDays(1))
                .statusTrip(StatusTrip.SCHEDULED)
                .build());

        Trip t2 = tripRepository.save(Trip.builder()
                .route(route)
                .bus(bus)
                .departureAt(LocalDateTime.now().plusDays(2))
                .statusTrip(StatusTrip.SCHEDULED)
                .build());

        List<Trip> result = tripRepository.findBetween(
                LocalDateTime.now(), LocalDateTime.now().plusDays(3));

        assertThat(result).contains(t1, t2);
    }
}