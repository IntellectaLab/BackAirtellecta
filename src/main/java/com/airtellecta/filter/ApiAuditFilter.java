package com.airtellecta.filter;

import com.airtellecta.repository.AuditRepository;
import io.quarkus.security.identity.SecurityIdentity;
import io.vertx.ext.web.RoutingContext;
import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.ext.Provider;

import java.util.Set;

/**
 * Filtro JAX-RS que intercepta TODAS las peticiones autenticadas a /api/*
 * y registra automáticamente una entrada en audit_log.
 *
 * <p>Principios de diseño:</p>
 * <ul>
 *   <li>Falla silenciosa: cualquier excepción en el filtro es capturada para
 *       no bloquear la petición real. El audit log es observabilidad, no negocio.</li>
 *   <li>Rutas excluidas: /api/ping y /q/* (health, métricas) no se auditan.</li>
 *   <li>Sólo usuarios autenticados: peticiones anónimas (sin token Firebase) se omiten.</li>
 *   <li>Prioridad USER+100: corre después del mecanismo de autenticación Firebase,
 *       garantizando que SecurityIdentity esté poblada.</li>
 * </ul>
 *
 * <p>Requiere {@code quarkus.http.auth.proactive=true} (ya configurado en
 * application.properties) para que la identidad esté disponible en este filtro.</p>
 */
@Provider
@Priority(Priorities.USER + 100)
public class ApiAuditFilter implements ContainerRequestFilter {

    private static final Set<String> RUTAS_EXCLUIDAS = Set.of(
        "/api/ping",
        "/q/"
    );

    @Inject
    SecurityIdentity identity;

    @Inject
    RoutingContext routingContext;

    @Inject
    AuditRepository auditRepository;

    @Override
    public void filter(ContainerRequestContext ctx) {
        try {
            String path = ctx.getUriInfo().getPath();

            if (debeOmitir(path) || identity.isAnonymous()) {
                return;
            }

            String firebaseUid = identity.getPrincipal().getName();
            String method      = ctx.getMethod();
            String ip          = routingContext.request().remoteAddress().host();
            String entidad     = resolverEntidad(path);
            String accion      = resolverAccion(method, path, entidad);
            String detalle     = buildDetalle(method, path);

            Integer usuarioId  = auditRepository.resolverUsuarioId(firebaseUid);
            auditRepository.registrar(usuarioId, accion, entidad, null, detalle, ip);

        } catch (Exception e) {
            // Audit failure never blocks the API request.
            // In production, consider forwarding to a dead-letter queue.
        }
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private boolean debeOmitir(String path) {
        return RUTAS_EXCLUIDAS.stream().anyMatch(path::startsWith);
    }

    /**
     * Mapea HTTP method + path a un código de acción legible por auditores.
     * Las acciones de alto valor (simulación, exportar) tienen nombres explícitos.
     */
    private String resolverAccion(String method, String path, String entidad) {
        if (path.startsWith("/api/export"))          return "EXPORTAR_DATOS";
        if (path.contains("/api/simulacion"))        return "POST".equals(method)
                                                         ? "EJECUTAR_SIMULACION"
                                                         : "CONSULTAR_SIMULACION";
        if (path.startsWith("/api/me"))              return "CONSULTAR_PERFIL";
        if (path.contains("/admin/usuarios") && "GET".equals(method)) return "LISTAR_USUARIOS";

        String base = entidad.toUpperCase().replace("-", "_");
        return switch (method) {
            case "GET"    -> "CONSULTAR_" + base;
            case "POST"   -> "CREAR_"     + base;
            case "PUT"    -> "ACTUALIZAR_"+ base;
            case "DELETE" -> "ELIMINAR_"  + base;
            default       -> method       + "_" + base;
        };
    }

    /**
     * Extrae el nombre de entidad del path.
     * /api/panel-ejecutivo → panel_ejecutivo
     * /api/admin/usuarios/42 → usuarios
     */
    private String resolverEntidad(String path) {
        String[] partes = path.split("/");
        for (int i = partes.length - 1; i >= 0; i--) {
            String parte = partes[i];
            if (parte.isEmpty() || "api".equals(parte) || "admin".equals(parte)) continue;
            // Omitir segmentos numéricos (IDs de recursos)
            try {
                Integer.parseInt(parte);
            } catch (NumberFormatException e) {
                return parte.replace("-", "_");
            }
        }
        return "sistema";
    }

    private String buildDetalle(String method, String path) {
        return "{\"path\":\"" + path + "\",\"method\":\"" + method + "\"}";
    }
}
