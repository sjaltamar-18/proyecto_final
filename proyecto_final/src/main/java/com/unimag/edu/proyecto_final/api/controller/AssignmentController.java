package com.unimag.edu.proyecto_final.api.controller;

import com.unimag.edu.proyecto_final.api.dto.AssignmentDtos.*;
import com.unimag.edu.proyecto_final.service.AssignmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/assignments")
@RequiredArgsConstructor
public class AssignmentController {

    private final AssignmentService service;

    @PostMapping
    public ResponseEntity<AssignmentResponse> create(
            @Valid @RequestBody AssignmentCreateRequest request
    ) {
        AssignmentResponse response = service.assign(request.tripId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AssignmentResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(service.get(id));
    }

    @GetMapping
    public ResponseEntity<Page<AssignmentResponse>> list(Pageable  pageable) {
        return ResponseEntity.ok(service.list(pageable));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AssignmentResponse> update(@PathVariable Long id, @Valid @RequestBody AssignmentUpdateRequest request) {
        AssignmentResponse response = service.update(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<AssignmentResponse> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}