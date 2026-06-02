package com.airtellecta.api;

import com.airtellecta.dto.SimulacionRequestDto;
import com.airtellecta.dto.response.PanelEjecutivoDto;
import com.airtellecta.dto.response.SimulacionResultadoDto;
import com.airtellecta.service.ExcelExportService;
import com.airtellecta.service.PanelEjecutivoService;
import com.airtellecta.service.SimulacionService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.io.IOException;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/api/export")
@RolesAllowed({"ADMIN", "USER"})
@Tag(name = "Exportar", description = "Descarga de reportes en formato Excel (.xlsx)")
@SecurityRequirement(name = "bearerAuth")
public class ExportResource {

    @Inject
    ExcelExportService excelExportService;

    @Inject
    SimulacionService simulacionService;

    @Inject
    PanelEjecutivoService panelEjecutivoService;

    @POST
    @Path("/simulacion/excel")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    @Operation(summary = "Exportar simulación a Excel", description = "Ejecuta la simulación con los parámetros indicados y devuelve el resultado como archivo Excel (.xlsx) para descarga.")
    @APIResponses({
        @APIResponse(responseCode = "200", description = "Archivo Excel generado correctamente",
            content = @Content(mediaType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")),
        @APIResponse(responseCode = "400", description = "Parámetros de simulación inválidos"),
        @APIResponse(responseCode = "401", description = "Token JWT ausente o inválido"),
        @APIResponse(responseCode = "403", description = "El usuario no tiene rol ADMIN o USER"),
        @APIResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public Response exportSimulacionExcel(
        @RequestBody(description = "Parámetros de la simulación", required = true,
            content = @Content(schema = @Schema(implementation = SimulacionRequestDto.class)))
        SimulacionRequestDto request) throws IOException {
        SimulacionResultadoDto resultado = simulacionService.simular(request);
        byte[] bytes = excelExportService.generarSimulacionExcel(resultado);
        return Response.ok(bytes)
                .header("Content-Disposition", "attachment; filename=\"simulacion-airtellecta.xlsx\"")
                .build();
    }

    @GET
    @Path("/panel-ejecutivo/excel")
    @Produces("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    @Operation(summary = "Exportar panel ejecutivo a Excel", description = "Genera y descarga el panel ejecutivo consolidado (carga económica, recaudación, epidemiología) en formato Excel (.xlsx).")
    @APIResponses({
        @APIResponse(responseCode = "200", description = "Archivo Excel del panel ejecutivo generado correctamente",
            content = @Content(mediaType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")),
        @APIResponse(responseCode = "401", description = "Token JWT ausente o inválido"),
        @APIResponse(responseCode = "403", description = "El usuario no tiene rol ADMIN o USER"),
        @APIResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public Response exportPanelEjecutivoExcel() throws IOException {
        PanelEjecutivoDto panel = panelEjecutivoService.obtenerPanel();
        byte[] bytes = excelExportService.generarPanelEjecutivoExcel(panel);
        return Response.ok(bytes)
                .header("Content-Disposition", "attachment; filename=\"panel-ejecutivo-airtellecta.xlsx\"")
                .build();
    }
}
