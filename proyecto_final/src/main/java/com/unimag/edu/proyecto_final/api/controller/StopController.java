package com.unimag.edu.proyecto_final.api.controller;

import com.unimag.edu.proyecto_final.api.dto.StopDtos.*;
import com.unimag.edu.proyecto_final.service.StopService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stops")
@RequiredArgsConstructor
public class StopController {
    private final StopService stopService;

    @PostMapping("/route/{routeId}")
    public ResponseEntity<StopResponse> create(
            @PathVariable Long routeId,
            @Valid @RequestBody StopCreateRequest request
    ) {
        StopResponse created = stopService.create(routeId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StopResponse> get(@PathVariable Long id) {
        StopResponse stop = stopService.get(id);
        return ResponseEntity.ok(stop);
    }

    @PutMapping("/{id}")
    public ResponseEntity<StopResponse> update(@PathVariable Long id, @Valid @RequestBody StopUpdateRequest request){
        StopResponse updated = stopService.update(id, request);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<StopResponse> delete(@PathVariable Long id){
        stopService.delete(id);
        return ResponseEntity.noContent().build();
    }


}