package com.airtellecta.service;

import com.airtellecta.dto.response.MortalidadDto;
import com.airtellecta.repository.MortalidadRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class MortalidadService {

    @Inject
    MortalidadRepository repository;

    public MortalidadDto obtenerMortalidad() {
        MortalidadDto dto = new MortalidadDto();
        dto.series = repository.obtenerSeriesAnuales();
        dto.desgloseCie10 = repository.obtenerDesgloseCie10();
        return dto;
    }
}
