package com.airtellecta.api;

import com.airtellecta.dto.ApiResponse;
import com.airtellecta.dto.response.EntidadPrevalenciaDto;
import com.airtellecta.service.MapaEstatalService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import java.util.List;

@Path("/api/mapa-estatal")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed({"ADMIN", "USER"})
public class MapaEstatalResource {

    @Inject
    MapaEstatalService service;

    @GET
    public ApiResponse<List<EntidadPrevalenciaDto>> get(@QueryParam("sexo") Byte sexo) {
        return ApiResponse.ok(service.obtenerMapa(sexo));
    }
}
