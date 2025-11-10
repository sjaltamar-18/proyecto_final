package com.unimag.edu.proyecto_final.service;

import com.unimag.edu.proyecto_final.api.dto.TicketDtos.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TicketService {

    TicketResponse create(TicketCreateRequest request);

    TicketResponse get(Long id);

    Page<TicketResponse> listByUser(Long userId, Pageable pageable);

    TicketResponse cancel(Long id);

    TicketResponse markNoShow(Long id);

    double calculatePrice(TicketUpdateRequest request);
}
