package com.unimag.edu.proyecto_final.security.web;

import com.unimag.edu.proyecto_final.security.domine.AppUser;
import com.unimag.edu.proyecto_final.security.domine.SecurityRole;
import com.unimag.edu.proyecto_final.security.dto.AuthDtos.*;
import com.unimag.edu.proyecto_final.security.jwt.JwtService;
import com.unimag.edu.proyecto_final.security.repo.AppUserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AppUserRepository users;
    private final BCryptPasswordEncoder encoder;
    private final AuthenticationManager authManager;
    private final JwtService jwt;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request){
        if (users.existsByEmailIgnoreCase(request.email())){
            return ResponseEntity.badRequest().build();
        }
        var roles = Optional.ofNullable(request.roles())
                .filter(r -> !r.isEmpty())
                .orElseGet(() -> Set.of(SecurityRole.ROLE_PASSENGER));

        var user = AppUser.builder()
                .email(request.email())
                .password(encoder.encode(request.password()))
                .securityRoles(roles)
                .build();

        users.save(user);

        var principal = User.withUsername(user.getEmail())
                .password(user.getPassword())
                .authorities(roles.stream().map(Enum::name).toArray(String[]::new))
                .build();

        var token = jwt.generateToken(principal, Map.of("roles", roles));
        return  ResponseEntity.ok(new AuthResponse(token,"Bearer", jwt.getExpirationSeconds()));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request){
        authManager.authenticate(new UsernamePasswordAuthenticationToken(request.email(), request.password()));
        var user = users.findByEmailIgnoreCase(request.email()).orElseThrow();
        var principal = User.withUsername(user.getEmail())
                .password(user.getPassword())
                .authorities(user.getSecurityRoles().stream().map(Enum::name).toArray(String[]::new))
                .build();

        var token = jwt.generateToken(principal, Map.of("roles", user.getSecurityRoles()));
        return ResponseEntity.ok(new AuthResponse(token,"Bearer", jwt.getExpirationSeconds()));
    }
}
