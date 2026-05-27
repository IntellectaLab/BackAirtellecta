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
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/api/mapa-estatal")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed({"ADMIN", "USER"})
@Tag(name = "Mapa Estatal", description = "Prevalencia de tabaquismo por entidad federativa")
@SecurityRequirement(name = "bearerAuth")
public class MapaEstatalResource {

    @Inject
    MapaEstatalService service;

    @GET
    @Operation(
        summary = "Obtener prevalencia por estado",
        description = "Retorna la prevalencia de tabaquismo, fumadores estimados y tasa por cada 100,000 habitantes " +
                      "para las 32 entidades federativas de México. Se puede filtrar por sexo."
    )
    @APIResponses({
        @APIResponse(responseCode = "200", description = "Datos de prevalencia estatal obtenidos correctamente",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = EntidadPrevalenciaDto.class))),
        @APIResponse(responseCode = "400", description = "Valor de sexo inválido"),
        @APIResponse(responseCode = "401", description = "Token JWT ausente o inválido"),
        @APIResponse(responseCode = "403", description = "El usuario autenticado no tiene rol ADMIN o USER"),
        @APIResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ApiResponse<List<EntidadPrevalenciaDto>> get(
        @Parameter(
            description = "Filtro por sexo: 1 = Hombre, 2 = Mujer. Omitir para ambos sexos.",
            required = false,
            schema = @Schema(enumeration = {"1", "2"})
        )
        @QueryParam("sexo") Byte sexo) {
        return ApiResponse.ok(service.obtenerMapa(sexo));
    }
}
