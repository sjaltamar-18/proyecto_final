package com.unimag.edu.proyecto_final.service;

import com.unimag.edu.proyecto_final.api.dto.UserDtos;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional

public class UserServicelmpl implements UserService {
    @Override
    public UserDtos.UserResponse register(UserDtos.UserCreateRequest request) {
        return null;
    }

    @Override
    public UserDtos.UserResponse get(Long id) {
        return null;
    }

    @Override
    public Page<UserDtos.UserResponse> list(Pageable pageable) {
        return null;
    }

    @Override
    public UserDtos.UserResponse update(Long id, UserDtos.UserUpdateRequest request) {
        return null;
    }

    @Override
    public void delete(Long id) {

    }
}
