package com.airtellecta.api;

import com.airtellecta.dto.ApiResponse;
import com.airtellecta.dto.SimulacionRequestDto;
import com.airtellecta.dto.response.SimulacionResultadoDto;
import com.airtellecta.service.SimulacionService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/api/simulacion")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed({"ADMIN", "USER"})
@Tag(name = "Simulación", description = "Motor de simulación de impacto de políticas de control de tabaco")
@SecurityRequirement(name = "bearerAuth")
public class SimulacionResource {

    @Inject
    SimulacionService service;

    @POST
    @Operation(
        summary = "Ejecutar simulación de políticas",
        description = "Ejecuta una proyección multianual del impacto sobre prevalencia, fumadores, defunciones " +
                      "evitadas y ahorro en costos de salud, dado un conjunto de políticas de control de tabaco " +
                      "y/o un incremento fiscal (IEPS). El horizonte temporal es configurable."
    )
    @APIResponses({
        @APIResponse(responseCode = "200", description = "Simulación ejecutada correctamente",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = SimulacionResultadoDto.class))),
        @APIResponse(responseCode = "400", description = "Parámetros de simulación inválidos"),
        @APIResponse(responseCode = "401", description = "Token JWT ausente o inválido"),
        @APIResponse(responseCode = "403", description = "El usuario autenticado no tiene rol ADMIN o USER"),
        @APIResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ApiResponse<SimulacionResultadoDto> simular(
        @RequestBody(description = "Parámetros de la simulación", required = true,
            content = @Content(schema = @Schema(implementation = SimulacionRequestDto.class)))
        SimulacionRequestDto request) {
        return ApiResponse.ok(service.simular(request));
    }
}
