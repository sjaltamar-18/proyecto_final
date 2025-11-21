
package com.unimag.edu.proyecto_final.domine.repository;

import com.unimag.edu.proyecto_final.domine.entities.Stop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StopRepository extends JpaRepository<Stop,Long> {


    List<Stop> findByRouteIdOrderByStopOrderAsc(Long routeId);

    @Query("SELECT s FROM Stop s WHERE s.route.id =:routeId AND lower(s.stopName) = LOWER(:name)")
    Optional<Stop> findByRouteAndName(@Param("routeId") Long routeId, @Param("name") String name);

    @Query("SELECT s FROM Stop s WHERE s.route.id = :routeId AND s.stopOrder = :order - 1")
    Optional<Stop> findPreviousStop(@Param("routeId") Long routeId, @Param("order") int order);
    @Query("SELECT s FROM Stop s WHERE s.route.id = :routeId AND s.stopOrder = :order + 1")
    Optional<Stop> findNextStop(@Param("routeId") Long routeId, @Param("order") int order);

    @Query("""
           SELECT s1.id, s1.stopName, s2.id, s2.stopName
           FROM Stop s1
           JOIN Stop s2 ON s2.route.id = s1.route.id AND s2.stopOrder = s1.stopOrder + 1
           WHERE s1.route.id = :routeId
           ORDER BY s1.stopOrder
           """)
    List<Object[]> findConsecutiveSegments(@Param("routeId") Long routeId);
}
