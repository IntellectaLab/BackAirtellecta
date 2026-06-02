package com.airtellecta.api;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/api")
@Tag(name = "Health", description = "Verificación de estado del servicio")
public class HealthResource {

    @GET
    @Path("/ping")
    @Produces(MediaType.APPLICATION_JSON)
    @PermitAll
    @Operation(summary = "Health check", description = "Verifica que el servicio esté en línea. No requiere autenticación.")
    @APIResponse(
        responseCode = "200",
        description = "Servicio operativo",
        content = @Content(mediaType = "application/json",
            schema = @Schema(example = "{\"status\":\"ok\",\"app\":\"AirTellecta\",\"version\":\"1.0.0-SNAPSHOT\"}"))
    )
    public String ping() {
        return "{\"status\":\"ok\",\"app\":\"AirTellecta\",\"version\":\"1.0.0-SNAPSHOT\"}";
    }
}
