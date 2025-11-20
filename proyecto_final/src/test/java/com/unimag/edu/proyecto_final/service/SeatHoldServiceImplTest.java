package com.unimag.edu.proyecto_final.service;

import com.unimag.edu.proyecto_final.api.dto.SeatHoldDtos.*;
import com.unimag.edu.proyecto_final.domine.entities.SeatHold;
import com.unimag.edu.proyecto_final.domine.entities.Stop;
import com.unimag.edu.proyecto_final.domine.entities.Trip;
import com.unimag.edu.proyecto_final.domine.entities.User;
import com.unimag.edu.proyecto_final.domine.entities.enumera.StatusSeatHold;
import com.unimag.edu.proyecto_final.domine.repository.*;
import com.unimag.edu.proyecto_final.exception.NotFoundException;
import com.unimag.edu.proyecto_final.service.mappers.SeatHoldMapper;



import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

@ExtendWith(MockitoExtension.class)
class SeatHoldServicelmplTest {

    @Mock
    private SeatHoldRepository seatHoldRepository;

    @Mock
    private TripRepository tripRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SeatHoldMapper seatHoldMapper;

    @Mock
    private StopRepository stopRepository;

    @Mock
    private TicketRepository ticketRepository;

    @InjectMocks
    private SeatHoldServicelmpl service;

    @BeforeEach
    void setUp() {}

    // ==========================================================================
    // CREATE
    // ==========================================================================
    @Test
    void create_debe_crear_seathold_correctamente() {
        SeatHoldCreateRequest req = mock(SeatHoldCreateRequest.class);

        when(req.tripId()).thenReturn(10L);
        when(req.userId()).thenReturn(20L);
        when(req.seatNumber()).thenReturn("12A");

        Trip trip = Trip.builder().id(10L).build();
        trip.setDepartureAt(LocalDateTime.now().plusDays(1));

        Stop fromStop = new Stop();
        fromStop.setStopOrder(1);
        Stop toStop = new Stop();
        toStop.setStopOrder(5);

        User user = User.builder().id(20L).build();

        when(tripRepository.findById(10L)).thenReturn(Optional.of(trip));
        when(userRepository.findById(20L)).thenReturn(Optional.of(user));
        when(seatHoldRepository.isSeatOnHoldByTramo(
                10L,
                "12A",
                fromStop.getStopOrder(),
                toStop.getStopOrder()
        )).thenReturn(false);

        SeatHold seatHold = new SeatHold();
        when(seatHoldMapper.toEntity(req)).thenReturn(seatHold);

        SeatHold saved = SeatHold.builder().id(100L).build();
        when(seatHoldRepository.save(seatHold)).thenReturn(saved);

        SeatHoldResponse mapped = mock(SeatHoldResponse.class);
        when(seatHoldMapper.toResponse(saved)).thenReturn(mapped);



        when(req.fromStopId()).thenReturn(1L);
        when(req.toStopId()).thenReturn(2L);
        when(stopRepository.findById(1L)).thenReturn(Optional.of(fromStop));
        when(stopRepository.findById(2L)).thenReturn(Optional.of(toStop));

        var result = service.create(req);

        assertThat(result).isEqualTo(mapped);
        verify(tripRepository).findById(10L);
        verify(userRepository).findById(20L);
        verify(seatHoldRepository).isSeatOnHoldByTramo(
                10L, "12A", fromStop.getStopOrder(), toStop.getStopOrder()
        );
        verify(seatHoldRepository).save(seatHold);
    }

    @Test
    void create_debe_fallar_si_trip_no_existe() {
        SeatHoldCreateRequest req = mock(SeatHoldCreateRequest.class);
        when(req.tripId()).thenReturn(99L);

        when(tripRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.create(req))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("trip not found");
    }

