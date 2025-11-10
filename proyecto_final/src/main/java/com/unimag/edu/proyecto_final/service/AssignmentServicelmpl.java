package com.unimag.edu.proyecto_final.service;

import com.unimag.edu.proyecto_final.api.dto.AssignmentDtos.*;
import com.unimag.edu.proyecto_final.domine.entities.Assignment;
import com.unimag.edu.proyecto_final.domine.repository.AssignmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AssignmentServicelmpl implements AssignmentService {
    private final AssignmentRepository assignmentRepository;


    @Override
    public AssignmentResponse create(AssignmentCreateRequest request) {
        return null;
    }

    @Override
    public AssignmentResponse get(Long id) {
        return null;
    }

    @Override
    public Page<AssignmentResponse> list(Pageable pageable) {
        return null;
    }

    @Override
    public AssignmentResponse update(Long id, AssignmentUpdateRequest request) {
        return null;
    }

    @Override
    public void delete(Long id) {

    }
}
