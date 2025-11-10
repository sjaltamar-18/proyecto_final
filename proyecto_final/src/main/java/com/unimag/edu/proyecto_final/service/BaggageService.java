package com.unimag.edu.proyecto_final.service;

import com.unimag.edu.proyecto_final.api.dto.BaggageDtos.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BaggageService {

    BaggageResponse register(BaggageCreateRequest request);

    BaggageResponse get(Long id);

    Page<BaggageResponse> listByTicket(Long ticketId, Pageable pageable);

    BaggageResponse update(Long id, BaggageUpdateRequest request);

    void delete(Long id);
}
