package com.airtellecta.api;

import com.airtellecta.dto.ApiResponse;
import com.airtellecta.dto.response.UsuarioDto;
import com.airtellecta.repository.UsuarioRepository;
import com.airtellecta.service.UsuarioService;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

@Path("/api/me")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed({"ADMIN", "USER"})
public class MeResource {

    @Inject
    UsuarioService usuarioService;

    @Inject
    UsuarioRepository usuarioRepository;

    @Inject
    SecurityIdentity identity;

    @GET
    public ApiResponse<UsuarioDto> me() {
        String uid = identity.getPrincipal().getName();

        // Update ultimo_acceso
        usuarioService.registrarAcceso(uid);

        // Return current user profile
        var usuario = usuarioRepository.buscarPorFirebaseUid(uid);
        if (usuario == null) {
            return ApiResponse.ok(null);
        }
        return ApiResponse.ok(usuarioRepository.buscarPorId(usuario.id));
    }
}
