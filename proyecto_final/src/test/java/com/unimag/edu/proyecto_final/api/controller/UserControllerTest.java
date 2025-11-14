package com.unimag.edu.proyecto_final.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unimag.edu.proyecto_final.api.dto.UserDtos.*;
import com.unimag.edu.proyecto_final.service.UserService;
import org.apache.catalina.security.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@Import(SecurityConfig.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockitoBean
    private UserService service;


    @Test
    void createUser_shouldReturn201() throws Exception {

        UserProfileDto profileDto = new UserProfileDto("301768390", "PASSENGER", "ACTIVE");

        UserCreateRequest request = new UserCreateRequest("Luis","luis@gmail.com","1234",profileDto);

        UserResponse response = new UserResponse(1L,"luis","luis@gmail.com",
                profileDto,null);

        when(service.create(any())).thenReturn(response);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                        .andExpect(status().isCreated())
                        .andExpect(jsonPath("$.id").value(1))
                        .andExpect(jsonPath("$.email").value("luis@gmail.com"));


    }

    @Test
    void getUser_shouldReturn200() throws Exception {

        UserProfileDto profile = new UserProfileDto("3101234567","PASSENGER","ACTIVE");

        UserResponse response = new UserResponse(1L, "Luis", "luis@mail.com", profile, null);

        when(service.get(1L)).thenReturn(response);

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getUserByEmail_shouldReturn200() throws Exception {

        UserProfileDto profile = new UserProfileDto("999","PASSENGER","ACTIVE");

        UserResponse response = new UserResponse(1L, "Luis", "luis@mail.com", profile, null);

        when(service.getByEmail("luis@mail.com")).thenReturn(response);

        mockMvc.perform(get("/api/users/email/luis@mail.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("luis@mail.com"));
    }

    @Test
    void listByRole_shouldReturn200() throws Exception {
        UserProfileDto profileDto = new UserProfileDto("9999","DRIVER","ACTIVE");

        List<UserResponse> list = List.of(
                new UserResponse(1L,"camilo","c@mail.com",profileDto,null)
        );
        when(service.listByRole("driver")).thenReturn(list);

        mockMvc.perform(get("/api/users/role/driver"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void updateUser_shouldReturn200() throws Exception {
        UserProfileDto profileDto = new UserProfileDto("9999","PASSENGER","ACTIVE");
        UserUpdateRequest request = new UserUpdateRequest("luis nuevo","l@mail.com",profileDto);
        UserResponse response = new UserResponse(1L,"luis nuevo","l@gmail",profileDto,null);

        when(service.update(eq(1L),any())).thenReturn(response);

        mockMvc.perform(put("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("luis nuevo"));

    }

    @Test
    void deleteUser_shoulReturn204() throws Exception {
        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNoContent());
    }
}