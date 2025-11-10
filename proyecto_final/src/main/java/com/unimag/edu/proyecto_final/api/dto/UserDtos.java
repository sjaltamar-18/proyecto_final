package com.unimag.edu.proyecto_final.api.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

public class UserDtos {
    public record UserCreateRequest(String name, String email, String password, UserProfileDto profile) implements Serializable {}
    public record UserProfileDto(String phone, String role, String status) implements Serializable {}
    public record UserUpdateRequest(String name, String email, UserProfileDto profile) implements Serializable {}
    public record UserResponse(Long id, String name, String email, UserProfileDto profile, LocalDateTime createdAt) implements Serializable {}
}
