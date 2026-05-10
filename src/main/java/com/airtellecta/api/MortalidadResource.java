package com.airtellecta.api;

import com.airtellecta.dto.ApiResponse;
import com.airtellecta.dto.response.MortalidadDto;
import com.airtellecta.service.MortalidadService;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/api/mortalidad")
@Produces(MediaType.APPLICATION_JSON)
public class MortalidadResource {

    @Inject
    MortalidadService service;

    @GET
    public ApiResponse<MortalidadDto> get() {
        return ApiResponse.ok(service.obtenerMortalidad());
    }
}
