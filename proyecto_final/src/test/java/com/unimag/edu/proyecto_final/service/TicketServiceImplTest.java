package com.unimag.edu.proyecto_final.service;

import com.unimag.edu.proyecto_final.api.dto.TicketDtos;
import com.unimag.edu.proyecto_final.api.dto.TicketDtos.*;
import com.unimag.edu.proyecto_final.domine.entities.*;
import com.unimag.edu.proyecto_final.domine.entities.enumera.StatusTicket;
import com.unimag.edu.proyecto_final.domine.repository.*;

import com.unimag.edu.proyecto_final.exception.NotFoundException;
import com.unimag.edu.proyecto_final.service.mappers.SeatHoldMapper;
import com.unimag.edu.proyecto_final.service.mappers.TicketMapper;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

@ExtendWith(MockitoExtension.class)
class TicketServicelmplTest {

    @Mock private TicketRepository ticketRepository;
    @Mock private TripRepository tripRepository;
    @Mock private UserRepository userRepository;
    @Mock private StopRepository stopRepository;
    @Mock private TicketMapper ticketMapper;
    @Mock private SeatHoldMapper seatHoldMapper;
    @Mock private SeatHoldRepository seatHoldRepository;

    @InjectMocks
    private TicketServicelmpl service;

    @BeforeEach
    void setup() {}

    @Test
    void create_debe_crear_ticket_correctamente() {
        TicketCreateRequest req = mock(TicketCreateRequest.class);

        when(req.tripId()).thenReturn(1L);
        when(req.passengerId()).thenReturn(20L);
        when(req.fromStopId()).thenReturn(3L);
        when(req.toStopId()).thenReturn(4L);
        when(req.seatNumber()).thenReturn("15B");

        Bus bus = Bus.builder().id(10L).capacity(50).build();
        Trip trip = Trip.builder().id(1L).bus(bus).departureAt(LocalDateTime.of(2025, 11, 19, 8, 0))  // fecha de salida
                .arrivalAt(LocalDateTime.of(2025, 11, 19, 12, 0)).build();
        User passenger = User.builder().id(20L).build();
        Stop fromStop = Stop.builder().id(3L).stopOrder(1).build();
        Stop toStop = Stop.builder().id(4L).stopOrder(5).build();

        when(tripRepository.findById(1L)).thenReturn(Optional.of(trip));
        when(userRepository.findById(20L)).thenReturn(Optional.of(passenger));
        when(stopRepository.findById(3L)).thenReturn(Optional.of(fromStop));
        when(stopRepository.findById(4L)).thenReturn(Optional.of(toStop));

        when(ticketRepository.isSeatOccupied(
                1L, "15B", 1, 5
        )).thenReturn(false);

        Ticket ticket = new Ticket();
        when(ticketMapper.toEntity(req)).thenReturn(ticket);

        Ticket saved = Ticket.builder().id(100L).build();
        when(ticketRepository.save(ticket)).thenReturn(saved);

        TicketResponse mapped = mock(TicketResponse.class);
        when(ticketMapper.toResponse(saved)).thenReturn(mapped);

        var result = service.create(req);

        assertThat(result).isEqualTo(mapped);

        verify(tripRepository).findById(1L);
        verify(userRepository).findById(20L);
        verify(stopRepository).findById(3L);
        verify(stopRepository).findById(4L);
        verify(ticketRepository).isSeatOccupied(1L, "15B", 1, 5);
        verify(ticketRepository).save(ticket);
        verify(ticketMapper).toResponse(saved);
    }

    @Test
    void create_debe_fallar_si_trip_no_existe() {
        TicketCreateRequest req = mock(TicketCreateRequest.class);
        when(req.tripId()).thenReturn(999L);

        when(tripRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.create(req))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("trip not found");
    }

