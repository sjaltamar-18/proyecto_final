
package com.unimag.edu.proyecto_final.service;

import com.unimag.edu.proyecto_final.api.dto.AssignmentDtos.*;
import com.unimag.edu.proyecto_final.domine.entities.Assignment;
import com.unimag.edu.proyecto_final.domine.entities.Trip;
import com.unimag.edu.proyecto_final.domine.entities.User;
import com.unimag.edu.proyecto_final.domine.entities.enumera.Role;
import com.unimag.edu.proyecto_final.domine.entities.enumera.StatusTrip;
import com.unimag.edu.proyecto_final.domine.repository.AssignmentRepository;
import com.unimag.edu.proyecto_final.domine.repository.TripRepository;
import com.unimag.edu.proyecto_final.domine.repository.UserRepository;
import com.unimag.edu.proyecto_final.exception.NotFoundException;
import com.unimag.edu.proyecto_final.service.mappers.AssignmentMapper;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class AssignmentServicelmpl implements AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final TripRepository tripRepository;
    private final UserRepository userRepository;
    private final AssignmentMapper assignmentMapper;


    @Override
    public AssignmentResponse assign(Long tripId, AssignmentCreateRequest request) {


        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new IllegalArgumentException("Trip not found"));


        User driver = userRepository.findById(request.driverId())
                .orElseThrow(() -> new IllegalArgumentException("Driver not found"));

        if (driver.getRole() != Role.DRIVER) {
            throw new IllegalArgumentException("User is not a driver");
        }


        User dispatcher = userRepository.findById(request.dispatcherId())
                .orElseThrow(() -> new IllegalArgumentException("Dispatcher not found"));

        if (dispatcher.getRole() != Role.DISPATCHER) {
            throw new IllegalArgumentException("User is not a dispatcher");
        }


        Assignment assignment = assignmentRepository.findByTripId(tripId)
                .orElse(new Assignment());


        assignment.setTrip(trip);
        assignment.setDriver(driver);
        assignment.setDispatcher(dispatcher);
        assignment.setChecklistOk(request.checklistOk());
        assignment.setAssignedDate(LocalDateTime.now());


        Assignment saved = assignmentRepository.save(assignment);

        return assignmentMapper.toResponse(saved);
    }

    @Override
    public AssignmentResponse get(Long id) {
        var assignment = assignmentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Asignación no encontrada con id: " + id));

        return assignmentMapper.toResponse(assignment);
    }

    @Override
    public Page<AssignmentResponse> list(Pageable pageable) {
        return assignmentRepository.findAll(pageable)
                .map(assignmentMapper::toResponse);
    }

    @Override
    @Transactional
    public AssignmentResponse update(Long id, AssignmentUpdateRequest request) {
        var assignment = assignmentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Assigment not found with the id " + id));

        assignmentMapper.updateEntityFromDto(request,assignment);

        assignmentRepository.save(assignment);
        return assignmentMapper.toResponse(assignment);
    }
    @Override
    @Transactional
    public void delete(Long id) {
        var assignment = assignmentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Assigment not found with the id " + id));

        assignmentRepository.delete(assignment);
    }
}
