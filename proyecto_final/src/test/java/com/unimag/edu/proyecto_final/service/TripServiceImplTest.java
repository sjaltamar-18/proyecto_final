package com.unimag.edu.proyecto_final.service;

import com.unimag.edu.proyecto_final.api.dto.TripDtos;
import com.unimag.edu.proyecto_final.api.dto.TripDtos.*;
import com.unimag.edu.proyecto_final.domine.entities.*;
import com.unimag.edu.proyecto_final.domine.entities.enumera.*;
import com.unimag.edu.proyecto_final.domine.repository.*;

import com.unimag.edu.proyecto_final.exception.NotFoundException;
import com.unimag.edu.proyecto_final.service.mappers.TripMapper;



import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

@ExtendWith(MockitoExtension.class)
class TripServicelmplTest {

    @Mock private TripRepository tripRepository;
    @Mock private RouteRepository routeRepository;
    @Mock private BusRepository busRepository;
    @Mock private TripMapper tripMapper;
    @Mock
    private AssignmentRepository assignmentRepository;

    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private TripServicelmpl service;


    @Test
    void create_debe_crear_trip_correctamente() {
        TripCreateRequest req = mock(TripCreateRequest.class);
        when(req.routeId()).thenReturn(1L);
        when(req.busId()).thenReturn(10L);

        Route route = Route.builder().id(1L).build();
        Bus bus = Bus.builder().id(10L).status(StatusBus.AVAILABLE).build();

        when(routeRepository.findById(1L)).thenReturn(Optional.of(route));
        when(busRepository.findById(10L)).thenReturn(Optional.of(bus));

        when(tripRepository.findByStatusTrip(StatusTrip.SCHEDULED))
                .thenReturn(List.of());

        Trip tripEntity = new Trip();
        when(tripMapper.toEntity(req)).thenReturn(tripEntity);

        Trip savedTrip = Trip.builder().id(100L).build();
        when(tripRepository.save(tripEntity)).thenReturn(savedTrip);

        TripResponse mapped = mock(TripResponse.class);
        when(tripMapper.toResponse(savedTrip)).thenReturn(mapped);

        var result = service.create(req);

        assertThat(result).isEqualTo(mapped);

        verify(busRepository).save(bus);
        assertThat(bus.getStatus()).isEqualTo(StatusBus.ACTIVE);
    }

    @Test
    void create_falla_si_route_no_existe() {
        TripCreateRequest req = mock(TripCreateRequest.class);
        when(req.routeId()).thenReturn(1L);

        when(routeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.create(req))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Route not found");
    }

