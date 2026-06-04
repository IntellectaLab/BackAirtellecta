package com.airtellecta.api;

import com.airtellecta.dto.ApiResponse;
import com.airtellecta.dto.response.AuditLogPageDto;
import com.airtellecta.service.AuditLogService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

@Path("/api/admin/audit-log")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed("ADMIN")
public class AuditLogResource {

    @Inject
    AuditLogService auditLogService;

    @GET
    public ApiResponse<AuditLogPageDto> listar(
            @QueryParam("page")           @DefaultValue("0")  int     page,
            @QueryParam("size")           @DefaultValue("50") int     size,
            @QueryParam("accion")                             String  accion,
            @QueryParam("usuarioId")                          Integer usuarioId,
            @QueryParam("fechaInicio")                        String  fechaInicio,
            @QueryParam("fechaFin")                           String  fechaFin,
            @QueryParam("emailBusqueda")                      String  emailBusqueda) {

        AuditLogPageDto resultado = auditLogService.obtenerPagina(
                page, size, accion, usuarioId, fechaInicio, fechaFin, emailBusqueda);

        return ApiResponse.ok(resultado);
    }
}
