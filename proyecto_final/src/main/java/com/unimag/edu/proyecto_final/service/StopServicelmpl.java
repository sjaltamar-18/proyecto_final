package com.unimag.edu.proyecto_final.service;

import com.unimag.edu.proyecto_final.api.dto.StopDtos;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class StopServicelmpl implements StopService {
    @Override
    public StopDtos.StopResponse create(Long routeId, StopDtos.StopCreateRequest request) {
        return null;
    }

    @Override
    public StopDtos.StopResponse get(Long id) {
        return null;
    }

    @Override
    public Page<StopDtos.StopResponse> listByRoute(Long routeId, Pageable pageable) {
        return null;
    }

    @Override
    public StopDtos.StopResponse update(Long id, StopDtos.StopUpdateRequest request) {
        return null;
    }

    @Override
    public void delete(Long id) {

    }
}
