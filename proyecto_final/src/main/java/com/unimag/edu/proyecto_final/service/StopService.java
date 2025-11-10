package com.unimag.edu.proyecto_final.service;

import com.unimag.edu.proyecto_final.api.dto.StopDtos.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface StopService {

    StopResponse create(Long routeId, StopCreateRequest request);

    StopResponse get(Long id);

    Page<StopResponse> listByRoute(Long routeId, Pageable pageable);

    StopResponse update(Long id, StopUpdateRequest request);

    void delete(Long id);
}
