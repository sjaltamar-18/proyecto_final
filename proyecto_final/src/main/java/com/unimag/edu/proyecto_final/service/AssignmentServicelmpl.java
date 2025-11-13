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
    public AssignmentResponse create(AssignmentCreateRequest request) {
        Trip trip = tripRepository.findById(request.tripId())
                .orElseThrow(() -> new NotFoundException("Trip not found"));
        if (trip.getStatusTrip() != StatusTrip.SCHEDULED){
            throw new IllegalStateException("Trip is already scheduled");
        }

        User driver  = userRepository.findById(request.driverId())
                .orElseThrow(() -> new NotFoundException("Driver not found"));
        if (driver.getRole() != Role.DRIVER){
            throw new IllegalStateException("Driver is not a driver");
        }
        User dispatcher = userRepository.findById(request.dispatcherId())
                .orElseThrow(() -> new NotFoundException("Dispatcher not found"));
        if (dispatcher.getRole() != Role.DISPATCHER){
            throw new IllegalStateException("Dispatcher is not a dispatcher");
        }
        Assignment assignment = Assignment.builder()
                .trip(trip)
                .driver(driver)
                .checklistOk(request.checklistOk())
                .assignedDate(LocalDateTime.now())
                .build();

        trip.setStatusTrip(StatusTrip.BOARDING);
        tripRepository.save(trip);

        assignmentRepository.save(assignment);

        return assignmentMapper.toResponse(assignment);
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
