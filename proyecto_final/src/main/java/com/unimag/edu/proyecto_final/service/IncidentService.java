package com.unimag.edu.proyecto_final.service;

import com.unimag.edu.proyecto_final.api.dto.IncidentDtos.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IncidentService {

    IncidentResponse create(IncidentCreateRequest request);

    IncidentResponse get(Long id);

    Page<IncidentResponse> listByEntity(String entityType, Long entityId, Pageable pageable);

    Page<IncidentResponse> listAll(Pageable pageable);
}
