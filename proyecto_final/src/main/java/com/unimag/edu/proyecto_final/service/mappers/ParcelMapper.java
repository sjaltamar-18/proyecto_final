package com.unimag.edu.proyecto_final.service.mappers;

import com.unimag.edu.proyecto_final.api.dto.ParcelDtos.*;
import com.unimag.edu.proyecto_final.domine.entities.Parcel;
import com.unimag.edu.proyecto_final.domine.entities.enumera.StatusParcel;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ParcelMapper {


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", constant = "CREATED")
    @Mapping(target = "proofPhotoUrl", ignore = true)
    @Mapping(target = "deliveryOtp", ignore = true)
    Parcel toEntity(ParcelCreateRequest request);


    @Mapping(target = "status", expression = "java(parcel.getStatus() != null ? parcel.getStatus().name() : null)")
    ParcelResponse toResponse(Parcel parcel);

    // Actualización de estado
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "status", expression = "java(request.status() != null ? mapStatus(request.status()) : parcel.getStatus())")
    void updateEntityFromStatusRequest(ParcelUpdateRequest request, @MappingTarget Parcel parcel);

    default StatusParcel mapStatus(String value) {
        if (value == null) return null;
        try {
            return StatusParcel.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException ex) {
            return StatusParcel.CREATED; // Valor por defecto
        }
    }
}
