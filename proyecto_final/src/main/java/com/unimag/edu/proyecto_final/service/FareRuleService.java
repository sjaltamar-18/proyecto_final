package com.unimag.edu.proyecto_final.service;

import com.unimag.edu.proyecto_final.api.dto.FareRuleDtos.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FareRuleService {

    FareRuleResponse create(FareRuleCreateRequest request);

    FareRuleResponse get(Long id);

    Page<FareRuleResponse> listByRoute(Long routeId, Pageable pageable);

    FareRuleResponse update(Long id, FareRuleUpdateRequest request);

    void delete(Long id);

}
