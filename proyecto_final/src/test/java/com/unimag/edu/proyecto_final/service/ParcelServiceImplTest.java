package com.unimag.edu.proyecto_final.service;

import com.unimag.edu.proyecto_final.api.dto.ParcelDtos.*;
import com.unimag.edu.proyecto_final.domine.entities.Parcel;
import com.unimag.edu.proyecto_final.domine.entities.Stop;
import com.unimag.edu.proyecto_final.domine.entities.enumera.StatusParcel;
import com.unimag.edu.proyecto_final.domine.repository.ParcelRepository;
import com.unimag.edu.proyecto_final.domine.repository.StopRepository;
import com.unimag.edu.proyecto_final.exception.NotFoundException;
import com.unimag.edu.proyecto_final.service.mappers.ParcelMapper;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

@ExtendWith(MockitoExtension.class)
class ParcelServiceImplTest {

    @Mock
    private ParcelRepository parcelRepository;

    @Mock
    private StopRepository stopRepository;

    @Mock
    private ParcelMapper parcelMapper;

    @InjectMocks
    private ParcelServicelmpl service;

    @BeforeEach
    void setUp() {}


    @Test
    void create_debe_crear_un_parcel_correctamente() {
        ParcelCreateRequest req = mock(ParcelCreateRequest.class);

        when(req.code()).thenReturn("PX100");
        when(req.fromStopId()).thenReturn(1L);
        when(req.toStopId()).thenReturn(2L);
        when(req.price()).thenReturn(BigDecimal.valueOf(15000.0));

        when(parcelRepository.existsByCode("PX100")).thenReturn(false);

        Stop from = Stop.builder().id(1L).build();
        Stop to = Stop.builder().id(2L).build();

        when(stopRepository.findById(1L)).thenReturn(Optional.of(from));
        when(stopRepository.findById(2L)).thenReturn(Optional.of(to));

        Parcel parcel = new Parcel();
        when(parcelMapper.toEntity(req)).thenReturn(parcel);

        Parcel saved = Parcel.builder().id(10L).build();
        when(parcelRepository.save(parcel)).thenReturn(saved);

        ParcelResponse mapped = mock(ParcelResponse.class);
        when(parcelMapper.toResponse(saved)).thenReturn(mapped);

        var result = service.create(req);

        assertThat(result).isEqualTo(mapped);

        verify(parcelRepository).existsByCode("PX100");
        verify(stopRepository).findById(1L);
        verify(stopRepository).findById(2L);
        verify(parcelMapper).toEntity(req);
        verify(parcelRepository).save(parcel);
    }

    @Test
    void create_debe_fallar_si_code_ya_existe() {
        ParcelCreateRequest req = mock(ParcelCreateRequest.class);
        when(req.code()).thenReturn("PX100");

        when(parcelRepository.existsByCode("PX100")).thenReturn(true);

        assertThatThrownBy(() -> service.create(req))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Code already exists");
    }

    @Test
    void create_debe_fallar_si_fromStop_no_existe() {
        ParcelCreateRequest req = mock(ParcelCreateRequest.class);
        lenient().when(req.code()).thenReturn("PX100");
        lenient().when(req.fromStopId()).thenReturn(1L);
        lenient().when(req.toStopId()).thenReturn(2L);

        lenient().when(parcelRepository.existsByCode("PX100")).thenReturn(false);
        when(stopRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.create(req))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Stop not found");
    }

