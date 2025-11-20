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

    @InjectMocks
    private AssignmentServicelmpl service;

    @BeforeEach
    void setUp() {}


    @Test
    void create_debe_crear_assignment_correctamente() {
        // DTO mock (record → se mockean sus métodos)
        AssignmentCreateRequest req = mock(AssignmentCreateRequest.class);
        when(req.tripId()).thenReturn(10L);
        when(req.driverId()).thenReturn(20L);
        when(req.dispatcherId()).thenReturn(30L);
        when(req.checklistOk()).thenReturn(true);

        // Trip existente y con status SCHEDULED
        Trip trip = Trip.builder()
                .id(10L)
                .statusTrip(StatusTrip.SCHEDULED)
                .build();

        User driver = User.builder()
                .id(20L)
                .role(Role.DRIVER)
                .build();

        User dispatcher = User.builder()
                .id(30L)
                .role(Role.DISPATCHER)
                .build();

        Assignment savedAssignment = Assignment.builder()
                .id(100L)
                .trip(trip)
                .driver(driver)
                .dispatcher(dispatcher)
                .assignedDate(LocalDateTime.now())
                .checklistOk(true)
                .build();

        when(tripRepository.findById(10L)).thenReturn(Optional.of(trip));
        when(userRepository.findById(20L)).thenReturn(Optional.of(driver));
        when(userRepository.findById(30L)).thenReturn(Optional.of(dispatcher));
        when(assignmentRepository.save(any(Assignment.class))).thenReturn(savedAssignment);

        AssignmentResponse mappedResponse = mock(AssignmentResponse.class);
        when(mapper.toResponse(any())).thenReturn(mappedResponse);

        var result = service.create(req);

        assertThat(result).isNotNull();
        verify(tripRepository).save(trip); // El viaje cambia a BOARDING
        assertThat(trip.getStatusTrip()).isEqualTo(StatusTrip.BOARDING);

        verify(assignmentRepository).save(any(Assignment.class));
        verify(mapper).toResponse(any(Assignment.class));
    }

    @Test
    void create_debe_fallar_si_trip_no_existe() {
        AssignmentCreateRequest req = mock(AssignmentCreateRequest.class);
        when(req.tripId()).thenReturn(10L);

        when(tripRepository.findById(10L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.create(req))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Trip not found");
    }

    @Test
    void create_debe_fallar_si_trip_no_esta_scheduled() {
        AssignmentCreateRequest req = mock(AssignmentCreateRequest.class);
        when(req.tripId()).thenReturn(10L);

        Trip trip = Trip.builder().id(10L).statusTrip(StatusTrip.DEPARTED).build();
        when(tripRepository.findById(10L)).thenReturn(Optional.of(trip));

        assertThatThrownBy(() -> service.create(req))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Trip is already scheduled");
    }

    @Test
    void create_debe_fallar_si_driver_no_es_driver() {
        AssignmentCreateRequest req = mock(AssignmentCreateRequest.class);
        when(req.tripId()).thenReturn(10L);
        when(req.driverId()).thenReturn(20L);

        Trip trip = Trip.builder().id(10L).statusTrip(StatusTrip.SCHEDULED).build();
        when(tripRepository.findById(10L)).thenReturn(Optional.of(trip));

        User invalid = User.builder().id(20L).role(Role.PASSENGER).build();
        when(userRepository.findById(20L)).thenReturn(Optional.of(invalid));

        assertThatThrownBy(() -> service.create(req))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Driver is not a driver");
    }

    @Test
    void create_debe_fallar_si_dispatcher_no_es_dispatcher() {
        AssignmentCreateRequest req = mock(AssignmentCreateRequest.class);
        when(req.tripId()).thenReturn(10L);
        when(req.driverId()).thenReturn(20L);
        when(req.dispatcherId()).thenReturn(30L);

        Trip trip = Trip.builder().id(10L).statusTrip(StatusTrip.SCHEDULED).build();
        User driver = User.builder().id(20L).role(Role.DRIVER).build();
        User invalidDispatcher = User.builder().id(30L).role(Role.ADMIN).build();

        when(tripRepository.findById(10L)).thenReturn(Optional.of(trip));
        when(userRepository.findById(20L)).thenReturn(Optional.of(driver));
        when(userRepository.findById(30L)).thenReturn(Optional.of(invalidDispatcher));

        assertThatThrownBy(() -> service.create(req))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Dispatcher is not a dispatcher");
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
