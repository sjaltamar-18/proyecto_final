package com.unimag.edu.proyecto_final.api.controller;

import com.unimag.edu.proyecto_final.service.AssignmentService;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/asignments")
@RequiredArgsConstructor
@Value
public class AssignmentController {
    private final AssignmentService service;
}
