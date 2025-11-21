
package com.unimag.edu.proyecto_final.service;

import com.unimag.edu.proyecto_final.api.dto.FareRuleDtos;
import com.unimag.edu.proyecto_final.domine.entities.FareRule;
import com.unimag.edu.proyecto_final.domine.entities.Route;
import com.unimag.edu.proyecto_final.domine.entities.Stop;
import com.unimag.edu.proyecto_final.domine.repository.FareRuleRepository;
import com.unimag.edu.proyecto_final.domine.repository.RouteRepository;
import com.unimag.edu.proyecto_final.domine.repository.StopRepository;
import com.unimag.edu.proyecto_final.exception.NotFoundException;
import com.unimag.edu.proyecto_final.service.mappers.FareRuleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class FareRuleServicelmpl  implements FareRuleService{

    private final FareRuleRepository fareRuleRepository;
    private final RouteRepository routeRepository;
    private final StopRepository stopRepository;
    private final FareRuleMapper fareRuleMapper;

    @Override
    public FareRuleDtos.FareRuleResponse create(FareRuleDtos.FareRuleCreateRequest request) {

        Route route = routeRepository.findById(request.routeId())
                .orElseThrow(() -> new NotFoundException("Route not found"));

        Stop toStop = stopRepository.findById(request.toStopId())
                .orElseThrow(() -> new NotFoundException("Stop not found"));

        Stop fromStop = stopRepository.findById(request.fromStopId())
                .orElseThrow(() -> new NotFoundException("Stop not found"));

        FareRule fareRule = fareRuleMapper.toEntity(request);
        fareRule.setRoute(route);
        fareRule.setToStop(toStop);
        fareRule.setFromStop(fromStop);

        FareRule result = fareRuleRepository.save(fareRule);
        return fareRuleMapper.toResponse(result);
    }

    @Override
    @Transactional(readOnly = true)
    public FareRuleDtos.FareRuleResponse get(Long id) {
        FareRule fareRule = fareRuleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("FareRule not found"));
        return fareRuleMapper.toResponse(fareRule);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FareRuleDtos.FareRuleResponse> listByRoute(Long routeId, Pageable pageable) {
        List<FareRule> fareRules = fareRuleRepository.findByRoute_Id(routeId);
        List<FareRuleDtos.FareRuleResponse> responses = fareRules.stream()
                .map(fareRuleMapper::toResponse)
                .toList();

        return new PageImpl<>(responses, pageable, responses.size());
    }

    @Override
    public FareRuleDtos.FareRuleResponse update(Long id, FareRuleDtos.FareRuleUpdateRequest request) {
        FareRule fareRule = fareRuleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("FareRule not found"));

        fareRuleMapper.updateEntityFromDto(request,fareRule);
        FareRule result = fareRuleRepository.save(fareRule);
        return fareRuleMapper.toResponse(result);
    }

    @Override
    public void delete(Long id) {

        FareRule fareRule = fareRuleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("FareRule not found"));

        fareRuleRepository.delete(fareRule);

    }
}
