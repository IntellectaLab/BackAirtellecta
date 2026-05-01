package com.airtellecta.api;

import com.airtellecta.dto.ApiResponse;
import com.airtellecta.dto.response.TendenciasDto;
import com.airtellecta.service.TendenciasService;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/api/tendencias")
@Produces(MediaType.APPLICATION_JSON)
public class TendenciasResource {

    @Inject
    TendenciasService service;

    @GET
    public ApiResponse<TendenciasDto> obtenerComparativo() {
        return ApiResponse.ok(service.obtenerComparativo());
    }
}
