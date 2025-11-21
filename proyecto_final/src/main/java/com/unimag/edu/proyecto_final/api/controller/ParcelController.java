package com.unimag.edu.proyecto_final.api.controller;

import com.unimag.edu.proyecto_final.api.dto.ParcelDtos.*;
import com.unimag.edu.proyecto_final.service.ParcelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/parcels")
@RequiredArgsConstructor
@RestController
public class ParcelController {
    private final ParcelService parcelService;

    @PostMapping
    public ResponseEntity<ParcelResponse> create(@Valid @RequestBody ParcelCreateRequest request){
        ParcelResponse created =  parcelService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ParcelResponse> get(@PathVariable Long id){
        return ResponseEntity.ok(parcelService.get(id));
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<ParcelResponse> getByCode(@PathVariable String code){
        return ResponseEntity.ok(parcelService.getByCode(code));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<Page<ParcelResponse>> getByStatus(@PathVariable String status, Pageable pageable){
        return ResponseEntity.ok(parcelService.listByStatus(status, pageable));
    }

    @PostMapping("/{code}/status")
    public ResponseEntity<ParcelResponse> update(@PathVariable String code, @RequestBody ParcelUpdateRequest request) {
        Long id = parcelService.getByCode(code).id();
        return ResponseEntity.ok(parcelService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ParcelResponse> delete(@PathVariable Long id){
        parcelService.delete(id);
        return ResponseEntity.noContent().build();
    }
}