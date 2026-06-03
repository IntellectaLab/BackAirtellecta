package com.airtellecta.repository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.transaction.Transactional;

/**
 * Repositorio dedicado para escritura y consulta del audit_log.
 *
 * <p>Separado de UsuarioRepository para evitar acoplamiento: cualquier
 * componente (filtros, servicios, triggers de compensación) puede inyectar
 * AuditRepository sin depender del dominio de usuarios.</p>
 *
 * <p>Diseño append-only: no hay métodos de UPDATE ni DELETE (la depuración
 * es responsabilidad del Event Scheduler / sp_depurar_audit_log).</p>
 */
@ApplicationScoped
public class AuditRepository {

    @Inject
    EntityManager em;

    /**
     * Inserta un registro en audit_log.
     *
     * @param usuarioId        ID interno del usuario (puede ser null si la acción es anónima)
     * @param accion           Código de acción (e.g. "CONSULTAR_SIMULACION")
     * @param entidadAfectada  Nombre lógico de la entidad (e.g. "simulacion", "usuarios")
     * @param registroId       ID del registro afectado, como string (puede ser null)
     * @param detalleJson      JSON con contexto adicional (path, método, params relevantes)
     * @param ip               Dirección IP del solicitante
     */
    @Transactional
    public void registrar(Integer usuarioId, String accion, String entidadAfectada,
                          String registroId, String detalleJson, String ip) {
        em.createNativeQuery("""
                INSERT INTO audit_log
                    (usuario_id, accion, entidad_afectada, registro_id, detalle, ip_address, created_at)
                VALUES
                    (:uid, :accion, :entidad, :regId, CAST(:detalle AS JSON), :ip, NOW())
                """)
            .setParameter("uid",     usuarioId)
            .setParameter("accion",  accion)
            .setParameter("entidad", entidadAfectada)
            .setParameter("regId",   registroId)
            .setParameter("detalle", detalleJson)
            .setParameter("ip",      ip)
            .executeUpdate();
    }

    /**
     * Resuelve el ID interno de un usuario a partir de su Firebase UID.
     * Usado por el filtro JAX-RS para asociar cada request a su usuario_id.
     *
     * @return ID interno, o null si no existe el usuario en la base.
     */
    public Integer resolverUsuarioId(String firebaseUid) {
        try {
            Object result = em.createNativeQuery(
                    "SELECT id FROM usuarios WHERE firebase_uid = :uid LIMIT 1")
                .setParameter("uid", firebaseUid)
                .getSingleResult();
            return result != null ? ((Number) result).intValue() : null;
        } catch (NoResultException e) {
            return null;
        }
    }
}
