package com.unimag.edu.proyecto_final.api.controller;

import com.unimag.edu.proyecto_final.api.dto.FareRuleDtos.*;
import com.unimag.edu.proyecto_final.service.FareRuleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/fare-rules")
@RequiredArgsConstructor
public class FareRuleController {

    private final FareRuleService fareRuleService;
    @PostMapping
    public ResponseEntity<FareRuleResponse> create(@Valid @RequestBody FareRuleCreateRequest request) {
        FareRuleResponse created = fareRuleService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FareRuleResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(fareRuleService.get(id));
    }

    @GetMapping("/route/{routeId}")
    public ResponseEntity<Page<FareRuleResponse>> listByRoute(@PathVariable Long routeId, Pageable pageable) {
        return ResponseEntity.ok(fareRuleService.listByRoute(routeId, pageable));
    }

    @PutMapping("/{id}")
    public  ResponseEntity<FareRuleResponse> update(@PathVariable Long id,@Valid @RequestBody FareRuleUpdateRequest request) {
        return ResponseEntity.ok(fareRuleService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<FareRuleResponse> delete(@PathVariable Long id) {
        fareRuleService.delete(id);
        return ResponseEntity.noContent().build();
    }

}