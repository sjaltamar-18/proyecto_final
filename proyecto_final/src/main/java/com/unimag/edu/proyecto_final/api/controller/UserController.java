package com.unimag.edu.proyecto_final.api.controller;

import com.unimag.edu.proyecto_final.api.dto.UserDtos.*;
import com.unimag.edu.proyecto_final.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    @PostMapping
    public ResponseEntity<UserResponse> create (@Valid@RequestBody UserCreateRequest userCreateRequest){
        UserResponse created = service.create(userCreateRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable Long id){
        return ResponseEntity.ok(service.get(id));
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UserResponse> getUserByEmail(@PathVariable String email){
        return ResponseEntity.ok(service.getByEmail(email));
    }

    @GetMapping("/role/{role}")
    public ResponseEntity<List<UserResponse>> listByRole(@PathVariable String role){
        List<UserResponse> list = service.listByRole(role);
        return new ResponseEntity<>(list,HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> update(@PathVariable Long id,@Valid @RequestBody UserUpdateRequest userUpdateRequest){
        return ResponseEntity.ok(service.update(id, userUpdateRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<UserResponse> delete(@PathVariable Long id){
        return ResponseEntity.noContent().build();
    }


}