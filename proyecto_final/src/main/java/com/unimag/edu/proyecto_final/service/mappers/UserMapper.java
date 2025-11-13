package com.unimag.edu.proyecto_final.service.mappers;

import com.unimag.edu.proyecto_final.api.dto.UserDtos.*;
import com.unimag.edu.proyecto_final.domine.entities.User;
import com.unimag.edu.proyecto_final.domine.entities.enumera.Role;
import com.unimag.edu.proyecto_final.domine.entities.enumera.StatusUser;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", expression = "java(mapRole(request.role()))")
    @Mapping(target = "status", constant = "ACTIVE")
    User toEntity(UserCreateRequest request);

    @Mapping(target = "role", expression = "java(user.getRole() != null ? user.getRole().name() : null)")
    @Mapping(target = "status", expression = "java(user.getStatus() != null ? user.getStatus().name() : null)")
    UserResponse toResponse(User user);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "role", expression = "java(request.role() != null ? mapRole(request.role()) : user.getRole())")
    @Mapping(target = "status", expression = "java(request.status() != null ? mapStatus(request.status()) : user.getStatus())")
    void updateEntityFromDto(UserUpdateRequest request, @MappingTarget User user);

    default Role mapRole(String value) {
        if (value == null) return Role.PASSENGER;
        try {
            return Role.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException ex) {
            return Role.PASSENGER;
        }
    }

    default StatusUser mapStatus(String value) {
        if (value == null) return StatusUser.ACTIVE;
        try {
            return StatusUser.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException ex) {
            return StatusUser.ACTIVE;
        }
    }
}