    @Test
    void create_debe_fallar_si_toStop_no_existe() {
        ParcelCreateRequest req = mock(ParcelCreateRequest.class);
        when(req.code()).thenReturn("PX100");
        when(req.fromStopId()).thenReturn(1L);
        when(req.toStopId()).thenReturn(2L);

        when(parcelRepository.existsByCode("PX100")).thenReturn(false);
        when(stopRepository.findById(1L)).thenReturn(Optional.of(mock(Stop.class)));
        when(stopRepository.findById(2L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.create(req))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Stop not found");
    }

    // ==========================================================================
    // GET
    // ==========================================================================
    @Test
    void get_debe_retornar_parcel_si_existe() {
        Parcel parcel = Parcel.builder().id(5L).build();
        when(parcelRepository.findById(5L)).thenReturn(Optional.of(parcel));

        ParcelResponse mapped = mock(ParcelResponse.class);
        when(parcelMapper.toResponse(parcel)).thenReturn(mapped);

        var result = service.get(5L);

        assertThat(result).isEqualTo(mapped);
        verify(parcelRepository).findById(5L);
    }

    @Test
    void get_debe_fallar_si_no_existe() {
        when(parcelRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.get(99L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Parcel not found");
    }

    // ==========================================================================
    // GET BY CODE
    // ==========================================================================
    @Test
    void getByCode_debe_retornar_correctamente() {
        Parcel parcel = Parcel.builder().id(7L).build();
        when(parcelRepository.findByCode("PX500")).thenReturn(Optional.of(parcel));

        ParcelResponse mapped = mock(ParcelResponse.class);
        when(parcelMapper.toResponse(parcel)).thenReturn(mapped);

        var result = service.getByCode("PX500");

        assertThat(result).isEqualTo(mapped);
        verify(parcelRepository).findByCode("PX500");
    }

    @Test
    void getByCode_debe_fallar_si_no_existe() {
        when(parcelRepository.findByCode("NOEXISTE")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getByCode("NOEXISTE"))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Parcel not found");
    }

    // ==========================================================================
    // LIST BY STATUS
    // ==========================================================================
    @Test
    void listByStatus_debe_listar_correctamente() {
        Pageable pageable = PageRequest.of(0, 10);

        Parcel p = Parcel.builder().id(1L).statusParcel(StatusParcel.CREATED).build();
        List<Parcel> list = List.of(p);

        when(parcelRepository.findByStatusParcel(StatusParcel.CREATED)).thenReturn(list);

        when(parcelMapper.toResponse(any())).thenReturn(mock(ParcelResponse.class));

        var page = service.listByStatus("created", pageable);

        assertThat(page.getTotalElements()).isEqualTo(1);
        verify(parcelRepository).findByStatusParcel(StatusParcel.CREATED);
        verify(parcelMapper).toResponse(any());
    }

    // ==========================================================================
    // UPDATE
    // ==========================================================================
    @Test
    void update_debe_modificar_y_guardar() {
        Parcel parcel = Parcel.builder().id(50L).build();
        when(parcelRepository.findById(50L)).thenReturn(Optional.of(parcel));

        ParcelUpdateRequest req = mock(ParcelUpdateRequest.class);

        Parcel saved = Parcel.builder().id(50L).build();
        when(parcelRepository.save(parcel)).thenReturn(saved);

        ParcelResponse mapped = mock(ParcelResponse.class);
        when(parcelMapper.toResponse(saved)).thenReturn(mapped);

        var result = service.update(50L, req);

        verify(parcelMapper).updateEntityFromStatusRequest(req, parcel);
        verify(parcelRepository).save(parcel);
        verify(parcelMapper).toResponse(saved);

        assertThat(result).isEqualTo(mapped);
    }

    @Test
    void update_debe_fallar_si_no_existe() {
        when(parcelRepository.findById(88L)).thenReturn(Optional.empty());
        ParcelUpdateRequest req = mock(ParcelUpdateRequest.class);

        assertThatThrownBy(() -> service.update(88L, req))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Parcel not found");
    }

    // ==========================================================================
    // DELETE
    // ==========================================================================
    @Test
    void delete_debe_eliminar_si_existe() {
        Parcel parcel = Parcel.builder().id(33L).build();
        when(parcelRepository.findById(33L)).thenReturn(Optional.of(parcel));

        service.delete(33L);

        verify(parcelRepository).delete(parcel);
    }

    @Test
    void delete_debe_fallar_si_no_existe() {
        when(parcelRepository.findById(1000L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(1000L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Parcel not found");
    }
}
