package com.unimag.edu.proyecto_final.service;

import com.unimag.edu.proyecto_final.api.dto.BusDtos.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BusService {

    BusResponse create(BusCreateRequest request);

    BusResponse get(Long id);

    Page<BusResponse> list(Pageable pageable);

    BusResponse update(Long id, BusUpdateRequest request);

    void delete(Long id);
}
