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

@Path("/api/export")
@RolesAllowed({"ADMIN", "USER"})
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
    public Response exportSimulacionExcel(SimulacionRequestDto request) throws IOException {
        SimulacionResultadoDto resultado = simulacionService.simular(request);
        byte[] bytes = excelExportService.generarSimulacionExcel(resultado);
        return Response.ok(bytes)
                .header("Content-Disposition", "attachment; filename=\"simulacion-airtellecta.xlsx\"")
                .build();
    }

    @GET
    @Path("/panel-ejecutivo/excel")
    @Produces("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public Response exportPanelEjecutivoExcel() throws IOException {
        PanelEjecutivoDto panel = panelEjecutivoService.obtenerPanel();
        byte[] bytes = excelExportService.generarPanelEjecutivoExcel(panel);
        return Response.ok(bytes)
                .header("Content-Disposition", "attachment; filename=\"panel-ejecutivo-airtellecta.xlsx\"")
                .build();
    }
}
