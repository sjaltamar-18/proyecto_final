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

        var request = new AssignmentCreateRequest(1L, 2L, 3L, true);


        Assignment entity = (Assignment) mapper.toEntity(request);

        assertThat(entity.getTrip()).isNotNull();
        assertThat(entity.getTrip().getId()).isEqualTo(1L);

        assertThat(entity.getDriver()).isNotNull();
        assertThat(entity.getDriver().getId()).isEqualTo(2L);

        assertThat(entity.getDispatcher()).isNotNull();
        assertThat(entity.getDispatcher().getId()).isEqualTo(3L);

        assertThat(entity.getChecklistOk()).isTrue();

        assertThat(entity.getAssignedDate()).isNotNull();
        assertThat(entity.getAssignedDate()).isBeforeOrEqualTo(LocalDateTime.now());
    }

    @Test
    void toResponse_shouldMapEntity() {

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


        AssignmentResponse dto = mapper.toResponse(entity);

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

        mapper.updateEntityFromDto(update, entity);


        assertThat(entity.getChecklistOk()).isTrue(); // debe actualizarse
        assertThat(entity.getId()).isEqualTo(10L); // no debe cambiar
        assertThat(entity.getTrip()).isNotNull(); // sigue existiendo
        assertThat(entity.getTrip().getId()).isEqualTo(1L); // relación intacta
        assertThat(entity.getDriver().getId()).isEqualTo(2L);
        assertThat(entity.getDispatcher().getId()).isEqualTo(3L);
    }

}