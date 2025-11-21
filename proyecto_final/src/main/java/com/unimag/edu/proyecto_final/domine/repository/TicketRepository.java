package com.unimag.edu.proyecto_final.domine.repository;
import com.unimag.edu.proyecto_final.domine.entities.enumera.StatusTicket;
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


    @EntityGraph(attributePaths ={"passenger"})
    List<Ticket> findByTripId(Long tripId);



    @Query("SELECT t FROM Ticket t WHERE t.trip.id = :tripId AND t.statusTicket = 'SOLD'")
    List<Ticket> findSoldTicketsByTrip(@Param("tripId") Long tripId);


    @Query("""
           SELECT COUNT(t) > 0 FROM Ticket t WHERE t.trip.id = :tripId AND t.seatNumber = :seat AND t.statusTicket = 'SOLD'
           AND (
                (t.fromStop.stopOrder <= :toOrder AND t.toStop.stopOrder > :fromOrder)
           )
           """)
    boolean isSeatOccupied(
            @Param("tripId") Long tripId,
            @Param("seat") String seat,
            @Param("fromOrder") int fromOrder,
            @Param("toOrder") int toOrder
    );

    @Query("""
       SELECT COUNT(t) > 0
       FROM Ticket t
       WHERE t.trip.id = :tripId
         AND t.seatNumber = :seatNumber
         AND t.statusTicket = com.unimag.edu.proyecto_final.domine.entities.enumera.StatusTicket.SOLD
       """)
    boolean isSeatSold(Long tripId, String seatNumber);

    @EntityGraph(attributePaths = {"trip", "trip.route"})
    List<Ticket> findByPassengerIdOrderByCreatedAtDesc(Long passengerId);


    @Query("SELECT COUNT(t) FROM Ticket t WHERE t.trip.id = :tripId AND t.statusTicket = 'SOLD'")
    int countSoldSeats(@Param("tripId") Long tripId);

    @Transactional
    @Modifying
    @Query("UPDATE Ticket t SET t.statusTicket = 'CANCELLED' WHERE t.id = :id")
    void markAsCancelled(@Param("id") Long id);


    @Query("""
       SELECT t
       FROM Ticket t
       WHERE t.trip.id = :tripId
         AND t.statusTicket = :status
         AND t.trip.departureAt < :limit
       """)
    List<Ticket> findNoShowTickets(@Param("tripId") Long tripId,
                                   @Param("status") StatusTicket status,
                                   @Param("limit") LocalDateTime limit);


    @Query("SELECT SUM(t.price) FROM Ticket t WHERE t.trip.id = :tripId AND t.statusTicket = 'SOLD'")
    Double calculateRevenueByTrip(@Param("tripId") Long tripId);

    Optional<Ticket> findByQrCode(String qrCode);


}
