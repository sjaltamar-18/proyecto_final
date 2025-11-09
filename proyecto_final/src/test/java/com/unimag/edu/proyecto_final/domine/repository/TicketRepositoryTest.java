package com.unimag.edu.proyecto_final.domine.repository;

import com.unimag.edu.proyecto_final.domine.entities.*;
import com.unimag.edu.proyecto_final.domine.entities.enumera.Role;
import com.unimag.edu.proyecto_final.domine.entities.enumera.StatusTicket;
import com.unimag.edu.proyecto_final.domine.entities.enumera.StatusUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class TicketRepositoryTest extends AbstractRepositoryIT {
    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private BusRepository busRepository;

    @Autowired
    private StopRepository stopRepository;

    @Test
    @DisplayName("Debe encontrar los tickets por ID de viaje")
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
                .trip(trip)
                .passenger(user)
                .statusTicket(StatusTicket.SOLD)
                .price(40000.0)
                .build());

        List<Ticket> result = ticketRepository.findByTripId(trip.getId());

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTrip().getId()).isEqualTo(trip.getId());
    }

    @Test
    @DisplayName("Debe encontrar los tickets vendidos en un viaje")
    void findSoldTicketsByTrip() {
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

        ticketRepository.save(Ticket.builder()
                .trip(trip)
                .passenger(user)
                .statusTicket(StatusTicket.SOLD)
                .build());

        List<Ticket> result = ticketRepository.findSoldTicketsByTrip(trip.getId());

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatusTicket()).isEqualTo(StatusTicket.SOLD);
    }

    @Test
    @DisplayName("Debe verificar si una silla está ocupada en un viaje")
    void isSeatOccupied() {
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
        Stop fromStop = stopRepository.save(Stop.builder().stopName("A").stopOrder(1).route(route).build());
        Stop toStop = stopRepository.save(Stop.builder().stopName("B").stopOrder(2).route(route).build());

        ticketRepository.save(Ticket.builder()
                .trip(trip)
                .passenger(user)
                .fromStop(fromStop)
                .toStop(toStop)
                .seatNumber("A1")
                .statusTicket(StatusTicket.SOLD)
                .build());

        boolean result = ticketRepository.isSeatOccupied(trip.getId(), "A1", 1, 5);

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Debe encontrar tickets por pasajero ordenados por fecha")
    void findByPassengerIdOrderByCreatedAtDesc() {
        Route route = routeRepository.save(Route.builder().routeCode("ASV123")
                .routeName("Cartagena-santa marta").build());

        User passenger = userRepository.save(User.builder()
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

        ticketRepository.save(Ticket.builder()
                .trip(trip)
                .passenger(passenger)
                .statusTicket(StatusTicket.SOLD)
                .build());

        List<Ticket> result = ticketRepository.findByPassengerIdOrderByCreatedAtDesc(passenger.getId());

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getPassenger().getId()).isEqualTo(passenger.getId());
    }

    @Test
    @DisplayName("Debe contar los asientos vendidos por viaje")
    void countSoldSeats() {
        User user = userRepository.save(User.builder()
                .name("Usuario Prueba")
                .email("usuario@test.com")
                .passwordHash("123456")
                .role(Role.PASSENGER)
                .status(StatusUser.ACTIVE)
                .build());
        Route route = routeRepository.save(Route.builder().routeCode("ASV123")
                .routeName("Cartagena-santa marta").build());


        Bus bus = busRepository.save(Bus.builder().plate("000123").build());

        Trip trip = tripRepository.save(Trip.builder()
                .route(route)
                .bus(bus)
                .build());

        ticketRepository.save(Ticket.builder().trip(trip)
                .passenger(user).statusTicket(StatusTicket.SOLD).build());
        ticketRepository.save(Ticket.builder().trip(trip)
                .passenger(user).statusTicket(StatusTicket.SOLD).build());

        int count = ticketRepository.countSoldSeats(trip.getId());

        assertThat(count).isEqualTo(2);

    }

    @Test
    @DisplayName("Debe marcar un ticket como cancelado")
    void markAsCancelled() {
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
                .trip(trip)
                .passenger(user)
                .statusTicket(StatusTicket.CANCELLED)
                .build());

        ticketRepository.markAsCancelled(ticket.getId());

        Ticket updated = ticketRepository.findById(ticket.getId())
                .orElseThrow(() -> new IllegalStateException("Ticket no encontrado"));


        assertThat(updated.getStatusTicket()).isEqualTo(StatusTicket.CANCELLED);
    }

    @Test
    @DisplayName("Debe encontrar tickets con estado NO_SHOW")
    void findNoShowTickets() {
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


        ticketRepository.save(Ticket.builder()
                .trip(trip)
                .passenger(user)
                .statusTicket(StatusTicket.NO_SHOW)
                .build());

        List<Ticket> result = ticketRepository.findNoShowTickets(trip.getId());

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatusTicket()).isEqualTo(StatusTicket.NO_SHOW);
    }

    @Test
    @DisplayName("Debe calcular los ingresos por viaje")
    void calculateRevenueByTrip() {

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

        ticketRepository.save(Ticket.builder()
                .trip(trip)
                .passenger(user)
                .statusTicket(StatusTicket.SOLD)
                .price(20000.0)
                .build());
        ticketRepository.save(Ticket.builder()
                .passenger(user)
                .trip(trip)
                .statusTicket(StatusTicket.SOLD)
                .price(30000.0)
                .build());

        Double total = ticketRepository.calculateRevenueByTrip(trip.getId());

        assertThat(total).isEqualTo(50000.0);
    }

    @Test
    @DisplayName("Debe encontrar ticket por QR code")
    void findByQrCode() {
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
                .trip(trip)
                .passenger(user)
                .qrCode("QR123")
                .statusTicket(StatusTicket.SOLD)
                .build());

        var result = ticketRepository.findByQrCode("QR123");

        assertThat(result).isPresent();
        assertThat(result.get().getQrCode()).isEqualTo("QR123");
    }
}