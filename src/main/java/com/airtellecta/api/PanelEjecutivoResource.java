package com.airtellecta.api;

import com.airtellecta.dto.ApiResponse;
import com.airtellecta.dto.response.PanelEjecutivoDto;
import com.airtellecta.service.PanelEjecutivoService;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/api/panel-ejecutivo")
@Produces(MediaType.APPLICATION_JSON)
public class PanelEjecutivoResource {

    @Inject
    PanelEjecutivoService service;

    @GET
    public ApiResponse<PanelEjecutivoDto> obtenerPanel() {
        return ApiResponse.ok(service.obtenerPanel());
    }
}
