package com.unimag.edu.proyecto_final.service;

import com.unimag.edu.proyecto_final.api.dto.ParcelDtos.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ParcelService {

    ParcelResponse create(ParcelCreateRequest request);

    ParcelResponse get(Long id);

    ParcelResponse updateStatus(String code, ParcelUpdateRequest request);

    Page<ParcelResponse> listInTransit(Pageable pageable);

    Page<ParcelResponse> listByUser(Long userId, Pageable pageable);
}
