package com.unimag.edu.proyecto_final.service;

import com.unimag.edu.proyecto_final.api.dto.RouteDtos.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RouteService {

    RouteResponse create(RouteCreateRequest request);

    RouteResponse get(Long id);

    Page<RouteResponse> list(Pageable pageable);

    RouteResponse update(Long id, RouteUpdateRequest request);

    void delete(Long id);
}
