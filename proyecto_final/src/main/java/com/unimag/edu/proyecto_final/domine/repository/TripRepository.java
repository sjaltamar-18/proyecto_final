
package com.unimag.edu.proyecto_final.domine.repository;

import com.unimag.edu.proyecto_final.domine.entities.Trip;
import com.unimag.edu.proyecto_final.domine.entities.enumera.StatusTrip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface TripRepository extends JpaRepository<Trip, Long> {


    @Query("""
           SELECT t 
           FROM Trip t 
           WHERE t.route.id = :routeId 
             AND CAST(t.departureAt AS date) = :date 
           ORDER BY t.departureAt ASC
           """)
    List<Trip> findByRoute(@Param("routeId") Long routeId,
                           @Param("date") LocalDate date);


    List<Trip> findByStatusTrip(StatusTrip statusTrip);


    @Query("""
           SELECT t 
           FROM Trip t 
           WHERE t.statusTrip IN ('SCHEDULED', 'BOARDING') 
             AND t.departureAt >= :now 
           ORDER BY t.departureAt ASC
           """)
    List<Trip> findUpcomingTrips(@Param("now") LocalDateTime now);


    @Query("""
           SELECT t 
           FROM Trip t 
           WHERE t.departureAt BETWEEN :start AND :end 
           ORDER BY t.departureAt
           """)
    List<Trip> findBetween(@Param("start") LocalDateTime start,
                           @Param("end") LocalDateTime end);
}
