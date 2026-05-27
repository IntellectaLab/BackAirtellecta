package com.airtellecta.api;

import com.airtellecta.dto.ApiResponse;
import com.airtellecta.dto.response.CostoCie10Dto;
import com.airtellecta.service.CostosService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.util.List;

@Path("/api/costos")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed({"ADMIN", "USER"})
public class CostosResource {

    @Inject
    CostosService service;

    @GET
    public ApiResponse<List<CostoCie10Dto>> obtenerCostos() {
        return ApiResponse.ok(service.obtenerCostos());
    }
}
