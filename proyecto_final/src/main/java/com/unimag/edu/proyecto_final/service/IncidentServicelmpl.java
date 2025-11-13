package com.unimag.edu.proyecto_final.service;

import com.unimag.edu.proyecto_final.api.dto.IncidentDtos;
import com.unimag.edu.proyecto_final.domine.entities.Incident;
import com.unimag.edu.proyecto_final.domine.entities.enumera.EntityType;
import com.unimag.edu.proyecto_final.domine.entities.enumera.Type;
import com.unimag.edu.proyecto_final.domine.repository.IncidentRepository;
import com.unimag.edu.proyecto_final.service.mappers.IncidentMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class IncidentServicelmpl  implements IncidentService{

    private final IncidentRepository incidentRepository;
    private final IncidentMapper incidentMapper;

    @Override
    public IncidentDtos.IncidentResponse create(IncidentDtos.IncidentCreateRequest request) {
        Incident incident = incidentMapper.toEntity(request);

        if (incident.getEntityType() == null || incident.getEntityType() == null) {
            throw new EntityNotFoundException("Incident not found");
        }
        if (incident.getType() == null ){
            throw new EntityNotFoundException("Incident type not found");
        }
        Incident saved = incidentRepository.save(incident);
        return incidentMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public IncidentDtos.IncidentResponse get(Long id) {
        Incident incident = incidentRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Incident not found"));

        return incidentMapper.toResponse(incident);
    }
    @Override
    @Transactional(readOnly = true)
    public Page<IncidentDtos.IncidentResponse> list(Pageable pageable) {
        Page<Incident> page = incidentRepository.findAll(pageable);
        return page.map(incidentMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<IncidentDtos.IncidentResponse> listByEntity(String entityType, Long entityId) {
        Type type = Type.valueOf(entityType);
        List<Incident> incidents = incidentRepository.findByType(type);
        return incidents.stream().map(incidentMapper::toResponse).toList();
    }



    @Override
    @Transactional(readOnly = true)
    public List<IncidentDtos.IncidentResponse> listByType(String type) {
        Type t = Type.valueOf(type.toUpperCase());
        List<Incident> incidents = incidentRepository.findByType(t);
        return incidents.stream().map(incidentMapper::toResponse).toList();
    }
    @Override
    @Transactional(readOnly = true)
    public List<IncidentDtos.IncidentResponse> listBetweenDates(LocalDateTime start, LocalDateTime end) {
        List<Incident> incidents = incidentRepository.findBetweenDates(start, end);
        return incidents.stream().map(incidentMapper::toResponse).toList();
    }
    @Override
    public IncidentDtos.IncidentResponse update(Long id, IncidentDtos.IncidentUpdateRequest request) {
        Incident incident = incidentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Incident not found with id: " + id));

        incidentMapper.updateEntityFromDto(request, incident);
        Incident updated = incidentRepository.save(incident);

        return incidentMapper.toResponse(updated);
    }
    @Override
    public void delete(Long id) {
        Incident incident = incidentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Incident not found with id: " + id));
        incidentRepository.delete(incident);
    }

    @Override
    public int deleteOlderThan(int days) {
        LocalDateTime threshold = LocalDateTime.now().minusDays(days);
        return incidentRepository.deleteOlderThan(threshold);
    }


}
