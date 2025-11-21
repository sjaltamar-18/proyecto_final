package com.unimag.edu.proyecto_final.api.controller;

import com.unimag.edu.proyecto_final.api.dto.ConfiDtos.*;
import com.unimag.edu.proyecto_final.service.ConfiService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/config")
public class ConfiController {
    private final ConfiService confiService;

    @PostMapping
    public ResponseEntity<ConfigResponse> create(@Valid@RequestBody ConfigCreateRequest configCreateRequest) {
        ConfigResponse configResponse = confiService.create(configCreateRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(configResponse);
    }

    @GetMapping("/{key}")
    public ResponseEntity<ConfigResponse> get(@PathVariable String key) {
        return ResponseEntity.ok(confiService.get(key));
    }

    @GetMapping
    public ResponseEntity<Page<ConfigResponse>> list(Pageable pageable) {
        return ResponseEntity.ok(confiService.list(pageable));
    }

    @PutMapping("/{key}")
    public ResponseEntity<ConfigResponse> update(@PathVariable String key, @Valid@RequestBody ConfigUpdateRequest configUpdateRequest) {
        return ResponseEntity.ok(confiService.update(key, configUpdateRequest));
    }

    @DeleteMapping("/{key}")
    public ResponseEntity<ConfigResponse> delete(@PathVariable String key) {
        confiService.delete(key);
        return ResponseEntity.noContent().build();
    }
}