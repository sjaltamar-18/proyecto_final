package com.unimag.edu.proyecto_final.service;

import com.unimag.edu.proyecto_final.api.dto.AssignmentDtos.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AssignmentService {

    AssignmentResponse create(AssignmentCreateRequest request);

    AssignmentResponse get(Long id);

    Page<AssignmentResponse> list(Pageable pageable);

    AssignmentResponse update(Long id, AssignmentUpdateRequest request);

    void delete(Long id);
}
