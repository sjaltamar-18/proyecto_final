package com.unimag.edu.proyecto_final.domine.repository;

import com.unimag.edu.proyecto_final.domine.entities.*;
import com.unimag.edu.proyecto_final.domine.entities.enumera.Role;
import com.unimag.edu.proyecto_final.domine.entities.enumera.StatusUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class BaggageRepositoryTest extends AbstractRepositoryIT {

    @Autowired
    private BaggageRepository baggageRepository;
    @Autowired
    private TicketRepository ticketRepository;
    @Autowired
    private TripRepository tripRepository;
    @Autowired
    private BusRepository busRepository;
    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Recupere equipajes por ticket")
    void findByTicketId() {
        Route route = routeRepository.save(Route.builder().routeCode("ASV123")
                .routeName("Cartagena-santa marta").build());
        User user = userRepository.save(User.builder()
                .name("Usuario Prueba")
                .email("usuario@test.com")
                .passwordHash("123456")
                .role(Role.PASSENGER)
                .status(StatusUser.ACTIVE)
                .build());

        Bus bus = busRepository.save(Bus.builder().plate("000123").build());

        Trip trip = tripRepository.save(Trip.builder()
                .route(route)
                .bus(bus)
                .build());


        Ticket ticket = ticketRepository.save(Ticket.builder()
                .passenger(user)
                .trip(trip)
                .build());

        Baggage baggage = baggageRepository.save(Baggage.builder()
                .ticket(ticket)
                .weight(12.3)

                .fee(500.0)
                .tagCode("VGO001")
                .build());


        List<Baggage> result = baggageRepository.findByTicketId(ticket.getId());

        // Validaciones
        assertThat(result).isNotEmpty();
        assertThat(result.get(0).getTicket().getId()).isEqualTo(ticket.getId());
        assertThat(result.get(0).getTagCode()).isEqualTo("VGO001");
        assertThat(result.get(0).getWeight()).isEqualTo(12.3);
    }

    @Test
    @DisplayName("Recupere equipajes por viaje")
    void findByTripId() {
        Route route = routeRepository.save(Route.builder().routeCode("ASV123")
                .routeName("Cartagena-santa marta").build());
        User user = userRepository.save(User.builder()
                .name("Usuario Prueba")
                .email("usuario@test.com")
                .passwordHash("123456")
                .role(Role.PASSENGER)
                .status(StatusUser.ACTIVE)
                .build());

        Bus bus = busRepository.save(Bus.builder().plate("000123").build());

        Trip trip = tripRepository.save(Trip.builder()
                .route(route)
                .bus(bus)
                .build());
        Ticket ticket = ticketRepository.save(Ticket.builder()
                .passenger(user)
                .trip(trip)
                .build());

        baggageRepository.save(Baggage.builder()
                .ticket(ticket)
                .weight(10.0)
                .fee(0.0)
                .tagCode("BG002")
                .build());

        List<Baggage> result = baggageRepository.findByTripId(trip.getId());

        assertThat(result).isNotEmpty();
        assertThat(result.get(0).getTicket().getTrip().getId()).isEqualTo(trip.getId());
    }

    @Test
    @DisplayName("Recupere equipajes con sobrepeso")
    void findOverweight() {

        Route route = routeRepository.save(Route.builder().routeCode("ASV123")
                .routeName("Cartagena-santa marta").build());

        User user = userRepository.save(User.builder()
                .name("Usuario Prueba")
                .email("usuario@test.com")
                .passwordHash("123456")
                .role(Role.PASSENGER)
                .status(StatusUser.ACTIVE)
                .build());

        Bus bus = busRepository.save(Bus.builder().plate("000123").build());

        Trip trip = tripRepository.save(Trip.builder()
                .route(route)
                .bus(bus)
                .build());
        Ticket ticket = ticketRepository.save(Ticket.builder()
                .passenger(user)
                .trip(trip)
                .build());

        baggageRepository.save(Baggage.builder()
                .ticket(ticket)
                .weight(30.5)
                .fee(1000.0)
                .tagCode("BG003")
                .build());

        List<Baggage> result = baggageRepository.findOverweight(20.0);

        assertThat(result).isNotEmpty();
        assertThat(result.get(0).getWeight()).isGreaterThan(20.0);
    }

    @Test
    @DisplayName("Calcule peso total de equipajes por viaje")
    void sumWeightByTrip() {

        Route route = routeRepository.save(Route.builder().routeCode("ASV123")
                .routeName("Cartagena-santa marta").build());

        User user = userRepository.save(User.builder()
                .name("Usuario Prueba")
                .email("usuario@test.com")
                .passwordHash("123456")
                .role(Role.PASSENGER)
                .status(StatusUser.ACTIVE)
                .build());

        Bus bus = busRepository.save(Bus.builder().plate("000123").build());

        Trip trip = tripRepository.save(Trip.builder()
                .route(route)
                .bus(bus)
                .build());
        Ticket ticket = ticketRepository.save(Ticket.builder()
                .passenger(user)
                .trip(trip)
                .build());

        baggageRepository.save(Baggage.builder().ticket(ticket).weight(5.0).build());
        baggageRepository.save(Baggage.builder().ticket(ticket).weight(7.5).build());

        Double totalWeight = baggageRepository.sumWeightByTrip(trip.getId());

        assertThat(totalWeight).isEqualTo(12.5);
    }

    @Test
    @DisplayName("Cuente cantidad de equipajes por viaje")
    void countByTripId() {

        Route route = routeRepository.save(Route.builder().routeCode("ASV123")
                .routeName("Cartagena-santa marta").build());

        User user = userRepository.save(User.builder()
                .name("Usuario Prueba")
                .email("usuario@test.com")
                .passwordHash("123456")
                .role(Role.PASSENGER)
                .status(StatusUser.ACTIVE)
                .build());

        Bus bus = busRepository.save(Bus.builder().plate("000123").build());

        Trip trip = tripRepository.save(Trip.builder()
                .route(route)
                .bus(bus)
                .build());
        Ticket ticket = ticketRepository.save(Ticket.builder()
                .passenger(user)
                .trip(trip)
                .build());

        baggageRepository.save(Baggage.builder().ticket(ticket).weight(9.0).build());
        baggageRepository.save(Baggage.builder().ticket(ticket).weight(7.0).build());

        Long count = baggageRepository.countByTripId(trip.getId());

        assertThat(count).isEqualTo(2L);
    }
}
