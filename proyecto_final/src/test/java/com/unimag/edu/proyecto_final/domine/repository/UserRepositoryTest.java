package com.unimag.edu.proyecto_final.domine.repository;

import com.unimag.edu.proyecto_final.domine.entities.User;
import com.unimag.edu.proyecto_final.domine.entities.enumera.Role;
import com.unimag.edu.proyecto_final.domine.entities.enumera.StatusUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class UserRepositoryTest extends AbstractRepositoryIT {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Debe encontrar usuario por email")
    void findByEmail() {
        User user = userRepository.save(User.builder()
                .name("Luis")
                .email("luis@test.com")
                .passwordHash("123456")
                .role(Role.ADMIN)
                .status(StatusUser.ACTIVE)
                .build());

        var result = userRepository.findByEmail("luis@test.com");

        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo(user.getEmail());
    }

    @Test
    @DisplayName("Debe retornar usuarios por rol")
    void findAllByRole() {
        userRepository.save(User.builder().email("driver1@test.com").role(Role.DRIVER).status(StatusUser.ACTIVE).build());
        userRepository.save(User.builder().email("driver2@test.com").role(Role.DRIVER).status(StatusUser.ACTIVE).build());

        List<User> result = userRepository.findAllByRole(Role.DRIVER);

        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("Debe contar usuarios por rol")
    void countByRole() {
        userRepository.save(User.builder().email("a@test.com").role(Role.ADMIN).status(StatusUser.ACTIVE).build());
        userRepository.save(User.builder().email("b@test.com").role(Role.ADMIN).status(StatusUser.ACTIVE).build());

        Long count = userRepository.countByRole(Role.ADMIN);

        assertThat(count).isEqualTo(2L);
    }
}
