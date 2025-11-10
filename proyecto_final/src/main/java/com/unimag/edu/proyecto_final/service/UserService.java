package com.unimag.edu.proyecto_final.service;

import com.unimag.edu.proyecto_final.api.dto.UserDtos.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {

    UserResponse register(UserCreateRequest request);

    UserResponse get(Long id);

    Page<UserResponse> list(Pageable pageable);

    UserResponse update(Long id, UserUpdateRequest request);

    void delete(Long id);
}
