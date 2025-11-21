
package com.unimag.edu.proyecto_final.service;

import com.unimag.edu.proyecto_final.api.dto.ConfiDtos;
import com.unimag.edu.proyecto_final.domine.entities.Confi;
import com.unimag.edu.proyecto_final.domine.repository.ConfiRepository;
import com.unimag.edu.proyecto_final.exception.NotFoundException;
import com.unimag.edu.proyecto_final.service.mappers.ConfiMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ConfiServicelmpl implements ConfiService {

    private final ConfiRepository confiRepository;
    private final ConfiMapper confiMapper;

    @Override
    public ConfiDtos.ConfigResponse create(ConfiDtos.ConfigCreateRequest request) {
        if(confiRepository.existsByKey(request.key())){
            throw new NotFoundException("config already exists");
        }
        Confi confi = confiMapper.toEntity(request);
        confiRepository.save(confi);
        return confiMapper.toResponse(confi);
    }

    @Override
    @Transactional(readOnly = true)
    public ConfiDtos.ConfigResponse get(String key) {
        Confi confi = confiRepository.findByKey(key)
                .orElseThrow(() -> new NotFoundException("config not found"));
        return confiMapper.toResponse(confi);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ConfiDtos.ConfigResponse> list(Pageable pageable) {
        return confiRepository.findAll(pageable)
                .map(confiMapper::toResponse);
    }

    @Override
    public ConfiDtos.ConfigResponse update(String key, ConfiDtos.ConfigUpdateRequest request) {
        Confi confi = confiRepository.findByKey(key)
                .orElseThrow(() -> new NotFoundException("config not found"));
        confiMapper.updateEntityFromDto(request,confi);
        confiRepository.save(confi);

        return confiMapper.toResponse(confi);
    }

    @Override
    public void delete(String key) {
        Confi confi = confiRepository.findByKey(key)
                .orElseThrow(() -> new NotFoundException("config not found"));
        confiRepository.delete(confi);
    }
    @Override
    public String getValue(String key) {
        return confiRepository.findByKey(key)
                .orElseThrow(() -> new NotFoundException("config not found"))
                .getValue();
    }

    @Override
    public double getDouble(String key) {
        return Double.parseDouble(getValue(key));
    }

    @Override
    public int getInt(String key) {
        return Integer.parseInt(getValue(key));
    }

    @Override
    public boolean getBoolean(String key) {
        return Boolean.parseBoolean(getValue(key));
    }

    @Override
    @Transactional
    public void setValue(String key, Object value) {
        Confi confi = confiRepository.findByKey(key).orElse(null);

        if (confi == null) {
            confi = Confi.builder()
                    .key(key)
                    .value(value.toString())
                    .build();
        } else {
            confi.setValue(value.toString());
        }

        confiRepository.save(confi);
    }
}
