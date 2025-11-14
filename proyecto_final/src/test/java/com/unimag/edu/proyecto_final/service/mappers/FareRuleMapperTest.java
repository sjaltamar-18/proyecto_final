package com.unimag.edu.proyecto_final.service.mappers;

import com.unimag.edu.proyecto_final.api.dto.FareRuleDtos.*;
import com.unimag.edu.proyecto_final.domine.entities.FareRule;
import com.unimag.edu.proyecto_final.domine.entities.Route;
import com.unimag.edu.proyecto_final.domine.entities.Stop;
import com.unimag.edu.proyecto_final.domine.entities.enumera.DynamicPricing;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class FareRuleMapperTest {

    private final FareRuleMapper mapper = Mappers.getMapper(FareRuleMapper.class);

    @Test
    void toEntity_shouldMapCreateRequest() {
        // given
        var request = new FareRuleCreateRequest(
                1L,
                2L,
                3L,
                25000.00,
                "{\"student\":0.2,\"child\":0.5}",
                true
        );


        // when
        FareRule entity = mapper.toEntity(request);

        // then
        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isNull(); // ignored by mapper
        assertThat(entity.getBasePrice()).isEqualByComparingTo(25000.00);
        assertThat(entity.getDiscountPrice()).contains("\"student\":0.2");
        assertThat(entity.getDiscountPrice()).contains("\"child\":0.5");
        assertThat(entity.getDynamicPricing()).isEqualTo(DynamicPricing.ON);
        assertThat(entity.getRoute()).isNull();
        assertThat(entity.getFromStop()).isNull();
        assertThat(entity.getToStop()).isNull();
    }

    @Test
    void toResponse_shouldMapEntity() {
        // given
        var route = Route.builder().id(1L).build();
        var fromStop = Stop.builder().id(2L).stopName("Bogotá").build();
        var toStop = Stop.builder().id(3L).stopName("Tunja").build();

        var entity = FareRule.builder()
                .id(10L)
                .route(route)
                .fromStop(fromStop)
                .toStop(toStop)
                .basePrice(30000.00)
                .discountPrice("\"students\":0.1")
                .dynamicPricing(DynamicPricing.OFF)
                .build();

        // when
        FareRuleResponse dto = mapper.toResponse(entity);

        // then
        assertThat(dto).isNotNull();
        assertThat(dto.id()).isEqualTo(10L);
        assertThat(dto.routeId()).isEqualTo(1L);
        assertThat(dto.fromStopId()).isEqualTo(2L);
        assertThat(dto.toStopId()).isEqualTo(3L);
        assertThat(dto.basePrice()).isEqualByComparingTo(30000.00);
        assertThat(dto.discounts()).isEqualTo("\"students\":0.1");
        assertThat(dto.dynamicPricing()).isFalse(); // OFF enum → false boolean
    }

    @Test
    void updateEntityFromDto_shouldUpdateNonNullFields() {
        // given
        var route = Route.builder().id(1L).build();
        var fromStop = Stop.builder().id(2L).build();
        var toStop = Stop.builder().id(3L).build();

        var entity = FareRule.builder()
                .id(5L)
                .route(route)
                .fromStop(fromStop)
                .toStop(toStop)
                .basePrice(20000.00)
                .discountPrice("\"child\":0.5")
                .dynamicPricing(DynamicPricing.OFF)
                .build();

        var update = new FareRuleUpdateRequest(
                22000.00,
                "\"student\":0.15",
                true // dynamicPricing → ON
        );

        // when
        mapper.updateEntityFromDto(update, entity);

        // then
        assertThat(entity.getBasePrice()).isEqualByComparingTo(22000.00);
        assertThat(entity.getDiscountPrice()).isEqualTo("\"student\":0.15");
        assertThat(entity.getDynamicPricing()).isEqualTo(DynamicPricing.ON);
        assertThat(entity.getId()).isEqualTo(5L);
        assertThat(entity.getRoute().getId()).isEqualTo(1L);
    }
}