    @Test
    void create_debe_fallar_si_user_no_existe() {
        SeatHoldCreateRequest req = mock(SeatHoldCreateRequest.class);
        when(req.tripId()).thenReturn(10L);
        when(req.userId()).thenReturn(20L);

        when(tripRepository.findById(10L)).thenReturn(Optional.of(new Trip()));
        when(userRepository.findById(20L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.create(req))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("user not found");
    }

    @Test
    void create_debe_fallar_si_seat_ya_esta_on_hold() {
        SeatHoldCreateRequest req = mock(SeatHoldCreateRequest.class);
        when(req.tripId()).thenReturn(10L);
        when(req.userId()).thenReturn(20L);
        when(req.seatNumber()).thenReturn("12A");
        when(req.fromStopId()).thenReturn(1L);
        when(req.toStopId()).thenReturn(2L);

        Stop fromStop = new Stop();
        fromStop.setStopOrder(1);
        Stop toStop = new Stop();
        toStop.setStopOrder(5);

        when(stopRepository.findById(1L)).thenReturn(Optional.of(fromStop));
        when(stopRepository.findById(2L)).thenReturn(Optional.of(toStop));

        Trip trip = new Trip();
        trip.setId(10L);
        when(tripRepository.findById(10L)).thenReturn(Optional.of(trip));

        when(userRepository.findById(20L)).thenReturn(Optional.of(new User()));

        when(seatHoldRepository.isSeatOnHoldByTramo(
                10L,
                "12A",
                fromStop.getStopOrder(),
                toStop.getStopOrder()
        )).thenReturn(true);

        assertThatThrownBy(() -> service.create(req))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Seat is temporarily hold in this tramo");
    }

    // ==========================================================================
    // GET
    // ==========================================================================
    @Test
    void get_debe_retornar_seathold_si_existe() {
        SeatHold seatHold = SeatHold.builder().id(5L).build();
        when(seatHoldRepository.findById(5L)).thenReturn(Optional.of(seatHold));

        SeatHoldResponse mapped = mock(SeatHoldResponse.class);
        when(seatHoldMapper.toResponse(seatHold)).thenReturn(mapped);

        var result = service.get(5L);

        assertThat(result).isEqualTo(mapped);
        verify(seatHoldRepository).findById(5L);
    }

    @Test
    void get_debe_fallar_si_no_existe() {
        when(seatHoldRepository.findById(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.get(404L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("seatHold not found");
    }

    // ==========================================================================
    // LISTS
    // ==========================================================================
    @Test
    void listActiveByTrip_debe_retornar_holds() {
        SeatHold sh = SeatHold.builder().id(1L).status(StatusSeatHold.HOLD).build();
        when(seatHoldRepository.findByTripIdAndStatus(10L, StatusSeatHold.HOLD))
                .thenReturn(List.of(sh));

        SeatHoldResponse mapped = mock(SeatHoldResponse.class);
        when(seatHoldMapper.toResponse(sh)).thenReturn(mapped);

        var result = service.listActiveByTrip(10L);

        assertThat(result).hasSize(1);
        verify(seatHoldRepository).findByTripIdAndStatus(10L, StatusSeatHold.HOLD);
    }

    @Test
    void listByUser_debe_retornar_holds() {
        SeatHold sh = SeatHold.builder().id(1L).status(StatusSeatHold.HOLD).build();
        when(seatHoldRepository.findByUserIdAndStatus(20L, StatusSeatHold.HOLD))
                .thenReturn(List.of(sh));

        when(seatHoldMapper.toResponse(sh)).thenReturn(mock(SeatHoldResponse.class));

        var result = service.listByUser(20L);

        assertThat(result).hasSize(1);
        verify(seatHoldRepository).findByUserIdAndStatus(20L, StatusSeatHold.HOLD);
    }


    @Test
    void update_debe_modificar_y_guardar() {
        SeatHold seatHold = SeatHold.builder().id(15L).build();
        when(seatHoldRepository.findById(15L)).thenReturn(Optional.of(seatHold));

        SeatHoldUpdateRequest req = mock(SeatHoldUpdateRequest.class);

        SeatHold saved = SeatHold.builder().id(15L).build();
        when(seatHoldRepository.save(seatHold)).thenReturn(saved);

        SeatHoldResponse mapped = mock(SeatHoldResponse.class);
        when(seatHoldMapper.toResponse(saved)).thenReturn(mapped);

        var result = service.update(15L, req);

        verify(seatHoldMapper).updateEntityFromStatusRequest(req, seatHold);
        verify(seatHoldRepository).save(seatHold);

        assertThat(result).isEqualTo(mapped);
    }

    @Test
    void update_debe_fallar_si_no_existe() {
        when(seatHoldRepository.findById(999L)).thenReturn(Optional.empty());

        SeatHoldUpdateRequest req = mock(SeatHoldUpdateRequest.class);

        assertThatThrownBy(() -> service.update(999L, req))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("seatHold not found");
    }


    @Test
    void expireHolds_debe_retornar_numero_de_registros() {
        when(seatHoldRepository.expireHolds()).thenReturn(7);

        int result = service.expireHolds();

        assertThat(result).isEqualTo(7);
        verify(seatHoldRepository).expireHolds();
    }

    @Test
    void cleanOldExpired_debe_retornar_numero_de_registros() {
        when(seatHoldRepository.deleteExpiredOlderThan(any())).thenReturn(3);

        int result = service.cleanOldExpired();

        assertThat(result).isEqualTo(3);
        verify(seatHoldRepository).deleteExpiredOlderThan(any());
    }


    @Test
    void delete_debe_eliminar_si_existe() {
        SeatHold sh = SeatHold.builder().id(40L).build();
        when(seatHoldRepository.findById(40L)).thenReturn(Optional.of(sh));

        service.delete(40L);

        verify(seatHoldRepository).delete(sh);
    }

    @Test
    void delete_debe_fallar_si_no_existe() {
        when(seatHoldRepository.findById(777L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(777L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("seatHold not found");
    }
}
