package com.unimag.edu.proyecto_final.domine.repository;

import com.unimag.edu.proyecto_final.domine.entities.Route;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RouteRepository extends JpaRepository<Route,Long> {

    @Query("""
           SELECT r FROM Route r
           WHERE LOWER(r.originName) LIKE LOWER(CONCAT('%', :city, '%'))
              OR LOWER(r.destinationName) LIKE LOWER(CONCAT('%', :city, '%'))
           ORDER BY r.originName, r.destinationName
           """)
    List<Route> searchByCity(@Param("city") String city);

    Optional<Route>findByRouteCode(String code);

    @Query("""
           SELECT r FROM Route r
           WHERE LOWER(r.originName) = LOWER(:origin)
             AND LOWER(r.destinationName) = LOWER(:destination)
           """)
    Optional<Route> findByOriginAndDestination(@Param("origin") String origin,
                                               @Param("destination") String destination);

    @Query("SELECT DISTINCT r FROM Route r LEFT JOIN FETCH r.stops")
    List<Route> findAllWithStops();


    @Query("""
           SELECT DISTINCT r
           FROM Route r
           JOIN r.stops s
           WHERE LOWER(s.stopName) LIKE LOWER(CONCAT('%', :city, '%'))
           """)
    List<Route> findByIntermediateStop(@Param("city") String city);
}