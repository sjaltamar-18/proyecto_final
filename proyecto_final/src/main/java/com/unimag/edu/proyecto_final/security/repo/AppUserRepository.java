package com.unimag.edu.proyecto_final.security.repo;

import com.unimag.edu.proyecto_final.security.domine.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AppUserRepository  extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByEmailIgnoreCase(String email);
    Boolean existsByEmailIgnoreCase(String email);
}