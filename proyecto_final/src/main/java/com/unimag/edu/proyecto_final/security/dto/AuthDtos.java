package com.unimag.edu.proyecto_final.security.dto;

import com.unimag.edu.proyecto_final.security.domine.SecurityRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.Set;

public class AuthDtos {
    public record  RegisterRequest(
            @NotBlank @Email String email,
            @NotBlank String password,
            Set<SecurityRole> roles) {}

    public record LoginRequest(
            @Email @NotBlank String email,
            @NotBlank String password
    ){}

    public record  AuthResponse(
            String accesToken,
            String tokenType,
            long expiresInSeconds
    ){}
}