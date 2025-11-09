package com.unimag.edu.proyecto_final.domine.repository;

import com.unimag.edu.proyecto_final.domine.entities.Confi;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ConfiRepositoryTest extends  AbstractRepositoryIT{
    @Autowired
    private ConfiRepository confiRepository;

    @Test
    @DisplayName("Debe encontrar una configuración por clave")
    void findByKey() {
        Confi confi = confiRepository.save(Confi.builder()
                .key("system.currency")
                .value("COP")
                .build());

        var result = confiRepository.findByKey("system.currency");

        assertThat(result).isPresent();
        assertThat(result.get().getValue()).isEqualTo("COP");
    }

    @Test
    @DisplayName("Debe verificar si existe una clave de configuración")
    void existsByKey() {
        confiRepository.save(Confi.builder()
                .key("refund.enabled")
                .value("true")
                .build());

        boolean exists = confiRepository.existsByKey("refund.enabled");

        assertThat(exists).isTrue();

    }

    @Test
    @DisplayName("Debe recuperar varias configuraciones por lista de claves")
    void findByKeys() {
        confiRepository.save(Confi.builder().key("system.timezone").value("America/Bogota").build());
        confiRepository.save(Confi.builder().key("system.language").value("es").build());

        var result = confiRepository.findByKeys(List.of("system.timezone", "system.language"));

        assertThat(result).hasSize(2);
        assertThat(result)
                .extracting(Confi::getKey)
                .containsExactlyInAnyOrder("system.timezone", "system.language");
    }

    @Test
    @DisplayName("Debe encontrar configuraciones por prefijo de clave")
    void findByPrefix() {
        confiRepository.save(Confi.builder().key("refund.enabled").value("true").build());
        confiRepository.save(Confi.builder().key("refund.limit").value("50000").build());
        confiRepository.save(Confi.builder().key("system.currency").value("COP").build());

        var result = confiRepository.findByPrefix("refund.");

        assertThat(result).hasSize(2);
        assertThat(result)
                .extracting(Confi::getKey)
                .allMatch(k -> k.startsWith("refund."));
    }
}