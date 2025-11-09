package com.unimag.edu.proyecto_final.domine.repository;

import com.unimag.edu.proyecto_final.domine.entities.*;
import com.unimag.edu.proyecto_final.domine.entities.enumera.Role;
import com.unimag.edu.proyecto_final.domine.entities.enumera.StatusSeatHold;
import com.unimag.edu.proyecto_final.domine.entities.enumera.StatusUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class SeatHoldRepositoryTest extends AbstractRepositoryIT{


    @Autowired
    private SeatHoldRepository seatHoldRepository;
    @Autowired
    private TripRepository tripRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BusRepository busRepository;
    @Autowired
    private RouteRepository routeRepository;

    @Test
    @DisplayName("Debe listar los asientos en hold activos por viaje")
    void findByTripIdAndStatus() {
        Route route = routeRepository.save(Route.builder().routeCode("ASV123")
                .routeName("Cartagena-santa marta").build());

        Bus bus = busRepository.save(Bus.builder().plate("000123").build());

        Trip trip = tripRepository.save(Trip.builder()
                .route(route)
                .bus(bus).build());

        User user = userRepository.save(User.builder()
                .name("Usuario Prueba")
                .email("usuario@test.com")
                .passwordHash("123456")
                .role(Role.PASSENGER)
                .status(StatusUser.ACTIVE)
                .build());

        seatHoldRepository.save(SeatHold.builder()
                .user(user)
                .trip(trip)
                .seatNumber("A1")
                .status(StatusSeatHold.HOLD)
                .expirationDate(LocalDateTime.now().plusMinutes(10))
                .build());

        List<SeatHold> result = seatHoldRepository.findByTripIdAndStatus(trip.getId(), StatusSeatHold.HOLD);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getSeatNumber()).isEqualTo("A1");
    }

    @Test
    @DisplayName("Debe verificar si un asiento está en hold activo")
    void isSeatOnHold() {
        Route route = routeRepository.save(Route.builder().routeCode("ASV123")
                .routeName("Cartagena-santa marta").build());

        Bus bus = busRepository.save(Bus.builder().plate("000123").build());

        Trip trip = tripRepository.save(Trip.builder()
                .route(route)
                .bus(bus).build());

        User user = userRepository.save(User.builder()
                .name("Usuario Prueba")
                .email("usuario@test.com")
                .passwordHash("123456")
                .role(Role.PASSENGER)
                .status(StatusUser.ACTIVE)
                .build());

        seatHoldRepository.save(SeatHold.builder()
                .user(user)
                .trip(trip)
                .seatNumber("B4")
                .status(StatusSeatHold.HOLD)
                .expirationDate(LocalDateTime.now().plusMinutes(5))
                .build());

        boolean result = seatHoldRepository.isSeatOnHold(trip.getId(), "B4");

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Debe obtener una reserva específica por número de asiento")
    void findByTripIdAndSeatNumberAndStatus() {
        Route route = routeRepository.save(Route.builder().routeCode("ASV123")
                .routeName("Cartagena-santa marta").build());

        Bus bus = busRepository.save(Bus.builder().plate("000123").build());

        Trip trip = tripRepository.save(Trip.builder()
                .route(route)
                .bus(bus).build());

        User user = userRepository.save(User.builder()
                .name("Usuario Prueba")
                .email("usuario@test.com")
                .passwordHash("123456")
                .role(Role.PASSENGER)
                .status(StatusUser.ACTIVE)
                .build());

        seatHoldRepository.save(SeatHold.builder()
                .user(user)
                .trip(trip)
                .seatNumber("C2")
                .status(StatusSeatHold.HOLD)
                .expirationDate(LocalDateTime.now().plusMinutes(15))
                .build());

        var result = seatHoldRepository.findByTripIdAndSeatNumberAndStatus(trip.getId(), "C2", StatusSeatHold.HOLD);

        assertThat(result).isPresent();
        assertThat(result.get().getSeatNumber()).isEqualTo("C2");
    }

    @Test
    @DisplayName("Debe expirar los asientos cuyo hold ya venció")
    void expireHolds() {
        Route route = routeRepository.save(Route.builder().routeCode("ASV123")
                .routeName("Cartagena-santa marta").build());

        Bus bus = busRepository.save(Bus.builder().plate("000123").build());

        Trip trip = tripRepository.save(Trip.builder()
                .route(route)
                .bus(bus).build());

        User user = userRepository.save(User.builder()
                .name("Usuario Prueba")
                .email("usuario@test.com")
                .passwordHash("123456")
                .role(Role.PASSENGER)
                .status(StatusUser.ACTIVE)
                .build());

        seatHoldRepository.save(SeatHold.builder()
                .user(user)
                .trip(trip)
                .seatNumber("D1")
                .status(StatusSeatHold.HOLD)
                .expirationDate(LocalDateTime.now().minusMinutes(1))
                .build());

        int updated = seatHoldRepository.expireHolds();

        assertThat(updated).isEqualTo(1);
    }

    @Test
    @DisplayName("Debe eliminar asientos expirados antiguos")
    void deleteExpiredOlderThan() {
        Route route = routeRepository.save(Route.builder().routeCode("ASV123")
                .routeName("Cartagena-santa marta").build());

        Bus bus = busRepository.save(Bus.builder().plate("000123").build());

        Trip trip = tripRepository.save(Trip.builder()
                .route(route)
                .bus(bus).build());

        User user = userRepository.save(User.builder()
                .name("Usuario Prueba")
                .email("usuario@test.com")
                .passwordHash("123456")
                .role(Role.PASSENGER)
                .status(StatusUser.ACTIVE)
                .build());

        seatHoldRepository.save(SeatHold.builder()
                .user(user)
                .trip(trip)
                .seatNumber("E1")
                .status(StatusSeatHold.EXPIRED)
                .expirationDate(LocalDateTime.now().minusDays(3))
                .build());

        int deleted = seatHoldRepository.deleteExpiredOlderThan(LocalDateTime.now().minusDays(1));

        assertThat(deleted).isEqualTo(1);
    }

    @Test
    @DisplayName("Debe encontrar los holds activos de un usuario")
    void findByUserIdAndStatus() {
        Route route = routeRepository.save(Route.builder().routeCode("ASV123")
                .routeName("Cartagena-santa marta").build());

        Bus bus = busRepository.save(Bus.builder().plate("000123").build());

        Trip trip = tripRepository.save(Trip.builder()
                .route(route)
                .bus(bus).build());

        User user = userRepository.save(User.builder()
                .name("Usuario Prueba")
                .email("usuario@test.com")
                .passwordHash("123456")
                .role(Role.PASSENGER)
                .status(StatusUser.ACTIVE)
                .build());

        seatHoldRepository.save(SeatHold.builder()
                .user(user)
                .trip(trip)
                .seatNumber("F2")
                .status(StatusSeatHold.HOLD)
                .expirationDate(LocalDateTime.now().plusMinutes(20))
                .build());

        List<SeatHold> result = seatHoldRepository.findByUserIdAndStatus(user.getId(), StatusSeatHold.HOLD);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUser().getId()).isEqualTo(user.getId());
    }
}