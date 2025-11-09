package com.unimag.edu.proyecto_final.domine.repository;

import com.unimag.edu.proyecto_final.domine.entities.*;
import com.unimag.edu.proyecto_final.domine.entities.enumera.Role;
import com.unimag.edu.proyecto_final.domine.entities.enumera.StatusBus;
import com.unimag.edu.proyecto_final.domine.entities.enumera.StatusTrip;
import com.unimag.edu.proyecto_final.domine.entities.enumera.StatusUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class AssignmentRepositoryTest extends AbstractRepositoryIT {

    @Autowired
    private AssignmentRepository assignmentRepository;
    @Autowired
    private TripRepository tripRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RouteRepository routeRepository;
    @Autowired
    private BusRepository  busRepository;


    @Test
    @DisplayName("Recupere una asignacion por viaje")
    void findByTripId() {
        Route route = routeRepository.save(Route.builder()
                .routeName("Ruta Prueba")
                .routeCode("RUTA001")
                .build());
        Bus bus = busRepository.save(Bus.builder()
                .plate("ABC-123")
                .status(StatusBus.ACTIVE)
                .build());
        User dispatcher = userRepository.save(User.builder()
                .name("Despachador Test")
                .email("dispatcher@test.com")
                .passwordHash("123456")
                .role(Role.DISPATCHER)
                .status(StatusUser.ACTIVE)
                .build());
        User driver = userRepository.save(User.builder()
                .name("Conductor Test")
                .email("driver@test.com")
                .passwordHash("123456")
                .role(Role.DRIVER)
                .status(StatusUser.ACTIVE)
                .build());

        Trip trip = tripRepository.save(Trip.builder()
                .route(route)
                .bus(bus)
                .date(LocalDate.now())
                .departureAt(LocalDateTime.now())
                .arrivalAt(LocalDateTime.now().plusHours(2))
                .statusTrip(StatusTrip.SCHEDULED)
                .build());
        var assignment =  assignmentRepository.save(
                Assignment.builder()
                        .dispatcher(dispatcher)
                        .driver(driver)
                        .trip(trip)
                        .checklistOk("Sí")
                        .assignedDate(LocalDate.now())
                        .build()
        );

        Optional<Assignment> result = assignmentRepository.findByTripId(trip.getId());

        assertTrue(result.isPresent());
        assertThat(result.get().getTrip().getId()).isEqualTo(trip.getId());
        assertThat(result.get().getId()).isEqualTo(assignment.getId());

    }

    @Test
    @DisplayName("Recupere una asignación por conductor ")
    void findByDriverId() {
        User dispatcher = userRepository.save(User.builder()
                .name("Despachador Test")
                .email("dispatcher@test.com")
                .passwordHash("123456")
                .role(Role.DISPATCHER)
                .status(StatusUser.ACTIVE)
                .build());
        User driver = userRepository.save(User.builder()
                .name("Conductor Prueba")
                .email("correo@test.com")
                .passwordHash("123456")
                .role(Role.DRIVER)
                .status(StatusUser.ACTIVE)
                .build());

        Assignment assignment = assignmentRepository.save(
                Assignment.builder()
                        .dispatcher(dispatcher)
                        .driver(driver)
                        .checklistOk("Si")
                        .assignedDate(LocalDate.now())
                        .build()
        );

        List<Assignment> result = assignmentRepository.findByDriverId(driver.getId());

       assertThat(result).isNotEmpty();
        assertThat(result.get(0).getDriver().getId()).isEqualTo(driver.getId());
        assertThat(result.get(0).getId()).isEqualTo(assignment.getId());
    }

    @Test
    @DisplayName("Recupere una asignación por dispacther ")
    void findByDispatcherId() {
        User driver = userRepository.save(User.builder()
                .name("Conductor Prueba")
                .email("correo-1@test.com")
                .passwordHash("123456")
                .role(Role.DRIVER)
                .status(StatusUser.ACTIVE)
                .build());

        User dispatcher = userRepository.save(User.builder()
                .name("Despachador Prueba")
                .email("correo@test.com")
                .passwordHash("123456")
                .role(Role.DISPATCHER)
                .status(StatusUser.ACTIVE)
                .build());
        Assignment assignment = assignmentRepository.save(Assignment.builder()
                .driver(driver)
                .dispatcher(dispatcher)
                .checklistOk("Si")
                .assignedDate(LocalDate.now())
                .build()
        );
        List<Assignment> result = assignmentRepository.findByDispatcherId(dispatcher.getId());
        assertThat(result).isNotEmpty();
        assertThat(result.get(0).getDispatcher().getId()).isEqualTo(dispatcher.getId());
        assertThat(result.get(0).getId()).isEqualTo(assignment.getId());
    }
}