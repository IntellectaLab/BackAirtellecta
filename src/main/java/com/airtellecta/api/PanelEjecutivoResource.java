package com.airtellecta.api;

import com.airtellecta.dto.ApiResponse;
import com.airtellecta.dto.response.PanelEjecutivoDto;
import com.airtellecta.service.PanelEjecutivoService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/api/panel-ejecutivo")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed({"ADMIN", "USER"})
@Tag(name = "Panel Ejecutivo", description = "Resumen ejecutivo con carga económica, recaudación y epidemiología")
@SecurityRequirement(name = "bearerAuth")
public class PanelEjecutivoResource {

    @Inject
    PanelEjecutivoService service;

    @GET
    @Operation(
        summary = "Obtener panel ejecutivo",
        description = "Retorna un resumen ejecutivo consolidado que incluye carga económica directa e indirecta, " +
                      "datos de recaudación IEPS más reciente, indicadores epidemiológicos y costos por patología."
    )
    @APIResponses({
        @APIResponse(responseCode = "200", description = "Panel ejecutivo obtenido correctamente",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = PanelEjecutivoDto.class))),
        @APIResponse(responseCode = "401", description = "Token JWT ausente o inválido"),
        @APIResponse(responseCode = "403", description = "El usuario autenticado no tiene rol ADMIN o USER"),
        @APIResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ApiResponse<PanelEjecutivoDto> obtenerPanel() {
        return ApiResponse.ok(service.obtenerPanel());
    }
}
