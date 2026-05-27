package com.airtellecta.api;

import com.airtellecta.dto.ApiResponse;
import com.airtellecta.dto.response.RecaudacionAnualDto;
import com.airtellecta.service.RecaudacionService;
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

@Path("/api/recaudacion")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed({"ADMIN", "USER"})
@Tag(name = "Recaudación", description = "Serie histórica de recaudación IEPS por tabaco")
@SecurityRequirement(name = "bearerAuth")
public class RecaudacionResource {

    @Inject
    RecaudacionService service;

    @GET
    @Operation(
        summary = "Obtener serie de recaudación IEPS",
        description = "Retorna la serie histórica anual de recaudación del Impuesto Especial sobre Producción y " +
                      "Servicios (IEPS) aplicado a productos de tabaco, expresada en millones de pesos."
    )
    @APIResponses({
        @APIResponse(responseCode = "200", description = "Serie de recaudación obtenida correctamente",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = RecaudacionAnualDto.class))),
        @APIResponse(responseCode = "401", description = "Token JWT ausente o inválido"),
        @APIResponse(responseCode = "403", description = "El usuario autenticado no tiene rol ADMIN o USER"),
        @APIResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ApiResponse<List<RecaudacionAnualDto>> obtenerSerie() {
        return ApiResponse.ok(service.obtenerSerie());
    }
}
