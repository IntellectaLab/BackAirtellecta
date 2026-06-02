package com.airtellecta.api;

import com.airtellecta.dto.ApiResponse;
import com.airtellecta.dto.response.MortalidadDto;
import com.airtellecta.service.MortalidadService;
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

@Path("/api/mortalidad")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed({"ADMIN", "USER"})
@Tag(name = "Mortalidad", description = "Datos de mortalidad y urgencias atribuibles al tabaquismo")
@SecurityRequirement(name = "bearerAuth")
public class MortalidadResource {

    @Inject
    MortalidadService service;

    @GET
    @Operation(
        summary = "Obtener datos de mortalidad",
        description = "Retorna la serie histórica anual de defunciones y urgencias atribuibles al tabaquismo (F17), " +
                      "junto con el desglose por código CIE-10."
    )
    @APIResponses({
        @APIResponse(responseCode = "200", description = "Datos de mortalidad obtenidos correctamente",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = MortalidadDto.class))),
        @APIResponse(responseCode = "401", description = "Token JWT ausente o inválido"),
        @APIResponse(responseCode = "403", description = "El usuario autenticado no tiene rol ADMIN o USER"),
        @APIResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ApiResponse<MortalidadDto> get() {
        return ApiResponse.ok(service.obtenerMortalidad());
    }
}
