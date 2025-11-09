package com.unimag.edu.proyecto_final.domine.repository;

import com.unimag.edu.proyecto_final.domine.entities.Bus;
import com.unimag.edu.proyecto_final.domine.entities.enumera.StatusBus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BusRepository extends JpaRepository<Bus,Long> {

    // Buscar bus por placa (única en el sistema)
    Optional<Bus> findByPlate(String plate);

    // Verificar si existe una placa (para evitar duplicados)
    boolean existsByPlate(String plate);

    // Buscar buses disponibles para despacho
    @Query("SELECT b FROM Bus b WHERE b.status = 'AVAILABLE'")
    List<Bus> findAvailableBuses();

    // Buscar buses en mantenimiento o revisión
    List<Bus> findByStatus(StatusBus status);

    // Buscar buses con capacidad mínima requerida (por ruta o demanda)
    @Query("SELECT b FROM Bus b WHERE b.status = 'AVAILABLE' AND b.capacity >= :minCapacity")
    List<Bus> findAvailableWithMinCapacity(@Param("minCapacity") int minCapacity);

    // Buscar buses que tengan una comodidad específica (ej. “wifi” o “aire acondicionado”)
    @Query("""
           SELECT b FROM Bus b
           WHERE LOWER(CAST(b.amenities AS string)) LIKE LOWER(CONCAT('%', :feature, '%'))
           """)
    List<Bus> findByAmenity(@Param("feature") String feature);

    // Buscar buses asignados a un trip (por panel de despacho)
    @Query("""
           SELECT DISTINCT b FROM Bus b
           JOIN Trip t ON t.bus.id = b.id
           WHERE t.statusTrip IN ('SCHEDULED', 'BOARDING', 'DEPARTED')
           """)
    List<Bus> findBusesInActiveTrips();

    // Contar buses por estado (para métricas administrativas)
    @Query("SELECT b.status, COUNT(b) FROM Bus b GROUP BY b.status")
    List<Object[]> countByStatus();
}
