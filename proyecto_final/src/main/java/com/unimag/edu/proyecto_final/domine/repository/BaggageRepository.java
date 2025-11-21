package com.unimag.edu.proyecto_final.domine.repository;

import com.unimag.edu.proyecto_final.domine.entities.Baggage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BaggageRepository extends JpaRepository<Baggage,Long> {

    @Query("""
           SELECT b FROM Baggage b
           WHERE b.ticket.id = :ticketId
           """)
    List<Baggage> findByTicketId(@Param("ticketId") Long ticketId);


    @Query("""
           SELECT b FROM Baggage b
           WHERE b.ticket.trip.id = :tripId
           """)
    List<Baggage> findByTripId(@Param("tripId") Long tripId);

    @Query("""
           SELECT b FROM Baggage b
           WHERE b.weight > :maxWeight
           """)
    List<Baggage> findOverweight(@Param("maxWeight") double maxWeight);


    @Query("""
           SELECT SUM(b.weight) FROM Baggage b
           WHERE b.ticket.trip.id = :tripId
           """)
    Double sumWeightByTrip(@Param("tripId") Long tripId);

    @Query("""
           SELECT COUNT(b) FROM Baggage b
           WHERE b.ticket.trip.id = :tripId
           """)
    Long countByTripId(@Param("tripId") Long tripId);
}

