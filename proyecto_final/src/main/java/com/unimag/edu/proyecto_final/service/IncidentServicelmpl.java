package com.unimag.edu.proyecto_final.service;

import com.unimag.edu.proyecto_final.api.dto.IncidentDtos;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class IncidentServicelmpl  implements IncidentService{
    @Override
    public IncidentDtos.IncidentResponse create(IncidentDtos.IncidentCreateRequest request) {
        return null;
    }

    @Override
    public IncidentDtos.IncidentResponse get(Long id) {
        return null;
    }

    @Override
    public Page<IncidentDtos.IncidentResponse> listByEntity(String entityType, Long entityId, Pageable pageable) {
        return null;
    }

    @Override
    public Page<IncidentDtos.IncidentResponse> listAll(Pageable pageable) {
        return null;
    }
}
