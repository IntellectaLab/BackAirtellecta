package com.airtellecta.api;

import com.airtellecta.dto.ApiResponse;
import com.airtellecta.dto.response.ResumenNacionalDto;
import com.airtellecta.service.ResumenNacionalService;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/api/resumen-nacional")
@Produces(MediaType.APPLICATION_JSON)
public class ResumenNacionalResource {

    @Inject
    ResumenNacionalService service;

    @GET
    public ApiResponse<ResumenNacionalDto> get() {
        return ApiResponse.ok(service.obtenerResumen());
    }
}
