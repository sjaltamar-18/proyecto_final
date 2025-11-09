package com.unimag.edu.proyecto_final.domine.repository;

import com.unimag.edu.proyecto_final.domine.entities.Ticket;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TicketRepository extends JpaRepository<Ticket,Long> {

    // encuentra los tickes de un viaje
    @EntityGraph(attributePaths ={"passenger"})
    List<Ticket> findByTripId(Long tripId);

    // encuentra los ticketes activos dentro de un viaje

    @Query("SELECT t FROM Ticket t WHERE t.trip.id = :tripId AND t.status = 'SOLD'")
    List<Ticket> findSoldTicketsByTrip(@Param("tripId") Long tripId);

    // verificar si una silla ya está ocupada en un viaje (por tramo)
    @Query("""
           SELECT COUNT(t) > 0 FROM Ticket t WHERE t.trip.id = :tripId AND t.seatNumber = :seat AND t.statusTicket = 'SOLD'
           AND (
                (t.fromStop.order <= :toOrder AND t.toStop.order > :fromOrder)
           )
           """)
    boolean isSeatOccupied(
            @Param("tripId") Long tripId,
            @Param("seat") String seat,
            @Param("fromOrder") int fromOrder,
            @Param("toOrder") int toOrder
    );

    //  buscar tickets por pasajero (historial)
    @EntityGraph(attributePaths = {"trip", "trip.route"})
    List<Ticket> findByPassengerIdOrderByCreatedAtDesc(Long passengerId);

    // calcular ocupación de un viaje (sillas vendidas / capacidad)
    @Query("SELECT COUNT(t) FROM Ticket t WHERE t.trip.id = :tripId AND t.statusTicket = 'SOLD'")
    int countSoldSeats(@Param("tripId") Long tripId);

    // cancelar ticket (actualización directa)
    @Transactional
    @Modifying
    @Query("UPDATE Ticket t SET t.statusTicket = 'CANCELLED' WHERE t.id = :id")
    void markAsCancelled(@Param("id") Long id);

    // buscar tickets con estado NO_SHOW (para métricas de puntualidad)
    @Query("SELECT t FROM Ticket t WHERE t.trip.id = :tripId AND t.statusTicket = 'NO_SHOW'")
    List<Ticket> findNoShowTickets(@Param("tripId") Long tripId);

    // total de ingresos por viaje (sumatoria de precios)
    @Query("SELECT SUM(t.price) FROM Ticket t WHERE t.trip.id = :tripId AND t.statusTicket = 'SOLD'")
    Double calculateRevenueByTrip(@Param("tripId") Long tripId);

    // buscar por QR (para control de abordaje)
    Optional<Ticket> findByQrCode(String qrCode);

}
