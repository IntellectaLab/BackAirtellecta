package com.airtellecta.api;

import com.airtellecta.dto.ApiResponse;
import com.airtellecta.dto.response.TendenciasDto;
import com.airtellecta.service.TendenciasService;
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

@Path("/api/tendencias")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed({"ADMIN", "USER"})
@Tag(name = "Tendencias", description = "Comparativo de indicadores de tabaquismo entre 2016 y 2025")
@SecurityRequirement(name = "bearerAuth")
public class TendenciasResource {

    @Inject
    TendenciasService service;

    @GET
    @Operation(
        summary = "Obtener comparativo de tendencias",
        description = "Retorna la comparación de los indicadores clave de tabaquismo entre la ENCODAT 2016 y " +
                      "la ENCODAT 2025: prevalencia, número de fumadores, usuarios de vapeo y uso dual."
    )
    @APIResponses({
        @APIResponse(responseCode = "200", description = "Datos de tendencias obtenidos correctamente",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = TendenciasDto.class))),
        @APIResponse(responseCode = "401", description = "Token JWT ausente o inválido"),
        @APIResponse(responseCode = "403", description = "El usuario autenticado no tiene rol ADMIN o USER"),
        @APIResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ApiResponse<TendenciasDto> obtenerComparativo() {
        return ApiResponse.ok(service.obtenerComparativo());
    }
}
