package com.airtellecta.service;

import com.airtellecta.dto.response.TendenciasDto;
import com.airtellecta.repository.TendenciasRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class TendenciasService {

    @Inject
    TendenciasRepository repository;

    public TendenciasDto obtenerComparativo() {
        return repository.obtenerComparativo();
    }
}
