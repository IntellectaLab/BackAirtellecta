package com.airtellecta.service;

import com.airtellecta.dto.response.EntidadPrevalenciaDto;
import com.airtellecta.repository.MapaEstatalRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class MapaEstatalService {

    @Inject
    MapaEstatalRepository repository;

    public List<EntidadPrevalenciaDto> obtenerMapa(Byte sexo) {
        return repository.obtenerPrevalenciaPorEntidad(Optional.ofNullable(sexo));
    }
}
