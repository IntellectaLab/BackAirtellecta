package com.airtellecta.repository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.transaction.Transactional;
import com.airtellecta.dto.response.AuditLogItemDto;
import java.util.List;

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

    /**
     * Retorna una página de registros de audit_log con el email del usuario joined.
     * Todos los filtros son opcionales (null = sin filtro).
     */
    @SuppressWarnings("unchecked")
    public List<AuditLogItemDto> buscarPaginado(
            int page, int size,
            String accion,
            Integer usuarioId,
            String fechaInicio,
            String fechaFin,
            String emailBusqueda) {

        StringBuilder sql = new StringBuilder("""
            SELECT
                a.id,
                a.usuario_id,
                u.email          AS usuario_email,
                a.accion,
                a.entidad_afectada,
                a.registro_id,
                CAST(a.detalle AS CHAR) AS detalle,
                a.ip_address,
                DATE_FORMAT(a.created_at, '%Y-%m-%dT%H:%i:%S') AS created_at
            FROM audit_log a
            LEFT JOIN usuarios u ON a.usuario_id = u.id
            WHERE 1=1
            """);

        if (accion         != null) sql.append(" AND a.accion = :accion");
        if (usuarioId      != null) sql.append(" AND a.usuario_id = :usuarioId");
        if (fechaInicio    != null) sql.append(" AND a.created_at >= :fechaInicio");
        if (fechaFin       != null) sql.append(" AND a.created_at <= :fechaFin");
        if (emailBusqueda  != null) sql.append(" AND u.email LIKE :email");
        sql.append(" ORDER BY a.created_at DESC LIMIT :size OFFSET :offset");

        var query = em.createNativeQuery(sql.toString());
        if (accion         != null) query.setParameter("accion",      accion);
        if (usuarioId      != null) query.setParameter("usuarioId",   usuarioId);
        if (fechaInicio    != null) query.setParameter("fechaInicio", fechaInicio);
        if (fechaFin       != null) query.setParameter("fechaFin",    fechaFin);
        if (emailBusqueda  != null) query.setParameter("email",       "%" + emailBusqueda + "%");
        query.setParameter("size",   size);
        query.setParameter("offset", page * size);

        List<Object[]> rows = query.getResultList();
        List<AuditLogItemDto> result = new java.util.ArrayList<>();
        for (Object[] r : rows) {
            AuditLogItemDto dto = new AuditLogItemDto();
            dto.id              = r[0] != null ? ((Number) r[0]).longValue()    : null;
            dto.usuarioId       = r[1] != null ? ((Number) r[1]).intValue()     : null;
            dto.usuarioEmail    = (String) r[2];
            dto.accion          = (String) r[3];
            dto.entidadAfectada = (String) r[4];
            dto.registroId      = (String) r[5];
            dto.detalle         = (String) r[6];
            dto.ipAddress       = (String) r[7];
            dto.createdAt       = (String) r[8];
            result.add(dto);
        }
        return result;
    }

    /**
     * Cuenta el total de registros con los mismos filtros (para calcular totalPages).
     */
    public long contar(String accion, Integer usuarioId, String fechaInicio, String fechaFin, String emailBusqueda) {
        StringBuilder sql = new StringBuilder(
            "SELECT COUNT(*) FROM audit_log a LEFT JOIN usuarios u ON a.usuario_id = u.id WHERE 1=1");
        if (accion        != null) sql.append(" AND a.accion = :accion");
        if (usuarioId     != null) sql.append(" AND a.usuario_id = :usuarioId");
        if (fechaInicio   != null) sql.append(" AND a.created_at >= :fechaInicio");
        if (fechaFin      != null) sql.append(" AND a.created_at <= :fechaFin");
        if (emailBusqueda != null) sql.append(" AND u.email LIKE :email");

        var query = em.createNativeQuery(sql.toString());
        if (accion        != null) query.setParameter("accion",      accion);
        if (usuarioId     != null) query.setParameter("usuarioId",   usuarioId);
        if (fechaInicio   != null) query.setParameter("fechaInicio", fechaInicio);
        if (fechaFin      != null) query.setParameter("fechaFin",    fechaFin);
        if (emailBusqueda != null) query.setParameter("email",       "%" + emailBusqueda + "%");

        return ((Number) query.getSingleResult()).longValue();
    }
}
