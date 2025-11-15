package com.unimag.edu.proyecto_final.api.controller;

import com.unimag.edu.proyecto_final.api.dto.IncidentDtos.*;
import com.unimag.edu.proyecto_final.service.IncidentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("api/incidents")
@RequiredArgsConstructor
public class IncidentController {
    private final IncidentService incidentService;

    @PostMapping
    public ResponseEntity<IncidentResponse> createIncident(@RequestBody IncidentCreateRequest incident){
        IncidentResponse create =  incidentService.create(incident);
        return ResponseEntity.status(HttpStatus.CREATED).body(create);
    }

    @GetMapping("/{id}")
    public ResponseEntity<IncidentResponse> getIncident(@PathVariable Long id){
        return ResponseEntity.ok(incidentService.get(id));
    }

    @GetMapping
    public ResponseEntity<Page<IncidentResponse>> list(Pageable pageable){
        return ResponseEntity.ok(incidentService.list(pageable));
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<IncidentResponse>> listByType(@PathVariable String type){
        return ResponseEntity.ok(incidentService.listByType(type));
    }

    @GetMapping("/entity")
    public ResponseEntity<List<IncidentResponse>> listByEntity(@RequestParam String entityType,
                                                               @RequestParam Long entityId){
        return ResponseEntity.ok(incidentService.listByEntity(entityType, entityId));
    }

    @GetMapping("/range")
    public ResponseEntity<List<IncidentResponse>> listByRange(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)LocalDateTime start,
                                                              @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)LocalDateTime end){
        return ResponseEntity.ok(incidentService.listBetweenDates(start, end));
    }

    @PutMapping("/{id}")
    public ResponseEntity<IncidentResponse> updateIncident(@PathVariable Long id,
                                                           @Valid @RequestBody IncidentUpdateRequest incident){
        return  ResponseEntity.ok(incidentService.update(id, incident));

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<IncidentResponse> deleteIncident(@PathVariable Long id){
        incidentService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/cleanup")
    public ResponseEntity<String> cleanup(@RequestParam int days){
        int delete = incidentService.deleteOlderThan(days);
        return ResponseEntity.ok(delete+"incidents deleted successfully");
    }

}
