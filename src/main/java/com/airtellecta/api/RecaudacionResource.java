package com.airtellecta.api;

import com.airtellecta.dto.ApiResponse;
import com.airtellecta.dto.response.RecaudacionAnualDto;
import com.airtellecta.service.RecaudacionService;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.util.List;

@Path("/api/recaudacion")
@Produces(MediaType.APPLICATION_JSON)
public class RecaudacionResource {

    @Inject
    RecaudacionService service;

    @GET
    public ApiResponse<List<RecaudacionAnualDto>> obtenerSerie() {
        return ApiResponse.ok(service.obtenerSerie());
    }
}
