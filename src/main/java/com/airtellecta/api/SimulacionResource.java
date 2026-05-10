package com.airtellecta.api;

import com.airtellecta.dto.ApiResponse;
import com.airtellecta.dto.SimulacionRequestDto;
import com.airtellecta.dto.response.SimulacionResultadoDto;
import com.airtellecta.service.SimulacionService;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/api/simulacion")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SimulacionResource {

    @Inject
    SimulacionService service;

    @POST
    public ApiResponse<SimulacionResultadoDto> simular(SimulacionRequestDto request) {
        return ApiResponse.ok(service.simular(request));
    }
}
