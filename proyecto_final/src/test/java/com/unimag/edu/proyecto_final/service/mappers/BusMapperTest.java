
package com.unimag.edu.proyecto_final.service.mappers;

import com.unimag.edu.proyecto_final.api.dto.BusDtos.*;
import com.unimag.edu.proyecto_final.domine.entities.Bus;
import com.unimag.edu.proyecto_final.domine.entities.enumera.StatusBus;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.Map;

import static com.unimag.edu.proyecto_final.domine.entities.enumera.StatusBus.ACTIVE;
import static org.assertj.core.api.Assertions.assertThat;

class BusMapperTest {

    private final BusMapper mapper = Mappers.getMapper(BusMapper.class);

    @Test
    void toEntity_shouldMapCreateRequest() {

        var request = new BusCreateRequest(
                "ABC123",     // plate
                40,           // capacity
                "{\"wifi\":true,\"usb\":true}"

        );

        Bus entity = mapper.toEntity(request);

        assertThat(entity).isNotNull();
        assertThat(entity.getPlate()).isEqualTo("ABC123");
        assertThat(entity.getCapacity()).isEqualTo(40);
        assertThat(entity.getAmenities()).contains("\"wifi\":true");
        assertThat(entity.getAmenities()).contains("\"usb\":true");
    }

    @Test
    void toResponse_shouldMapEntity() {

        var entity = Bus.builder()
                .id(10L)
                .plate("XYZ789")
                .capacity(50)
                .amenities("{\"wifi\":true,\"usb\":false}")
                .status(ACTIVE)
                .build();


        BusResponse dto = mapper.toResponse(entity);


        assertThat(dto).isNotNull();
        assertThat(dto.id()).isEqualTo(10L);
        assertThat(dto.plate()).isEqualTo("XYZ789");
        assertThat(dto.capacity()).isEqualTo(50);
        assertThat(dto.amenities()).contains("\"wifi\":true");
        assertThat(dto.amenities()).contains("\"usb\":false");
        assertThat(dto.status()).isEqualTo("ACTIVE");
    }

    @Test
    void updateEntityFromDto_shouldUpdateNonNullFields() {

        var entity = Bus.builder()
                .id(5L)
                .plate("NEW456")
                .capacity(45)
                .amenities("{\"wifi\": false}")
                .status(StatusBus.INACTIVE)
                .build();

        var update = new BusUpdateRequest(
                35,
                "{\"wifi\":true,\"ac\":true}",
                "ACTIVE"

        );        // when
        mapper.updateEntityFromDto(update, entity);


        assertThat(entity.getPlate()).isEqualTo("NEW456");
        assertThat(entity.getCapacity()).isEqualTo(35);
        assertThat(entity.getAmenities()).contains("\"wifi\":true");
        assertThat(entity.getAmenities()).contains("\"ac\":true");
        assertThat(entity.getStatus()).isEqualTo(ACTIVE);
        assertThat(entity.getId()).isEqualTo(5L);
    }

}
