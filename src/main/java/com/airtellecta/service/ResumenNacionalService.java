package com.airtellecta.service;

import com.airtellecta.dto.response.ResumenNacionalDto;
import com.airtellecta.repository.ResumenNacionalRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class ResumenNacionalService {

    @Inject
    ResumenNacionalRepository repository;

    public ResumenNacionalDto obtenerResumen() {
        return repository.obtenerResumen();
    }
}
