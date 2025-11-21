
package com.unimag.edu.proyecto_final.service;

import com.unimag.edu.proyecto_final.api.dto.IncidentDtos;
import com.unimag.edu.proyecto_final.api.dto.IncidentDtos.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

public interface IncidentService {

    IncidentResponse create(IncidentCreateRequest request);

    IncidentResponse get(Long id);

    Page<IncidentResponse> list(Pageable pageable);

    List<IncidentResponse> listByEntity(String entityType, Long entityId);

    List<IncidentResponse> listByType(String type);

    List<IncidentResponse> listBetweenDates(LocalDateTime start, LocalDateTime end);

    IncidentResponse update(Long id, IncidentUpdateRequest request);

    void delete(Long id);

    int deleteOlderThan(int days);

    IncidentResponse createDeliveryFailureIncident(Long parcelId, String reason);
}
