
package com.unimag.edu.proyecto_final.domine.repository;

import com.unimag.edu.proyecto_final.domine.entities.User;
import com.unimag.edu.proyecto_final.domine.entities.enumera.Role;
import com.unimag.edu.proyecto_final.domine.entities.enumera.StatusUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {


    Optional<User> findByEmail(String email);


    Optional<User> findByPhone(String phone);


    boolean existsByEmail(String email);


    List<User> findAllByRole(Role role);


    List<User> findByStatus(StatusUser status);

    @Query("SELECT COUNT(u) FROM User u WHERE u.role = :role")
    Long countByRole(@Param("role") Role role);
}
