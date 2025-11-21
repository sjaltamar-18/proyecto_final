package com.unimag.edu.proyecto_final.api.controller;

import com.unimag.edu.proyecto_final.api.dto.BaggageDtos.*;
import com.unimag.edu.proyecto_final.service.BaggageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/baggages")
@RequiredArgsConstructor
public class BaggageController {
    private final BaggageService baggageService;

    @PostMapping
    public ResponseEntity<BaggageResponse> register(@Valid@RequestBody BaggageCreateRequest baggageCreateRequest) {
        BaggageResponse baggageResponse = baggageService.register(baggageCreateRequest);
        return  ResponseEntity.status(HttpStatus.CREATED).body(baggageResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaggageResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(baggageService.get(id));
    }

    @GetMapping("/ticket/{ticketId}")
    public ResponseEntity<Page<BaggageResponse>> getTickets(@PathVariable Long ticketId, Pageable pageable) {
        Page<BaggageResponse> page = baggageService.listByTicket(ticketId, pageable);
        return ResponseEntity.ok(page);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaggageResponse> update(@PathVariable Long id, @Valid  @RequestBody BaggageUpdateRequest baggageUpdateRequest) {
        BaggageResponse baggageResponse = baggageService.update(id, baggageUpdateRequest);
        return ResponseEntity.ok(baggageResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        baggageService.delete(id);
        return ResponseEntity.noContent().build();
    }
}