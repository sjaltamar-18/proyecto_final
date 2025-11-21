package com.unimag.edu.proyecto_final.domine.repository;

import com.unimag.edu.proyecto_final.domine.entities.Bus;
import com.unimag.edu.proyecto_final.domine.entities.enumera.StatusBus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BusRepository extends JpaRepository<Bus,Long> {


    Optional<Bus> findByPlate(String plate);


    boolean existsByPlate(String plate);


    @Query("SELECT b FROM Bus b WHERE b.status = 'AVAILABLE'")
    List<Bus> findAvailableBuses();


    List<Bus> findByStatus(StatusBus status);


    @Query("SELECT b FROM Bus b WHERE b.status = 'AVAILABLE' AND b.capacity >= :minCapacity")
    List<Bus> findAvailableWithMinCapacity(@Param("minCapacity") int minCapacity);


    @Query("""
           SELECT b FROM Bus b
           WHERE LOWER(CAST(b.amenities AS string)) LIKE LOWER(CONCAT('%', :feature, '%'))
           """)
    List<Bus> findByAmenity(@Param("feature") String feature);


    @Query("""
           SELECT DISTINCT b FROM Bus b
           JOIN Trip t ON t.bus.id = b.id
           WHERE t.statusTrip IN ('SCHEDULED', 'BOARDING', 'DEPARTED')
           """)
    List<Bus> findBusesInActiveTrips();


    @Query("SELECT b.status, COUNT(b) FROM Bus b GROUP BY b.status")
    List<Object[]> countByStatus();
}