package com.unimag.edu.proyecto_final.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unimag.edu.proyecto_final.service.AssignmentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;




@WebMvcTest(AssignmentController.class)
class AssignmentControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    AssignmentService service;



    @Test
    void create() {
    }

    @Test
    void get() {
    }

    @Test
    void list() {
    }

    @Test
    void update() {
    }

    @Test
    void delete() {
    }
}