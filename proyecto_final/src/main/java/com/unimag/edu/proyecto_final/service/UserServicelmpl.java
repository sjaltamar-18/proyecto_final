package com.unimag.edu.proyecto_final.service;

import com.unimag.edu.proyecto_final.api.dto.UserDtos;
import com.unimag.edu.proyecto_final.domine.entities.User;
import com.unimag.edu.proyecto_final.domine.entities.enumera.Role;
import com.unimag.edu.proyecto_final.domine.entities.enumera.StatusUser;
import com.unimag.edu.proyecto_final.domine.repository.UserRepository;
import com.unimag.edu.proyecto_final.service.mappers.UserMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional

public class UserServicelmpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDtos.UserResponse create(UserDtos.UserCreateRequest request) {
       if (userRepository.existsByEmail(request.email())){
           throw new IllegalArgumentException("User already exists");
       }
        User user = userMapper.toEntity(request);
        if (request.profile() != null) {
            user.setPhone(request.profile().phone());
            user.setRole(mapRole(request.profile().role()));
            user.setStatus(mapStatus(request.profile().status()));
        } else {
            user.setRole(Role.PASSENGER);
            user.setStatus(StatusUser.ACTIVE);
        }

        user.setPasswordHash(encryptPassword(request.password()));

        User saved = userRepository.save(user);
        return userMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDtos.UserResponse get(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        return userMapper.toResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDtos.UserResponse getByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        return userMapper.toResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDtos.UserResponse> listByRole(String role) {
        Role roleEnum = mapRole(role);
        List<User> users = userRepository.findAllByRole(roleEnum);
        return users.stream().map(userMapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserDtos.UserResponse> list(Pageable pageable) {
        return userRepository.findAll(pageable).map(userMapper::toResponse);
    }

    @Override
    public UserDtos.UserResponse update(Long id, UserDtos.UserUpdateRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (request.email() != null && !request.email().equals(user.getEmail())) {
            throw new IllegalArgumentException("User already exists");
        }
        if (request.name() != null) user.setName(request.name());
        if (request.email() != null) user.setEmail(request.email());

        if (request.profile() != null) {
            var profile = request.profile();
            if (profile.phone() != null) user.setPhone(profile.phone());
            if (profile.role() != null) user.setRole(mapRole(profile.role()));
            if (profile.status() != null) user.setStatus(mapStatus(profile.status()));
        }

        User updated = userRepository.save(user);
        return userMapper.toResponse(updated);
    }

    @Override
    public void delete(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        user.setStatus(StatusUser.INACTIVE);
        userRepository.save(user);
    }

    // Metodos auxiliares

    private String encryptPassword(String plainPassword) {
        return "{hashed}" + plainPassword;
    }

    private Role mapRole(String value) {
        if (value == null) return Role.PASSENGER;
        try {
            return Role.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException ex) {
            return Role.PASSENGER;
        }
    }

    private StatusUser mapStatus(String value) {
        if (value == null) return StatusUser.ACTIVE;
        try {
            return StatusUser.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException ex) {
            return StatusUser.ACTIVE;
        }
    }
}
