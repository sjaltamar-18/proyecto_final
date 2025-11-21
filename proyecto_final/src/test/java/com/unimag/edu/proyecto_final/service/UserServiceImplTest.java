
package com.unimag.edu.proyecto_final.service;

import com.unimag.edu.proyecto_final.api.dto.UserDtos.*;
import com.unimag.edu.proyecto_final.domine.entities.User;
import com.unimag.edu.proyecto_final.domine.entities.enumera.Role;
import com.unimag.edu.proyecto_final.domine.entities.enumera.StatusUser;
import com.unimag.edu.proyecto_final.domine.repository.UserRepository;
import com.unimag.edu.proyecto_final.exception.NotFoundException;
import com.unimag.edu.proyecto_final.service.mappers.UserMapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServicelmplTest {

    @Mock private UserRepository userRepository;
    @Mock private UserMapper userMapper;

    @InjectMocks
    private UserServicelmpl service;


    @Test
    void create_debe_crear_usuario_correctamente_con_profile() {

        UserProfileDto profile = new UserProfileDto("123", "ADMIN", "BLOCKED");
        UserCreateRequest req = new UserCreateRequest("Juan", "juan@mail.com", "abc", profile);

        when(userRepository.existsByEmail("juan@mail.com")).thenReturn(false);

        User entity = new User();
        when(userMapper.toEntity(req)).thenReturn(entity);

        User saved = User.builder()
                .id(1L)
                .name("Juan")
                .email("juan@mail.com")
                .createdAt(LocalDateTime.now())
                .build();

        when(userRepository.save(entity)).thenReturn(saved);

        UserResponse mapped = new UserResponse(
                saved.getId(),
                saved.getName(),
                saved.getEmail(),
                profile,
                saved.getCreatedAt()
        );

        when(userMapper.toResponse(saved)).thenReturn(mapped);

        UserResponse result = service.create(req);

        assertThat(result).isEqualTo(mapped);
        assertThat(entity.getPhone()).isEqualTo("123");
        assertThat(entity.getRole()).isEqualTo(Role.ADMIN);
        assertThat(entity.getStatus()).isEqualTo(StatusUser.ACTIVE);
        assertThat(entity.getPasswordHash()).isEqualTo("{hashed}abc");
    }

    @Test
    void create_asigna_valores_por_defecto_si_profile_es_null() {

        UserCreateRequest req = new UserCreateRequest("Juan", "abc@mail.com", "pwd", null);

        when(userRepository.existsByEmail("abc@mail.com")).thenReturn(false);

        User entity = new User();
        when(userMapper.toEntity(req)).thenReturn(entity);

        User saved = new User();
        when(userRepository.save(entity)).thenReturn(saved);

        UserResponse mapped = mock(UserResponse.class);
        when(userMapper.toResponse(saved)).thenReturn(mapped);

        UserResponse result = service.create(req);

        assertThat(entity.getRole()).isEqualTo(Role.PASSENGER);
        assertThat(entity.getStatus()).isEqualTo(StatusUser.ACTIVE);
        assertThat(entity.getPasswordHash()).isEqualTo("{hashed}pwd");
        assertThat(result).isEqualTo(mapped);
    }

    @Test
    void create_falla_si_email_existe() {

        UserCreateRequest req = new UserCreateRequest("A", "dup@mail.com", "pwd", null);

        when(userRepository.existsByEmail("dup@mail.com")).thenReturn(true);

        assertThatThrownBy(() -> service.create(req))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("User already exists");
    }


    @Test
    void get_retorna_usuario_mapeado() {
        User user = new User();
        user.setId(5L);

        when(userRepository.findById(5L)).thenReturn(Optional.of(user));

        UserResponse mapped = mock(UserResponse.class);
        when(userMapper.toResponse(user)).thenReturn(mapped);

        assertThat(service.get(5L)).isEqualTo(mapped);
    }

    @Test
    void get_falla_si_no_existe() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.get(99L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("User not found");
    }


    @Test
    void getByEmail_retorna_usuario() {

        User user = new User();
        user.setEmail("x@mail.com");

        when(userRepository.findByEmail("x@mail.com"))
                .thenReturn(Optional.of(user));

        UserResponse mapped = mock(UserResponse.class);
        when(userMapper.toResponse(user)).thenReturn(mapped);

        assertThat(service.getByEmail("x@mail.com")).isEqualTo(mapped);
    }

    @Test
    void getByEmail_falla_si_no_existe() {
        when(userRepository.findByEmail("none@mail.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getByEmail("none@mail.com"))
                .isInstanceOf(NotFoundException.class);
    }


    @Test
    void listByRole_ok() {

        User user = new User();
        when(userRepository.findAllByRole(Role.ADMIN))
                .thenReturn(List.of(user));

        UserResponse mapped = mock(UserResponse.class);
        when(userMapper.toResponse(user))
                .thenReturn(mapped);

        var result = service.listByRole("ADMIN");

        assertThat(result).containsExactly(mapped);
    }


    @Test
    void list_pageable_ok() {

        Page<User> page = new PageImpl<>(List.of(new User()));
        when(userRepository.findAll(any(Pageable.class)))
                .thenReturn(page);

        when(userMapper.toResponse(any()))
                .thenReturn(mock(UserResponse.class));

        var result = service.list(PageRequest.of(0, 10));

        assertThat(result.getContent()).hasSize(1);
    }


    @Test
    void update_ok_con_profile() {

        UserProfileDto profile = new UserProfileDto("555", "DRIVER", "BLOCKED");
        UserUpdateRequest req =
                new UserUpdateRequest("Nuevo", "mail@mail.com", profile);

        User user = new User();
        user.setEmail("mail@mail.com");

        when(userRepository.findById(10L)).thenReturn(Optional.of(user));

        User saved = new User();
        when(userRepository.save(user)).thenReturn(saved);

        UserResponse mapped = mock(UserResponse.class);
        when(userMapper.toResponse(saved)).thenReturn(mapped);

        var result = service.update(10L, req);

        assertThat(user.getName()).isEqualTo("Nuevo");
        assertThat(user.getPhone()).isEqualTo("555");
        assertThat(user.getRole()).isEqualTo(Role.DRIVER);
        assertThat(user.getStatus()).isEqualTo(StatusUser.ACTIVE);
        assertThat(result).isEqualTo(mapped);
    }

    @Test
    void update_falla_si_email_cambia() {

        UserUpdateRequest req =
                new UserUpdateRequest("A", "nuevo@mail.com", null);

        User user = new User();
        user.setEmail("viejo@mail.com");

        when(userRepository.findById(7L))
                .thenReturn(Optional.of(user));

        assertThatThrownBy(() -> service.update(7L, req))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("User already exists");
    }


    @Test
    void delete_cambia_a_INACTIVE() {

        User user = new User();
        user.setStatus(StatusUser.ACTIVE);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        service.delete(1L);

        assertThat(user.getStatus()).isEqualTo(StatusUser.INACTIVE);
        verify(userRepository).save(user);
    }

    @Test
    void delete_falla_si_no_existe() {

        when(userRepository.findById(20L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(20L))
                .isInstanceOf(NotFoundException.class);
    }
}
