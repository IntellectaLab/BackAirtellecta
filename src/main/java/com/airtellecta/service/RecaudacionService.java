package com.airtellecta.service;

import com.airtellecta.dto.response.RecaudacionAnualDto;
import com.airtellecta.repository.RecaudacionRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;

@ApplicationScoped
public class RecaudacionService {

    @Inject
    RecaudacionRepository repository;

    public List<RecaudacionAnualDto> obtenerSerie() {
        return repository.obtenerSerie();
    }
}
