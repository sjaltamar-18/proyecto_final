package com.unimag.edu.proyecto_final.service.mappers;

import com.unimag.edu.proyecto_final.api.dto.UserDtos.*;
import com.unimag.edu.proyecto_final.domine.entities.User;
import com.unimag.edu.proyecto_final.domine.entities.enumera.Role;
import com.unimag.edu.proyecto_final.domine.entities.enumera.StatusUser;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", expression = "java(mapRole(request.profile() != null ? request.profile().role() : null))")
    @Mapping(target = "status", expression = "java(mapStatus(request.profile() != null ? request.profile().status() : null))")
    @Mapping(target = "phone", expression = "java(request.profile() != null ? request.profile().phone() : null)")
    @Mapping(target = "passwordHash", source = "password")
    User toEntity(UserCreateRequest request);

    @Mapping(target = "profile", expression = "java(toProfileDto(user))")
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    UserResponse toResponse(User user);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "role", expression = "java(request.profile() != null && request.profile().role() != null ? mapRole(request.profile().role()) : user.getRole())")
    @Mapping(target = "status", expression = "java(request.profile() != null && request.profile().status() != null ? mapStatus(request.profile().status()) : user.getStatus())")
    @Mapping(target = "phone", expression = "java(request.profile() != null && request.profile().phone() != null ? request.profile().phone() : user.getPhone())")
    void updateEntityFromDto(UserUpdateRequest request, @MappingTarget User user);

    default UserProfileDto toProfileDto(User user) {
        return new UserProfileDto(
                user.getPhone(),
                user.getRole() != null ? user.getRole().name() : null,
                user.getStatus() != null ? user.getStatus().name() : null
        );
    }

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
