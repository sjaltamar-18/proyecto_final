package com.unimag.edu.proyecto_final.domine.repository;

import com.unimag.edu.proyecto_final.domine.entities.Trip;
import com.unimag.edu.proyecto_final.domine.entities.enumera.StatusTrip;
import com.unimag.edu.proyecto_final.domine.entities.enumera.StatusUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public interface TripRepository extends JpaRepository<Trip,Long> {
// query para encontrar las salidas de una ruta en una fecha especifica
    @Query("SELECT t from Trip t where t.route.id = :routeId AND DATE(t.departureAt)=:date ORDER BY t.departureAt ASC")
    List<Trip> findByRoute(@Param("routeId") Long routeId, @Param("date") LocalDate date);
// busca viajes dependiendo de su estado
    List<Trip> findByStatus(StatusTrip status);
// viajes proximos ó activos
    @Query("SELECT t FROM Trip t WHERE t.statusTrip IN ('SCHEDULED', 'BOARDING')AND t.departureAt >= :now ORDER BY t.departureAt ASC")
    List<Trip> findByDepartureAt(@Param("now") LocalDate now);
// busca viajes entre fechas
    @Query("SELECT t from Trip  t WHERE t.departureAt BETWEEN :start AND :end ORDER BY t.departureAt")
    List<Trip> findBetween(@Param("start") LocalDate start, @Param("end") LocalDate end);
}
