package com.unimag.edu.proyecto_final.service;

import com.unimag.edu.proyecto_final.api.dto.FareRuleDtos;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class FareRuleServicelmpl  implements FareRuleService{
    @Override
    public FareRuleDtos.FareRuleResponse create(FareRuleDtos.FareRuleCreateRequest request) {
        return null;
    }

    @Override
    public FareRuleDtos.FareRuleResponse get(Long id) {
        return null;
    }

    @Override
    public Page<FareRuleDtos.FareRuleResponse> listByRoute(Long routeId, Pageable pageable) {
        return null;
    }

    @Override
    public FareRuleDtos.FareRuleResponse update(Long id, FareRuleDtos.FareRuleUpdateRequest request) {
        return null;
    }

    @Override
    public void delete(Long id) {

    }
}
