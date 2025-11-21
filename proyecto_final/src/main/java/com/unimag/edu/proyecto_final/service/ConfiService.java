package com.unimag.edu.proyecto_final.service;

import com.unimag.edu.proyecto_final.api.dto.ConfiDtos.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ConfiService {

    ConfigResponse create(ConfigCreateRequest request);

    ConfigResponse get(String key);

    Page<ConfigResponse> list(Pageable pageable);

    ConfigResponse update(String key, ConfigUpdateRequest request);

    String getValue(String key);

    double getDouble(String key);

    int getInt(String key);

    boolean getBoolean(String key);

    void setValue(String key, Object value);

    void delete(String key);
}
