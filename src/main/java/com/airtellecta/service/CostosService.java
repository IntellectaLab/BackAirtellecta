package com.airtellecta.service;

import com.airtellecta.dto.response.CostoCie10Dto;
import com.airtellecta.repository.CostosRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;

@ApplicationScoped
public class CostosService {

    @Inject
    CostosRepository repository;

    public List<CostoCie10Dto> obtenerCostos() {
        return repository.obtenerCostos();
    }
}
