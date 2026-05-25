package com.airtellecta.api;

import com.airtellecta.dto.ApiResponse;
import com.airtellecta.dto.request.ActualizarUsuarioRequest;
import com.airtellecta.dto.request.CrearUsuarioRequest;
import com.airtellecta.dto.response.UsuarioDto;
import com.airtellecta.service.UsuarioService;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import io.vertx.ext.web.RoutingContext;
import java.util.List;

@Path("/api/admin/usuarios")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed("ADMIN")
public class UsuarioResource {

    @Inject
    UsuarioService service;

    @Inject
    SecurityIdentity identity;

    @Inject
    RoutingContext routingContext;

    @GET
    public ApiResponse<List<UsuarioDto>> listar() {
        return ApiResponse.ok(service.listarTodos());
    }

    @GET
    @Path("/{id}")
    public ApiResponse<UsuarioDto> obtener(@PathParam("id") int id) {
        return ApiResponse.ok(service.obtenerPorId(id));
    }

    @POST
    public ApiResponse<UsuarioDto> crear(CrearUsuarioRequest request) {
        String adminUid = identity.getPrincipal().getName();
        String ip = routingContext.request().remoteAddress().host();
        return ApiResponse.ok(service.crear(request, adminUid, ip));
    }

    @PUT
    @Path("/{id}")
    public ApiResponse<UsuarioDto> actualizar(@PathParam("id") int id, ActualizarUsuarioRequest request) {
        String adminUid = identity.getPrincipal().getName();
        String ip = routingContext.request().remoteAddress().host();
        return ApiResponse.ok(service.actualizar(id, request, adminUid, ip));
    }
}