    @Test
    void create_debe_fallar_si_passenger_no_existe() {
        TicketCreateRequest req = mock(TicketCreateRequest.class);
        when(req.tripId()).thenReturn(1L);
        when(req.passengerId()).thenReturn(50L);

        when(tripRepository.findById(1L)).thenReturn(Optional.of(new Trip()));
        when(userRepository.findById(50L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.create(req))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("passenger not found");
    }

    @Test
    void create_debe_fallar_si_fromStop_no_existe() {
        TicketCreateRequest req = mock(TicketCreateRequest.class);
        when(req.tripId()).thenReturn(1L);
        when(req.passengerId()).thenReturn(2L);
        when(req.fromStopId()).thenReturn(777L);

        when(tripRepository.findById(1L)).thenReturn(Optional.of(new Trip()));
        when(userRepository.findById(2L)).thenReturn(Optional.of(new User()));
        when(stopRepository.findById(777L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.create(req))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("stop not found");
    }

    @Test
    void create_debe_fallar_si_toStop_no_existe() {
        TicketCreateRequest req = mock(TicketCreateRequest.class);
        when(req.tripId()).thenReturn(1L);
        when(req.passengerId()).thenReturn(2L);
        when(req.fromStopId()).thenReturn(3L);
        when(req.toStopId()).thenReturn(888L);

        Trip trip = Trip.builder()
                .id(1L)
                .bus(Bus.builder().capacity(40).build())
                .departureAt(LocalDateTime.now().plusHours(2))
                .build();

        when(tripRepository.findById(1L)).thenReturn(Optional.of(trip));
        when(userRepository.findById(2L)).thenReturn(Optional.of(new User()));
        when(stopRepository.findById(3L)).thenReturn(Optional.of(new Stop()));
        when(stopRepository.findById(888L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.create(req))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("stop not found");
    }

    @Test
    void create_debe_fallar_si_seat_esta_ocupado() {
        TicketCreateRequest req = mock(TicketCreateRequest.class);

        when(req.tripId()).thenReturn(1L);
        when(req.passengerId()).thenReturn(2L);
        when(req.fromStopId()).thenReturn(3L);
        when(req.toStopId()).thenReturn(4L);
        when(req.seatNumber()).thenReturn("10C");

        Trip trip = Trip.builder()
                .id(1L)
                .bus(Bus.builder().capacity(40).build())
                .departureAt(LocalDateTime.now().plusHours(2))
                .build();

        when(tripRepository.findById(1L)).thenReturn(Optional.of(trip));
        when(userRepository.findById(2L)).thenReturn(Optional.of(new User()));
        when(stopRepository.findById(3L)).thenReturn(Optional.of(Stop.builder().stopOrder(1).build()));
        when(stopRepository.findById(4L)).thenReturn(Optional.of(Stop.builder().stopOrder(5).build()));

        when(ticketRepository.isSeatOccupied(1L, "10C", 1, 5)).thenReturn(true);
        when(seatHoldRepository.isSeatOnHoldByTramo(1L, "10C", 1, 5)).thenReturn(false);
        assertThatThrownBy(() -> service.create(req))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("already occupied");
    }


    @Test
    void get_debe_retornar_ticket_si_existe() {
        Ticket ticket = Ticket.builder().id(10L).build();
        when(ticketRepository.findById(10L)).thenReturn(Optional.of(ticket));

        TicketResponse mapped = mock(TicketResponse.class);
        when(ticketMapper.toResponse(ticket)).thenReturn(mapped);

        var result = service.get(10L);

        assertThat(result).isEqualTo(mapped);
        verify(ticketRepository).findById(10L);
    }

    @Test
    void get_debe_fallar_si_no_existe() {
        when(ticketRepository.findById(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.get(404L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("ticket not found");
    }


    @Test
    void listByTrip_debe_retornar_lista_mapeada() {
        Ticket t = Ticket.builder().id(1L).build();
        when(ticketRepository.findByTripId(10L)).thenReturn(List.of(t));

        TicketResponse mapped = mock(TicketResponse.class);
        when(ticketMapper.toResponse(t)).thenReturn(mapped);

        var result = service.listByTrip(10L);

        assertThat(result).hasSize(1);
        verify(ticketRepository).findByTripId(10L);
    }


    @Test
    void listByPassenger_debe_retornar_lista_mapeada() {
        Ticket t = Ticket.builder().id(1L).build();
        when(ticketRepository.findByPassengerIdOrderByCreatedAtDesc(20L))
                .thenReturn(List.of(t));

        TicketResponse mapped = mock(TicketResponse.class);
        when(ticketMapper.toResponse(t)).thenReturn(mapped);

        var result = service.listByPassenger(20L);

        assertThat(result).hasSize(1);
        verify(ticketRepository).findByPassengerIdOrderByCreatedAtDesc(20L);
    }


    @Test
    void update_debe_modificar_y_guardar() {
        Ticket t = Ticket.builder().id(55L).build();
        when(ticketRepository.findById(55L)).thenReturn(Optional.of(t));

        TicketUpdateRequest req = mock(TicketUpdateRequest.class);

        Ticket saved = Ticket.builder().id(55L).build();
        when(ticketRepository.save(t)).thenReturn(saved);

        TicketResponse mapped = mock(TicketResponse.class);
        when(ticketMapper.toResponse(saved)).thenReturn(mapped);

        var result = service.update(55L, req);

        verify(ticketMapper).updateEntityFromStatusRequest(req, t);
        verify(ticketRepository).save(t);

        assertThat(result).isEqualTo(mapped);
    }

    @Test
    void update_debe_fallar_si_ticket_no_existe() {
        when(ticketRepository.findById(999L)).thenReturn(Optional.empty());

        TicketUpdateRequest req = mock(TicketUpdateRequest.class);

        assertThatThrownBy(() -> service.update(999L, req))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("ticket not found");
    }


    @Test
    void cancel_debe_cancelar_y_reembolsar_90_por_ciento_si_falta_mas_de_24_horas() {

        Long ticketId = 10L;

        Ticket ticket = new Ticket();
        ticket.setId(ticketId);
        ticket.setPrice(100000.0);
        ticket.setStatusTicket(StatusTicket.SOLD);


        Trip trip = new Trip();
        trip.setDepartureAt(LocalDateTime.now().plusHours(30));

        ticket.setTrip(trip);


        TicketDtos.TicketResponse mappedResponse = mock(TicketDtos.TicketResponse.class);


        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(inv -> inv.getArgument(0));
        when(ticketMapper.toResponse(any(Ticket.class))).thenReturn(mappedResponse);


        TicketDtos.TicketResponse result = service.cancel(ticketId);


        assertThat(result).isEqualTo(mappedResponse);


        assertThat(ticket.getStatusTicket()).isEqualTo(StatusTicket.CANCELLED);


        assertThat(ticket.getRefundAmount()).isEqualByComparingTo("90000.0");



        assertThat(ticket.getCancelledAt()).isNotNull();


        verify(ticketRepository).findById(ticketId);
        verify(ticketRepository).save(ticket);
        verify(ticketMapper).toResponse(ticket);
    }
    @Test
    void cancel_debe_fallar_si_ticket_ya_esta_cancelado() {

        Long ticketId = 20L;

        Ticket ticket = new Ticket();
        ticket.setId(ticketId);
        ticket.setStatusTicket(StatusTicket.CANCELLED);

        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));

        assertThatThrownBy(() -> service.cancel(ticketId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("ticket already cancelled");

        verify(ticketRepository, never()).save(any());
    }
    @Test
    void cancel_debe_reembolsar_50_por_ciento_si_falta_entre_12_y_24_horas() {

        Long ticketId = 30L;

        Ticket ticket = new Ticket();
        ticket.setId(ticketId);
        ticket.setPrice(200000.0);
        ticket.setStatusTicket(StatusTicket.SOLD);

        Trip trip = new Trip();
        trip.setDepartureAt(LocalDateTime.now().plusHours(15)); // entre 12 y 24

        ticket.setTrip(trip);

        TicketDtos.TicketResponse mapped = mock(TicketDtos.TicketResponse.class);

        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));
        when(ticketRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(ticketMapper.toResponse(any())).thenReturn(mapped);

        TicketDtos.TicketResponse result = service.cancel(ticketId);

        assertThat(result).isEqualTo(mapped);
        assertThat(ticket.getRefundAmount()).isEqualByComparingTo("100000.0"); // 50%
        assertThat(ticket.getCancelledAt()).isNotNull();
        assertThat(ticket.getStatusTicket()).isEqualTo(StatusTicket.CANCELLED);

        verify(ticketRepository).save(ticket);
        verify(ticketMapper).toResponse(ticket);
    }
    @Test
    void cancel_debe_fallar_si_ticket_no_existe() {

        when(ticketRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.cancel(999L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("ticket not found");

        verify(ticketRepository, never()).save(any());
    }




    @Test
    void getByQrCode_debe_retornar_ticket() {
        Ticket t = Ticket.builder().id(1L).build();
        when(ticketRepository.findByQrCode("QR123")).thenReturn(Optional.of(t));

        TicketResponse mapped = mock(TicketResponse.class);
        when(ticketMapper.toResponse(t)).thenReturn(mapped);

        var result = service.getByQrCode("QR123");

        assertThat(result).isEqualTo(mapped);
        verify(ticketRepository).findByQrCode("QR123");
    }

    @Test
    void getByQrCode_debe_fallar_si_no_existe() {
        when(ticketRepository.findByQrCode(anyString())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getByQrCode("INVALID"))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("QR not found");
    }

}
