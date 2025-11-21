package com.unimag.edu.proyecto_final.service.mappers;

import com.unimag.edu.proyecto_final.api.dto.ParcelDtos.*;
import com.unimag.edu.proyecto_final.domine.entities.Parcel;
import com.unimag.edu.proyecto_final.domine.entities.enumera.StatusParcel;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ParcelMapper {


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "statusParcel", constant = "CREATED")
    @Mapping(target = "fromStop", ignore = true)
    @Mapping(target = "toStop", ignore = true)
    Parcel toEntity(ParcelCreateRequest request);

    @Mapping(target = "status", expression = "java(parcel.getStatusParcel() != null ? parcel.getStatusParcel().name() : null)")
    @Mapping(source = "fromStop.id", target = "fromStopId")
    @Mapping(source = "toStop.id", target = "toStopId")
    ParcelResponse toResponse(Parcel parcel);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "statusParcel", expression = "java(request.status() != null ? mapStatus(request.status()) : parcel.getStatusParcel())")
    void updateEntityFromStatusRequest(ParcelUpdateRequest request, @MappingTarget Parcel parcel);

    default StatusParcel mapStatus(String value) {
        if (value == null) return null;
        try {
            return StatusParcel.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException ex) {
            return StatusParcel.CREATED;
        }
    }
}
