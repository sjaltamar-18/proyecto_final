package com.unimag.edu.proyecto_final.api.controller;

import com.unimag.edu.proyecto_final.api.dto.BusDtos.*;
import com.unimag.edu.proyecto_final.service.BusService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/buses")
@RequiredArgsConstructor
public class BusController {
    private final BusService busService;

    @PostMapping
    public ResponseEntity<BusResponse> create (@Valid @RequestBody BusCreateRequest busCreateRequest) {
        BusResponse busResponse = busService.create(busCreateRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(busResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BusResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok( busService.get(id));
    }

    @GetMapping
    public ResponseEntity<Page<BusResponse>> list (Pageable pageable) {
        return ResponseEntity.ok( busService.list(pageable));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BusResponse> update (@PathVariable Long id, @Valid @RequestBody BusUpdateRequest busUpdateRequest) {
        return ResponseEntity.ok( busService.update(id, busUpdateRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BusResponse> delete(@PathVariable Long id) {
        busService.delete(id);
        return ResponseEntity.noContent().build();
    }
}


