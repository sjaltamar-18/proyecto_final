package com.unimag.edu.proyecto_final.service;

import com.unimag.edu.proyecto_final.api.dto.AssignmentDtos.AssignmentCreateRequest;
import com.unimag.edu.proyecto_final.api.dto.AssignmentDtos.AssignmentResponse;
import com.unimag.edu.proyecto_final.api.dto.AssignmentDtos.AssignmentUpdateRequest;
import com.unimag.edu.proyecto_final.domine.entities.Assignment;
import com.unimag.edu.proyecto_final.domine.entities.Trip;
import com.unimag.edu.proyecto_final.domine.entities.User;
import com.unimag.edu.proyecto_final.domine.entities.enumera.Role;
import com.unimag.edu.proyecto_final.domine.entities.enumera.StatusTrip;
import com.unimag.edu.proyecto_final.domine.repository.AssignmentRepository;
import com.unimag.edu.proyecto_final.domine.repository.TripRepository;
import com.unimag.edu.proyecto_final.domine.repository.UserRepository;
import com.unimag.edu.proyecto_final.exception.NotFoundException;
import com.unimag.edu.proyecto_final.service.mappers.AssignmentMapper;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;


import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class AssignmentServiceImplTest {

    @Mock
    private AssignmentRepository assignmentRepository;

    @Mock
    private TripRepository tripRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AssignmentMapper mapper;

    @Mock private AssignmentMapper assignmentMapper;

    @InjectMocks
    private AssignmentServicelmpl service;



    @BeforeEach
    void setUp() {}


    @Test
    void assign_debe_asignar_correctamente() {

        Trip trip = new Trip();
        trip.setId(1L);

        User driver = new User();
        driver.setId(10L);
        driver.setRole(Role.DRIVER);

        User dispatcher = new User();
        dispatcher.setId(20L);
        dispatcher.setRole(Role.DISPATCHER);

        AssignmentCreateRequest request =
                new AssignmentCreateRequest(
                        1L,               // tripId (aunque no se usa)
                        10L,              // driverId
                        20L,              // dispatcherId
                        true              // checklistOk
                );

        Assignment saved = new Assignment();
        saved.setId(100L);
        saved.setTrip(trip);
        saved.setDriver(driver);
        saved.setDispatcher(dispatcher);
        saved.setChecklistOk(true);
        saved.setAssignedDate(LocalDateTime.now());

        AssignmentResponse dto = new AssignmentResponse(
                saved.getId(),
                trip.getId(),
                driver.getId(),
                dispatcher.getId(),
                true,
                saved.getAssignedDate()
        );

        when(tripRepository.findById(1L)).thenReturn(Optional.of(trip));
        when(userRepository.findById(driver.getId())).thenReturn(Optional.of(driver));
        when(userRepository.findById(dispatcher.getId())).thenReturn(Optional.of(dispatcher));
        when(assignmentRepository.findByTripId(1L)).thenReturn(Optional.empty());
        when(assignmentRepository.save(any())).thenReturn(saved);
        when(assignmentMapper.toResponse(saved)).thenReturn(dto);

        AssignmentResponse result = service.assign(1L, request);

        assertThat(result.id()).isEqualTo(100L);
        assertThat(result.tripId()).isEqualTo(1L);
        assertThat(result.driverId()).isEqualTo(10L);
        assertThat(result.dispatcherId()).isEqualTo(20L);
    }

    // ============================================================
    // 2. Trip no existe
    // ============================================================
    @Test
    void assign_debe_fallar_si_trip_no_existe() {

        AssignmentCreateRequest req =
                new AssignmentCreateRequest(1L, 10L, 20L, true);

        when(tripRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.assign(1L, req))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Trip not found");
    }

    // ============================================================
    // 3. Driver no existe
    // ============================================================
    @Test
    void assign_debe_fallar_si_driver_no_existe() {

        Trip trip = new Trip();
        trip.setId(1L);

        AssignmentCreateRequest req =
                new AssignmentCreateRequest(1L, 10L, 20L, true);

        when(tripRepository.findById(1L)).thenReturn(Optional.of(trip));
        when(userRepository.findById(10L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.assign(1L, req))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Driver not found");
    }

    // ============================================================
    // 4. Driver rol incorrecto
    // ============================================================
    @Test
    void assign_debe_fallar_si_usuario_no_es_driver() {

        Trip trip = new Trip();
        trip.setId(1L);

        User wrongDriver = new User();
        wrongDriver.setId(10L);
        wrongDriver.setRole(Role.CLERK);

        AssignmentCreateRequest req =
                new AssignmentCreateRequest(1L, 10L, 20L, true);

        when(tripRepository.findById(1L)).thenReturn(Optional.of(trip));
        when(userRepository.findById(10L)).thenReturn(Optional.of(wrongDriver));

        assertThatThrownBy(() -> service.assign(1L, req))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("User is not a driver");
    }

    // ============================================================
    // 5. Dispatcher no existe
    // ============================================================
    @Test
    void assign_debe_fallar_si_dispatcher_no_existe() {

        Trip trip = new Trip();
        trip.setId(1L);

        User driver = new User();
        driver.setId(10L);
        driver.setRole(Role.DRIVER);

        AssignmentCreateRequest req =
                new AssignmentCreateRequest(1L, 10L, 20L, true);

        when(tripRepository.findById(1L)).thenReturn(Optional.of(trip));
        when(userRepository.findById(10L)).thenReturn(Optional.of(driver));
        when(userRepository.findById(20L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.assign(1L, req))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Dispatcher not found");
    }

    // ============================================================
    // 6. Dispatcher rol incorrecto
    // ============================================================
    @Test
    void assign_debe_fallar_si_usuario_no_es_dispatcher() {

        Trip trip = new Trip();
        trip.setId(1L);

        User driver = new User();
        driver.setId(10L);
        driver.setRole(Role.DRIVER);

        User wrongDispatcher = new User();
        wrongDispatcher.setId(20L);
        wrongDispatcher.setRole(Role.DRIVER);

        AssignmentCreateRequest req =
                new AssignmentCreateRequest(1L, 10L, 20L, true);

        when(tripRepository.findById(1L)).thenReturn(Optional.of(trip));
        when(userRepository.findById(10L)).thenReturn(Optional.of(driver));
        when(userRepository.findById(20L)).thenReturn(Optional.of(wrongDispatcher));

        assertThatThrownBy(() -> service.assign(1L, req))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("User is not a dispatcher");
    }

    @Test
    void get_debe_devolver_response_si_existe() {
        Assignment assignment = Assignment.builder().id(5L).build();
        when(assignmentRepository.findById(5L)).thenReturn(Optional.of(assignment));

        AssignmentResponse response = mock(AssignmentResponse.class);
        when(mapper.toResponse(assignment)).thenReturn(response);

        var result = service.get(5L);

        assertThat(result).isNotNull();
        verify(assignmentRepository).findById(5L);
        verify(mapper).toResponse(assignment);
    }

    @Test
    void get_debe_lanzar_excepcion_si_no_existe() {
        when(assignmentRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.get(999L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Asignación no encontrada");
    }


    @Test
    void list_debe_devolver_pagina_mapeada() {
        Pageable pageable = PageRequest.of(0, 10);

        Assignment a = Assignment.builder().id(1L).build();
        Assignment b = Assignment.builder().id(2L).build();

        Page<Assignment> page = new PageImpl<>(List.of(a, b), pageable, 2);

        when(assignmentRepository.findAll(pageable)).thenReturn(page);
        when(mapper.toResponse(any())).thenReturn(mock(AssignmentResponse.class));

        var resp = service.list(pageable);

        assertThat(resp.getTotalElements()).isEqualTo(2);
        verify(assignmentRepository).findAll(pageable);
        verify(mapper, times(2)).toResponse(any());
    }

    @Test
    void update_debe_actualizar_y_guardar() {
        Long id = 10L;
        Assignment existing = Assignment.builder().id(id).build();
        when(assignmentRepository.findById(id)).thenReturn(Optional.of(existing));

        AssignmentUpdateRequest req = mock(AssignmentUpdateRequest.class);

        AssignmentResponse mapped = mock(AssignmentResponse.class);
        when(mapper.toResponse(existing)).thenReturn(mapped);

        var result = service.update(id, req);

        verify(mapper).updateEntityFromDto(req, existing);
        verify(assignmentRepository).save(existing);
        verify(mapper).toResponse(existing);
        assertThat(result).isEqualTo(mapped);
    }

    @Test
    void update_debe_fallar_si_no_existe() {
        when(assignmentRepository.findById(55L)).thenReturn(Optional.empty());
        AssignmentUpdateRequest req = mock(AssignmentUpdateRequest.class);

        assertThatThrownBy(() -> service.update(55L, req))
                .isInstanceOf(NotFoundException.class);
    }


    @Test
    void delete_debe_eliminar_si_existe() {
        Long id = 100L;
        Assignment ass = Assignment.builder().id(id).build();

        when(assignmentRepository.findById(id)).thenReturn(Optional.of(ass));

        service.delete(id);

        verify(assignmentRepository).delete(ass);
    }

    @Test
    void delete_debe_fallar_si_no_existe() {
        when(assignmentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(99L))
                .isInstanceOf(NotFoundException.class);
    }
}
