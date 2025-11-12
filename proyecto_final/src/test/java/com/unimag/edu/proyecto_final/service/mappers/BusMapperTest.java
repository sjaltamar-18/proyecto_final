package com.unimag.edu.proyecto_final.service.mappers;

import com.unimag.edu.proyecto_final.api.dto.BusDtos.*;
import com.unimag.edu.proyecto_final.domine.entities.Bus;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class BusMapperTest {

    private final BusMapper mapper = Mappers.getMapper(BusMapper.class);

    @Test
    void toEntity_shouldMapCreateRequest() {
        // given
        var request = new BusCreateRequest(
                "ABC123",     // plate
                40,           // capacity
                Map.of("wifi", true, "usb", true)

        );

        // when
        Bus entity = mapper.toEntity(request);

        // then
        assertThat(entity).isNotNull();
        assertThat(entity.getPlate()).isEqualTo("ABC123");
        assertThat(entity.getCapacity()).isEqualTo(40);
        assertThat(entity.getAmenities()).contains("\"wifi\":true");
        assertThat(entity.getAmenities()).contains("\"usb\":true");
        assertThat(entity.getStatus()).isEqualTo("ACTIVE");
    }

    @Test
    void toResponse_shouldMapEntity() {
        // given
        var amenities = Map.of("wifi", true, "usb", false);
        var entity = Bus.builder()
                .id(10L)
                .plate("XYZ789")
                .capacity(50)
                .amenities(amenities.toString())
                .build();

        // when
        BusResponse dto = mapper.toResponse(entity);

        // then
        assertThat(dto).isNotNull();
        assertThat(dto.id()).isEqualTo(10L);
        assertThat(dto.plate()).isEqualTo("XYZ789");
        assertThat(dto.capacity()).isEqualTo(50);
        assertThat(dto.amenities()).containsEntry("wifi", true);
        assertThat(dto.amenities()).containsEntry("usb", false);
        assertThat(dto.status()).isEqualTo("IN_SERVICE");
    }

    @Test
    void updateEntityFromDto_shouldUpdateNonNullFields() {
        // given
        var entity = Bus.builder()
                .id(5L)
                .plate("OLD123")
                .capacity(35)
                .amenities("{\"wifi\": false}")
                .build();

        var update = new BusUpdateRequest(
                35,
                Map.of("wifi", true, "ac", true),
                "ACTIVE"
        );

        // when
        mapper.updateEntityFromDto(update, entity);

        // then
        assertThat(entity.getPlate()).isEqualTo("NEW456");
        assertThat(entity.getCapacity()).isEqualTo(45);
        assertThat(entity.getAmenities()).contains("\"wifi\":true");
        assertThat(entity.getAmenities()).contains("\"ac\":true");
        assertThat(entity.getStatus()).isEqualTo("ACTIVE");
        assertThat(entity.getId()).isEqualTo(5L);
    }
}