    @Test
    void create_falla_si_bus_no_existe() {
        TripCreateRequest req = mock(TripCreateRequest.class);
        when(req.routeId()).thenReturn(1L);
        when(req.busId()).thenReturn(20L);

        when(routeRepository.findById(1L)).thenReturn(Optional.of(new Route()));
        when(busRepository.findById(20L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.create(req))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Bus not found");
    }

    @Test
    void create_falla_si_bus_no_esta_available() {
        TripCreateRequest req = mock(TripCreateRequest.class);
        when(req.routeId()).thenReturn(1L);
        when(req.busId()).thenReturn(20L);

        when(routeRepository.findById(1L)).thenReturn(Optional.of(new Route()));

        Bus bus = Bus.builder().status(StatusBus.ACTIVE).build();
        when(busRepository.findById(20L)).thenReturn(Optional.of(bus));

        assertThatThrownBy(() -> service.create(req))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Bus not available");
    }

    @Test
    void create_falla_si_bus_ya_esta_en_trip_activo() {
        TripCreateRequest req = mock(TripCreateRequest.class);
        when(req.routeId()).thenReturn(1L);
        when(req.busId()).thenReturn(20L);

        Route route = new Route();
        Bus bus = Bus.builder().id(20L).status(StatusBus.AVAILABLE).build();

        when(routeRepository.findById(1L)).thenReturn(Optional.of(route));
        when(busRepository.findById(20L)).thenReturn(Optional.of(bus));

        Trip activeTrip = Trip.builder().bus(bus).build();
        when(tripRepository.findByStatusTrip(StatusTrip.SCHEDULED))
                .thenReturn(List.of(activeTrip));

        assertThatThrownBy(() -> service.create(req))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Bus already in trip");
    }


    @Test
    void get_debe_retornar_trip_si_existe() {
        Trip t = Trip.builder().id(5L).build();
        when(tripRepository.findById(5L)).thenReturn(Optional.of(t));

        TripResponse mapped = mock(TripResponse.class);
        when(tripMapper.toResponse(t)).thenReturn(mapped);

        var result = service.get(5L);

        assertThat(result).isEqualTo(mapped);
    }

    @Test
    void get_falla_si_no_existe() {
        when(tripRepository.findById(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.get(404L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Trip not found");
    }


    @Test
    void listByRoute_retornar_trips_mapeados() {
        Trip trip = Trip.builder().id(1L).build();
        when(tripRepository.findByRoute(10L, LocalDate.of(2024, 1, 1)))
                .thenReturn(List.of(trip));

        TripResponse mapped = mock(TripResponse.class);
        when(tripMapper.toResponse(trip)).thenReturn(mapped);

        var result = service.listByRoute(10L, LocalDate.of(2024, 1, 1));

        assertThat(result).hasSize(1);
    }


    @Test
    void listUpcoming_retornar_trips_mapeados() {
        Trip trip = Trip.builder().id(7L).build();
        when(tripRepository.findUpcomingTrips(any(LocalDateTime.class)))
                .thenReturn(List.of(trip));

        TripResponse mapped = mock(TripResponse.class);
        when(tripMapper.toResponse(trip)).thenReturn(mapped);

        var result = service.listUpcoming();

        assertThat(result).hasSize(1);
    }


    void listBetween_retornar_trips_mapeados() {
        Trip trip = Trip.builder().id(2L).build();
        when(tripRepository.findBetween(any(), any()))
                .thenReturn(List.of(trip));

        TripResponse mapped = mock(TripResponse.class);
        when(tripMapper.toResponse(trip)).thenReturn(mapped);

        var result = service.listBetween(
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(1)
        );

        assertThat(result).hasSize(1);
    }


    @Test
    void update_modifica_y_guarda() {
        Trip trip = Trip.builder().id(9L).build();
        when(tripRepository.findById(9L)).thenReturn(Optional.of(trip));

        TripUpdateRequest req = mock(TripUpdateRequest.class);

        Trip saved = Trip.builder().id(9L).build();
        when(tripRepository.save(trip)).thenReturn(saved);

        TripResponse mapped = mock(TripResponse.class);
        when(tripMapper.toResponse(saved)).thenReturn(mapped);

        var result = service.update(9L, req);

        verify(tripMapper).updateEntityFromStatusRequest(req, trip);
        verify(tripRepository).save(trip);

        assertThat(result).isEqualTo(mapped);
    }

    @Test
    void update_falla_si_trip_no_existe() {
        when(tripRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(999L, mock(TripUpdateRequest.class)))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Trip not found");
    }


    @Test
    void delete_funciona_y_pone_bus_available() {
        Bus bus = Bus.builder().id(22L).status(StatusBus.ACTIVE).build();
        Trip trip = Trip.builder()
                .id(1L)
                .statusTrip(StatusTrip.SCHEDULED)
                .bus(bus)
                .build();

        when(tripRepository.findById(1L)).thenReturn(Optional.of(trip));

        service.delete(1L);

        assertThat(bus.getStatus()).isEqualTo(StatusBus.AVAILABLE);

        verify(busRepository).save(bus);
        verify(tripRepository).delete(trip);
    }

    @Test
    void delete_falla_si_trip_es_departed_o_arrived() {
        Trip trip = Trip.builder().id(1L).statusTrip(StatusTrip.DEPARTED).build();

        when(tripRepository.findById(1L)).thenReturn(Optional.of(trip));

        assertThatThrownBy(() -> service.delete(1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("already been departed");
    }

    @Test
    void delete_falla_si_no_existe() {
        when(tripRepository.findById(1000L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(1000L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Trip not found");
    }
    @Test
    void authorizeDeparture_debe_autorizar_salida_correctamente() {

        Long tripId = 10L;
        Long driverId = 5L;


        Trip trip = Trip.builder()
                .id(tripId)
                .departureReal(null) // no ha salido
                .statusTrip(StatusTrip.SCHEDULED)
                .build();

        Bus bus = Bus.builder()
                .id(2L)
                .soatExp(LocalDate.now().plusDays(10))
                .revisionExp(LocalDate.now().plusDays(10))
                .build();


        User driver = User.builder()
                .id(driverId)
                .build();


        Assignment assignment = Assignment.builder()
                .id(99L)
                .driver(driver)
                .trip(trip)
                .build();


        trip.setBus(bus);

        TripDtos.TripResponse mappedResponse = mock(TripDtos.TripResponse.class);

        when(tripRepository.findById(tripId)).thenReturn(Optional.of(trip));
        when(assignmentRepository.findByTripId(tripId)).thenReturn(Optional.of(assignment));
        when(userRepository.findById(driverId)).thenReturn(Optional.of(driver));
        when(tripRepository.save(any(Trip.class))).thenAnswer(inv -> inv.getArgument(0));
        when(tripMapper.toResponse(any(Trip.class))).thenReturn(mappedResponse);


        TripDtos.TripResponse result = service.authorizeDeparture(tripId, driverId);


        assertThat(result).isEqualTo(mappedResponse);
        assertThat(trip.getDepartureReal()).isNotNull();
        assertThat(trip.getStatusTrip()).isEqualTo(StatusTrip.DEPARTED);


        verify(tripRepository).findById(tripId);
        verify(assignmentRepository).findByTripId(tripId);
        verify(userRepository).findById(driverId);
        verify(tripRepository).save(trip);
        verify(tripMapper).toResponse(trip);
    }

}
