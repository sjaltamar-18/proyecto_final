package com.unimag.edu.proyecto_final.service.mappers;

import com.unimag.edu.proyecto_final.api.dto.AssignmentDtos.*;
import com.unimag.edu.proyecto_final.domine.entities.Assignment;
import com.unimag.edu.proyecto_final.domine.entities.Trip;
import com.unimag.edu.proyecto_final.domine.entities.User;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class AssignmentMapperTest {

    private final AssignmentMapper mapper = Mappers.getMapper(AssignmentMapper.class);

    @Test
    void toEntity_shouldMapCreateRequest() {
        // given
        var request = new AssignmentCreateRequest(1L, 2L, 3L, true);

        // when
        Assignment entity = (Assignment) mapper.toEntity(request);

        // then
        assertThat(entity).isNotNull();
        assertThat(entity.getTrip()).isEqualTo(1L);
        assertThat(entity.getDriver()).isEqualTo(2L);
        assertThat(entity.getDispatcher()).isEqualTo(3L);
        assertThat(entity.getChecklistOk()).isEqualTo("true");
        assertThat(entity.getAssignedDate()).isNotNull();
        assertThat(entity.getAssignedDate()).isBeforeOrEqualTo(LocalDateTime.from(LocalDateTime.now()));
    }

    @Test
    void toResponse_shouldMapEntity() {
        // given
        var trip = Trip.builder().id(1L).build();
        var driver = User.builder().id(2L).build();
        var dispatcher = User.builder().id(3L).build();
        var entity = Assignment.builder()
                .id(10L)
                .trip(trip)
                .driver(driver)
                .dispatcher(dispatcher)
                .checklistOk(true)
                .assignedDate(LocalDateTime.now())
                .build();

        // when
        AssignmentCreateRequest request = new AssignmentCreateRequest(
                1L, // tripId
                2L, // driverId
                3L, // dispatcherId
                true // checklistOk
        );

        Assignment mapperEntity = mapper.toEntity(request);
        AssignmentResponse dto = mapper.toResponse(mapperEntity);

        // then
        assertThat(dto).isNotNull();
        assertThat(dto.id()).isEqualTo(10L);
        assertThat(dto.tripId()).isEqualTo(1L);
        assertThat(dto.driverId()).isEqualTo(2L);
        assertThat(dto.dispatcherId()).isEqualTo(3L);
        assertThat(dto.checklistOk()).isTrue();
    }

    @Test
    void updateEntityFromDto_shouldUpdateNonNullFields() {
        var trip = Trip.builder().id(1L).build();
        var driver = User.builder().id(2L).build();
        var dispatcher = User.builder().id(3L).build();

        var entity = Assignment.builder()
                .id(10L)
                .trip(trip)
                .driver(driver)
                .dispatcher(dispatcher)
                .checklistOk(false)
                .assignedDate(LocalDateTime.now().minusDays(1))
                .build();

        var update = new AssignmentUpdateRequest(true);

        // when
        mapper.updateEntityFromDto(update, entity);

        // then
        assertThat(entity.getChecklistOk()).isEqualTo("true");
        assertThat(entity.getId()).isEqualTo(10L);
        assertThat(entity.getTrip()).isEqualTo(1L);
    }
}
