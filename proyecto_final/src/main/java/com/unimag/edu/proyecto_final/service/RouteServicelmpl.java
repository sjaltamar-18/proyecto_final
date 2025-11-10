package com.unimag.edu.proyecto_final.service;

import com.unimag.edu.proyecto_final.api.dto.RouteDtos;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional

public class RouteServicelmpl implements RouteService{
    @Override
    public RouteDtos.RouteResponse create(RouteDtos.RouteCreateRequest request) {
        return null;
    }

    @Override
    public RouteDtos.RouteResponse get(Long id) {
        return null;
    }

    @Override
    public Page<RouteDtos.RouteResponse> list(Pageable pageable) {
        return null;
    }

    @Override
    public RouteDtos.RouteResponse update(Long id, RouteDtos.RouteUpdateRequest request) {
        return null;
    }

    @Override
    public void delete(Long id) {

    }
}
