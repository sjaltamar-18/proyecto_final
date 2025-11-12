package com.unimag.edu.proyecto_final.service.mappers;

import com.unimag.edu.proyecto_final.api.dto.ParcelDtos.*;
import com.unimag.edu.proyecto_final.domine.entities.Parcel;
import com.unimag.edu.proyecto_final.domine.entities.Stop;
import com.unimag.edu.proyecto_final.domine.entities.enumera.StatusParcel;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class ParcelMapperTest {

    private final ParcelMapper mapper = Mappers.getMapper(ParcelMapper.class);

    @Test
    void toEntity_shouldMapCreateRequest() {
        // given
        var request = new ParcelCreateRequest(
                "PKG001",
                "Juan Pérez",
                "3001234567",
                "María Gómez",
                "3017654321",
                1L, // fromStopId
                2L, // toStopId
                25000.00 // price
        );

        // when
        Parcel entity = mapper.toEntity(request);

        // then
        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isNull(); // ignored by mapper
        assertThat(entity.getCode()).isEqualTo("PKG001");
        assertThat(entity.getSenderName()).isEqualTo("Juan Pérez");
        assertThat(entity.getSenderPhone()).isEqualTo("3001234567");
        assertThat(entity.getReceiverName()).isEqualTo("María Gómez");
        assertThat(entity.getReceiverPhone()).isEqualTo("3017654321");
        assertThat(entity.getPrice()).isEqualTo(25000.00);
        assertThat(entity.getStatusParcel()).isEqualTo(StatusParcel.CREATED);
        assertThat(entity.getFromStop()).isNull();
        assertThat(entity.getToStop()).isNull();
    }

    @Test
    void toResponse_shouldMapEntity() {
        // given
        var fromStop = Stop.builder().id(1L).stopName("Bogotá").build();
        var toStop = Stop.builder().id(2L).stopName("Tunja").build();

        var entity = Parcel.builder()
                .id(10L)
                .code("PKG999")
                .senderName("Luis Torres")
                .senderPhone("3007778888")
                .receiverName("Ana Ramírez")
                .receiverPhone("3015556666")
                .price(BigDecimal.valueOf(32000.00))
                .statusParcel(StatusParcel.IN_PROGRESS)
                .fromStop(fromStop)
                .toStop(toStop)
                .build();

        // when
        ParcelResponse dto = mapper.toResponse(entity);

        // then
        assertThat(dto).isNotNull();
        assertThat(dto.id()).isEqualTo(10L);
        assertThat(dto.code()).isEqualTo("PKG999");
        assertThat(dto.senderName()).isEqualTo("Luis Torres");
        assertThat(dto.receiverName()).isEqualTo("Ana Ramírez");
        assertThat(dto.fromStopId()).isEqualTo(1L);
        assertThat(dto.toStopId()).isEqualTo(2L);
        assertThat(dto.status()).isEqualTo("IN_PROGRESS");
        assertThat(dto.price()).isEqualTo(32000.00);
        assertThat(dto.proofPhotoUrl()).isEqualTo("URL_IMG");
        assertThat(dto.deliveryOtp()).isEqualTo("999999");
    }

    @Test
    void updateEntityFromStatusRequest_shouldUpdateOnlyStatus() {
        // given
        var fromStop = Stop.builder().id(1L).build();
        var toStop = Stop.builder().id(2L).build();

        var entity = Parcel.builder()
                .id(15L)
                .code("PKG555")
                .senderName("Carlos Gómez")
                .receiverName("Laura Díaz")
                .price(BigDecimal.valueOf(15000.00))
                .statusParcel(StatusParcel.CREATED)
                .fromStop(fromStop)
                .toStop(toStop)
                .build();

        var update = new ParcelUpdateRequest("DELIVERED");

        // when
        mapper.updateEntityFromStatusRequest(update, entity);

        // then
        assertThat(entity.getStatusParcel()).isEqualTo(StatusParcel.COMPLETED);
        assertThat(entity.getId()).isEqualTo(15L);
        assertThat(entity.getCode()).isEqualTo("PKG555");
        assertThat(entity.getFromStop().getId()).isEqualTo(1L);
    }

    @Test
    void updateEntityFromStatusRequest_shouldHandleInvalidEnumValuesGracefully() {
        // given
        var entity = Parcel.builder()
                .id(20L)
                .statusParcel(StatusParcel.IN_PROGRESS)
                .build();

        var update = new ParcelUpdateRequest("FAILED");

        // when
        mapper.updateEntityFromStatusRequest(update, entity);

        // then
        // valor por defecto definido en mapStatus → CREATED
        assertThat(entity.getStatusParcel()).isEqualTo(StatusParcel.CREATED);
    }
}
