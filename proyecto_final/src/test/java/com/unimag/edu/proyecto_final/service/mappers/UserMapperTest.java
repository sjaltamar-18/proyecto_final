package com.unimag.edu.proyecto_final.service.mappers;

import com.unimag.edu.proyecto_final.api.dto.UserDtos.*;
import com.unimag.edu.proyecto_final.domine.entities.User;
import com.unimag.edu.proyecto_final.domine.entities.enumera.Role;
import com.unimag.edu.proyecto_final.domine.entities.enumera.StatusUser;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class UserMapperTest {

    private final UserMapper mapper = Mappers.getMapper(UserMapper.class);

    @Test
    void toEntity_shouldMapCreateRequest() {

        var profile = new UserProfileDto("3001234567", "DRIVER", "ACTIVE");
        var request = new UserCreateRequest(
                "Sebastián Altamar",
                "sebastian@correo.com",
                "securePass123",
                profile
        );


        User entity = mapper.toEntity(request);


        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isNull(); // ignorado por mapper
        assertThat(entity.getName()).isEqualTo("Sebastián Altamar");
        assertThat(entity.getEmail()).isEqualTo("sebastian@correo.com");
        assertThat(entity.getPasswordHash()).isEqualTo("securePass123");
        assertThat(entity.getPhone()).isEqualTo("3001234567");
        assertThat(entity.getRole()).isEqualTo(Role.DRIVER);
        assertThat(entity.getStatus()).isEqualTo(StatusUser.ACTIVE);
    }

    @Test
    void toResponse_shouldMapEntity() {

        var entity = User.builder()
                .id(10L)
                .name("Laura Gómez")
                .email("laura@correo.com")
                .passwordHash("pass456")
                .phone("3105557890")
                .role(Role.ADMIN)
                .status(StatusUser.INACTIVE)
                .build();


        UserResponse dto = mapper.toResponse(entity);

        assertThat(dto).isNotNull();
        assertThat(dto.id()).isEqualTo(10L);
        assertThat(dto.name()).isEqualTo("Laura Gómez");
        assertThat(dto.email()).isEqualTo("laura@correo.com");
        assertThat(dto.profile()).isNotNull();
        assertThat(dto.profile().phone()).isEqualTo("3105557890");
        assertThat(dto.profile().role()).isEqualTo("ADMIN");
        assertThat(dto.profile().status()).isEqualTo("INACTIVE");
        assertThat(dto.createdAt()).isBeforeOrEqualTo(LocalDateTime.now());
    }

    @Test
    void updateEntityFromDto_shouldUpdateNonNullFields() {

        var entity = User.builder()
                .id(5L)
                .name("Carlos Torres")
                .email("carlos@oldmail.com")
                .passwordHash("oldPass")
                .phone("3111111111")
                .role(Role.PASSENGER)
                .status(StatusUser.ACTIVE)
                .build();

        var newProfile = new UserProfileDto("3209998888", "DISPATCHER", "INACTIVE");
        var update = new UserUpdateRequest(
                "Carlos T.",
                "carlos@newmail.com",
                newProfile
        );


        mapper.updateEntityFromDto(update, entity);


        assertThat(entity.getId()).isEqualTo(5L);
        assertThat(entity.getName()).isEqualTo("Carlos T.");
        assertThat(entity.getEmail()).isEqualTo("carlos@newmail.com");
        assertThat(entity.getPhone()).isEqualTo("3209998888");
        assertThat(entity.getRole()).isEqualTo(Role.DISPATCHER);
        assertThat(entity.getStatus()).isEqualTo(StatusUser.INACTIVE);
    }

    @Test
    void updateEntityFromDto_shouldIgnoreNullFields() {

        var entity = User.builder()
                .id(7L)
                .name("Andrés Pérez")
                .email("andres@mail.com")
                .phone("3000000000")
                .role(Role.ADMIN)
                .status(StatusUser.ACTIVE)
                .build();

        var update = new UserUpdateRequest(
                null,
                null,
                new UserProfileDto(null, null, null)
        );


        mapper.updateEntityFromDto(update, entity);


        assertThat(entity.getId()).isEqualTo(7L);
        assertThat(entity.getName()).isEqualTo("Andrés Pérez");
        assertThat(entity.getEmail()).isEqualTo("andres@mail.com");
        assertThat(entity.getPhone()).isEqualTo("3000000000");
        assertThat(entity.getRole()).isEqualTo(Role.ADMIN);
        assertThat(entity.getStatus()).isEqualTo(StatusUser.ACTIVE);
    }
}