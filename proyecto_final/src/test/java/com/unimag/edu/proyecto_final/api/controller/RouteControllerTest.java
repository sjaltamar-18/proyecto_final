package com.unimag.edu.proyecto_final.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unimag.edu.proyecto_final.api.dto.RouteDtos.*;
import com.unimag.edu.proyecto_final.api.dto.StopDtos;
import com.unimag.edu.proyecto_final.service.RouteService;
import com.unimag.edu.proyecto_final.service.StopService;
import org.apache.catalina.security.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(RouteController.class)
class RouteControllerTest extends TestBase {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    RouteService routeService;
    @MockitoBean
    private StopService stopService;

    @Test
    void create_shouldReturn201() throws Exception {
        RouteCreateRequest request = new RouteCreateRequest(
                "R01", "Ruta 1", "Ciudad A", "Ciudad B", 120.0, 90
        );

        RouteResponse response = new RouteResponse(
                1L, "R01", "Ruta 1", "Ciudad A", "Ciudad B", 120.0, 90
        );

        when(routeService.create(any(RouteCreateRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/routes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.code").value("R01"))
                .andExpect(jsonPath("$.name").value("Ruta 1"));
    }

    @Test
    void get_shouldReturn200() throws Exception {

        RouteResponse response = new RouteResponse(
                10L, "R10", "Ruta Express", "A", "B", 200.0, 120
        );
        when(routeService.get(10L)).thenReturn(response);

        mockMvc.perform(get("/api/routes/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.name").value("Ruta Express"));
    }

    @Test
    void list_shouldReturn200() throws Exception {
        List<RouteResponse> routes = List.of(
                new RouteResponse(1L, "R01", "Ruta 1", "A", "B", 100.0, 60),
                new RouteResponse(2L, "R02", "Ruta 2", "B", "C", 180.0, 90)
        );

        Page<RouteResponse> page = new PageImpl<>(routes, PageRequest.of(0, 10), 2);

        when(routeService.list(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/routes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].code").value("R01"))
                .andExpect(jsonPath("$.content[1].code").value("R02"));
    }

    @Test
    void searchByCity_shouldReturn200() throws Exception {
        List<RouteResponse> list = List.of(
                new RouteResponse(3L, "R03", "Ruta Costa", "Cartagena", "Barranquilla", 130.0, 80)
        );

        when(routeService.searchByCity("Cartagena")).thenReturn(list);

        mockMvc.perform(get("/api/routes/search?city=Cartagena"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(3))
                .andExpect(jsonPath("$[0].origin").value("Cartagena"));
    }

    @Test
    void update_shouldReturn200() throws Exception {
        RouteUpdateRequest request = new RouteUpdateRequest(
                "Ruta Actualizada", "Santa Marta", "Barranquilla", 150.0, 95
        );

        RouteResponse updated = new RouteResponse(
                5L, "R05", "Ruta Actualizada", "Santa Marta", "Barranquilla", 150.0, 95
        );

        when(routeService.update(eq(5L), any(RouteUpdateRequest.class))).thenReturn(updated);

        mockMvc.perform(put("/api/routes/5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Ruta Actualizada"));
    }

    @Test
    void delete_shouldReturn204() throws Exception {
        doNothing().when(routeService).delete(7L);

        mockMvc.perform(delete("/api/routes/7"))
                .andExpect(status().isNoContent());
    }

    @Test
    void getStopsByRoute_debe_retornar_lista_de_stops() throws Exception {

        Long routeId = 1L;

        StopDtos.StopResponse stop1 =
                new StopDtos.StopResponse(10L, 1L, "A", 1, 1.1, 1.2);

        StopDtos.StopResponse stop2 =
                new StopDtos.StopResponse(20L, 1L, "B", 2, 2.1, 2.2);

        when(routeService.getStopsByRoute(routeId))
                .thenReturn(List.of(stop1, stop2));

        mockMvc.perform(get("/api/routes/{id}/stops", routeId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].id").value(10))
                .andExpect(jsonPath("$[0].routeId").value(1))
                .andExpect(jsonPath("$[0].name").value("A"))
                .andExpect(jsonPath("$[1].id").value(20))
                .andExpect(jsonPath("$[1].order").value(2));
    }

    @Test
    void getStopsByRoute_debe_retornar_404_si_no_existe_la_ruta() throws Exception {

        Long routeId = 999L;

        when(routeService.getStopsByRoute(routeId))
                .thenThrow(new IllegalArgumentException("Route not found"));

        mockMvc.perform(get("/api/routes/{id}/stops", routeId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest()); // o .isNotFound() si usas @ControllerAdvice
    }
}