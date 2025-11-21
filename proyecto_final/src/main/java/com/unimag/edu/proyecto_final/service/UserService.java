
package com.unimag.edu.proyecto_final.service;

import com.unimag.edu.proyecto_final.api.dto.UserDtos.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {

    UserResponse create(UserCreateRequest request);

    UserResponse get(Long id);

    UserResponse getByEmail(String email);

    List<UserResponse> listByRole(String role);

    Page<UserResponse> list(Pageable pageable);

    UserResponse update(Long id, UserUpdateRequest request);

    void delete(Long id);
}
