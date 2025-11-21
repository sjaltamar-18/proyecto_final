

package com.unimag.edu.proyecto_final.service;

import com.unimag.edu.proyecto_final.api.dto.ConfiDtos.*;
import com.unimag.edu.proyecto_final.domine.entities.Confi;
import com.unimag.edu.proyecto_final.domine.repository.ConfiRepository;
import com.unimag.edu.proyecto_final.exception.NotFoundException;
import com.unimag.edu.proyecto_final.service.mappers.ConfiMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

import org.springframework.data.domain.*;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class ConfiServicelmplTest {

    @Mock
    private ConfiRepository confiRepository;

    @Mock
    private ConfiMapper confiMapper;

    private ConfiService service;

    @BeforeEach
    void setup() {
        service = new ConfiServicelmpl(confiRepository, confiMapper);
    }


    @Test
    void create_debe_crear_config_correctamente() {
        ConfigCreateRequest req = new ConfigCreateRequest("LIMIT", "20");

        when(confiRepository.existsByKey("LIMIT")).thenReturn(false);

        Confi entity = new Confi(null, "LIMIT", "20");
        when(confiMapper.toEntity(req)).thenReturn(entity);
        when(confiRepository.save(entity)).thenReturn(entity);

        ConfigResponse response = new ConfigResponse(1L, "LIMIT", "20");
        when(confiMapper.toResponse(entity)).thenReturn(response);

        ConfigResponse result = service.create(req);

        assertThat(result).isEqualTo(response);
        verify(confiRepository).save(entity);
        verify(confiMapper).toResponse(entity);
    }

    @Test
    void create_debe_fallar_si_key_ya_existe() {
        ConfigCreateRequest req = new ConfigCreateRequest("LIMIT", "20");

        when(confiRepository.existsByKey("LIMIT")).thenReturn(true);

        assertThatThrownBy(() -> service.create(req))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("config already exists");
    }


    @Test
    void get_debe_retornar_config() {
        Confi confi = new Confi(1L, "LIMIT", "20");

        when(confiRepository.findByKey("LIMIT")).thenReturn(Optional.of(confi));
        ConfigResponse mapped = new ConfigResponse(1L, "LIMIT", "20");
        when(confiMapper.toResponse(confi)).thenReturn(mapped);

        ConfigResponse result = service.get("LIMIT");

        assertThat(result).isEqualTo(mapped);
    }

    @Test
    void get_debe_fallar_si_no_existe() {
        when(confiRepository.findByKey("X")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.get("X"))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("config not found");
    }


    @Test
    void list_debe_retornar_paginado() {
        Pageable pageable = PageRequest.of(0, 10);
        Confi confi = new Confi(1L, "LIMIT", "20");

        Page<Confi> page = new PageImpl<>(java.util.List.of(confi));
        when(confiRepository.findAll(pageable)).thenReturn(page);

        ConfigResponse res = new ConfigResponse(1L, "LIMIT", "20");
        when(confiMapper.toResponse(confi)).thenReturn(res);

        Page<ConfigResponse> result = service.list(pageable);

        assertThat(result.getContent()).containsExactly(res);
    }

     @Test
    void update_debe_actualizar_correctamente() {
        Confi confi = new Confi(1L, "LIMIT", "20");
        when(confiRepository.findByKey("LIMIT")).thenReturn(Optional.of(confi));

        ConfigUpdateRequest req = new ConfigUpdateRequest("30");

        doAnswer(inv -> {
            confi.setValue("30");
            return null;
        }).when(confiMapper).updateEntityFromDto(req, confi);

        ConfigResponse res = new ConfigResponse(1L, "LIMIT", "30");
        when(confiMapper.toResponse(confi)).thenReturn(res);

        ConfigResponse result = service.update("LIMIT", req);

        assertThat(result).isEqualTo(res);
        assertThat(confi.getValue()).isEqualTo("30");
        verify(confiRepository).save(confi);
    }

    @Test
    void update_debe_fallar_si_no_existe() {
        when(confiRepository.findByKey("x")).thenReturn(Optional.empty());

        ConfigUpdateRequest req = new ConfigUpdateRequest("123");

        assertThatThrownBy(() -> service.update("x", req))
                .isInstanceOf(NotFoundException.class);
    }

     @Test
    void delete_debe_eliminar_correctamente() {
        Confi confi = new Confi(1L, "LIMIT", "20");
        when(confiRepository.findByKey("LIMIT")).thenReturn(Optional.of(confi));

        service.delete("LIMIT");

        verify(confiRepository).delete(confi);
    }

    @Test
    void delete_debe_fallar_si_no_existe() {
        when(confiRepository.findByKey("xxx")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete("xxx"))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void getValue_debe_retornar_valor() {
        Confi confi = new Confi(1L, "LIMIT", "20");
        when(confiRepository.findByKey("LIMIT")).thenReturn(Optional.of(confi));

        String val = service.getValue("LIMIT");

        assertThat(val).isEqualTo("20");
    }

    @Test
    void getDouble_debe_retornar_double() {
        Confi confi = new Confi(1L, "LIMIT", "20.5");
        when(confiRepository.findByKey("LIMIT")).thenReturn(Optional.of(confi));

        double value = service.getDouble("LIMIT");

        assertThat(value).isEqualTo(20.5);
    }

    @Test
    void getInt_debe_retornar_int() {
        Confi confi = new Confi(1L, "LIMIT", "15");
        when(confiRepository.findByKey("LIMIT")).thenReturn(Optional.of(confi));

        int value = service.getInt("LIMIT");

        assertThat(value).isEqualTo(15);
    }

    @Test
    void getBoolean_debe_retornar_boolean() {
        Confi confi = new Confi(1L, "FLAG", "true");
        when(confiRepository.findByKey("FLAG")).thenReturn(Optional.of(confi));

        boolean value = service.getBoolean("FLAG");

        assertThat(value).isTrue();
    }


    @Test
    void setValue_debe_crear_si_no_existe() {
        when(confiRepository.findByKey("LIMIT")).thenReturn(Optional.empty());

        service.setValue("LIMIT", 99);

        verify(confiRepository).save(argThat(c ->
                c.getKey().equals("LIMIT") &&
                        c.getValue().equals("99")
        ));
    }


    @Test
    void setValue_debe_actualizar_si_existe() {
        Confi confi = new Confi(1L, "LIMIT", "20");
        when(confiRepository.findByKey("LIMIT")).thenReturn(Optional.of(confi));

        service.setValue("LIMIT", 55);

        assertThat(confi.getValue()).isEqualTo("55");
        verify(confiRepository).save(confi);
    }
}
