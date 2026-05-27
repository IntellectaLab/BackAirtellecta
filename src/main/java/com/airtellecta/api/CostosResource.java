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
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/api/costos")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed({"ADMIN", "USER"})
@Tag(name = "Costos", description = "Costos de atención por diagnóstico CIE-10 atribuibles al tabaquismo")
@SecurityRequirement(name = "bearerAuth")
public class CostosResource {

    @Inject
    CostosService service;

    @GET
    @Operation(
        summary = "Obtener costos por diagnóstico CIE-10",
        description = "Retorna el catálogo de costos de atención médica por patología relacionada con el tabaquismo, " +
                      "ajustados a pesos mexicanos de 2025."
    )
    @APIResponses({
        @APIResponse(responseCode = "200", description = "Lista de costos CIE-10 obtenida correctamente",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = CostoCie10Dto.class))),
        @APIResponse(responseCode = "401", description = "Token JWT ausente o inválido"),
        @APIResponse(responseCode = "403", description = "El usuario autenticado no tiene rol ADMIN o USER"),
        @APIResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ApiResponse<List<CostoCie10Dto>> obtenerCostos() {
        return ApiResponse.ok(service.obtenerCostos());
    }
}
