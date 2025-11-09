package com.unimag.edu.proyecto_final.domine.repository;

import com.unimag.edu.proyecto_final.domine.entities.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AssignmentRepository extends JpaRepository<Assignment,Long> {
    // Buscar asignación por viaje (cada viaje debe tener una)
    Optional<Assignment> findByTripId(Long tripId);

    // Buscar asignaciones por conductor
    List<Assignment> findByDriverId(Long driverId);

    // Buscar asignaciones por bus
    List<Assignment> findByDispatcherId(Long dispatcherId);


}
