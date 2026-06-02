package com.airtellecta.api;

import com.airtellecta.dto.ApiResponse;
import com.airtellecta.dto.response.ResumenNacionalDto;
import com.airtellecta.service.ResumenNacionalService;
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

@Path("/api/resumen-nacional")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed({"ADMIN", "USER"})
@Tag(name = "Resumen Nacional", description = "Indicadores nacionales de tabaquismo en México")
@SecurityRequirement(name = "bearerAuth")
public class ResumenNacionalResource {

    @Inject
    ResumenNacionalService service;

    @GET
    @Operation(
        summary = "Obtener resumen nacional",
        description = "Retorna los principales indicadores epidemiológicos nacionales: prevalencia, población fumadora, " +
                      "usuarios de vapeo, uso dual, defunciones y urgencias atribuibles al tabaquismo (F17)."
    )
    @APIResponses({
        @APIResponse(responseCode = "200", description = "Resumen nacional obtenido correctamente",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ResumenNacionalDto.class))),
        @APIResponse(responseCode = "401", description = "Token JWT ausente o inválido"),
        @APIResponse(responseCode = "403", description = "El usuario autenticado no tiene rol ADMIN o USER"),
        @APIResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ApiResponse<ResumenNacionalDto> get() {
        return ApiResponse.ok(service.obtenerResumen());
    }
}
