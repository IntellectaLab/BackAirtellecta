package com.airtellecta.repository;

import com.airtellecta.dto.response.UsuarioDto;
import com.airtellecta.entity.Usuario;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Tuple;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class UsuarioRepository {

    @Inject
    EntityManager em;

    // -------------------------------------------------------------------------
    // Public API
    // -------------------------------------------------------------------------

    public List<UsuarioDto> listarTodos() {
        @SuppressWarnings("unchecked")
        List<Tuple> rows = em.createNativeQuery("""
                SELECT id, email, nombre_completo, cargo, institucion,
                       rol, entidad_id, activo, activation_link, ultimo_acceso, created_at
                FROM usuarios
                ORDER BY created_at DESC
                """, Tuple.class)
                .getResultList();

        return rows.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public UsuarioDto buscarPorId(int id) {
        Tuple row = (Tuple) em.createNativeQuery("""
                SELECT id, email, nombre_completo, cargo, institucion,
                       rol, entidad_id, activo, activation_link, ultimo_acceso, created_at
                FROM usuarios
                WHERE id = :id
                """, Tuple.class)
                .setParameter("id", id)
                .getSingleResult();

        return toDto(row);
    }

    public Usuario buscarPorFirebaseUid(String uid) {
        return em.createQuery(
                        "SELECT u FROM Usuario u WHERE u.firebaseUid = :uid", Usuario.class)
                .setParameter("uid", uid)
                .getResultStream()
                .findFirst()
                .orElse(null);
    }

    public void insertar(Usuario u) {
        em.createNativeQuery("""
                INSERT INTO usuarios
                    (firebase_uid, email, nombre_completo, cargo, institucion,
                     rol, entidad_id, activo, activation_link, created_at, updated_at)
                VALUES
                    (:firebaseUid, :email, :nombreCompleto, :cargo, :institucion,
                     :rol, :entidadId, :activo, :activationLink, NOW(), NOW())
                """)
                .setParameter("firebaseUid", u.firebaseUid)
                .setParameter("email", u.email)
                .setParameter("nombreCompleto", u.nombreCompleto)
                .setParameter("cargo", u.cargo)
                .setParameter("institucion", u.institucion)
                .setParameter("rol", u.rol)
                .setParameter("entidadId", u.entidadId)
                .setParameter("activo", u.activo)
                .setParameter("activationLink", u.activationLink)
                .executeUpdate();
    }

    public void actualizar(int id, String nombre, String cargo, String inst,
                           String rol, Byte entidadId, Boolean activo) {
        em.createNativeQuery("""
                UPDATE usuarios
                SET nombre_completo = :nombre,
                    cargo           = :cargo,
                    institucion     = :inst,
                    rol             = :rol,
                    entidad_id      = :entidadId,
                    activo          = :activo,
                    updated_at      = NOW()
                WHERE id = :id
                """)
                .setParameter("nombre", nombre)
                .setParameter("cargo", cargo)
                .setParameter("inst", inst)
                .setParameter("rol", rol)
                .setParameter("entidadId", entidadId)
                .setParameter("activo", activo != null && activo ? (byte) 1 : (byte) 0)
                .setParameter("id", id)
                .executeUpdate();
    }

    public void registrarAcceso(String firebaseUid) {
        em.createNativeQuery("""
                UPDATE usuarios
                SET ultimo_acceso = NOW(),
                    updated_at    = NOW()
                WHERE firebase_uid = :uid
                """)
                .setParameter("uid", firebaseUid)
                .executeUpdate();
    }

    public void insertarAudit(Integer usuarioId, String accion, String entidadAfectada,
                              String registroId, String detalleJson, String ip) {
        em.createNativeQuery("""
                INSERT INTO audit_log
                    (usuario_id, accion, entidad_afectada, registro_id, detalle, ip_address)
                VALUES
                    (:usuarioId, :accion, :entidadAfectada, :registroId,
                     CAST(:detalle AS JSON), :ip)
                """)
                .setParameter("usuarioId", usuarioId)
                .setParameter("accion", accion)
                .setParameter("entidadAfectada", entidadAfectada)
                .setParameter("registroId", registroId)
                .setParameter("detalle", detalleJson)
                .setParameter("ip", ip)
                .executeUpdate();
    }

    // -------------------------------------------------------------------------
    // Mapping helper
    // -------------------------------------------------------------------------

    private java.time.LocalDateTime toLocalDateTime(Object raw) {
        if (raw instanceof java.time.LocalDateTime ldt) return ldt;
        if (raw instanceof java.sql.Timestamp ts) return ts.toLocalDateTime();
        throw new IllegalArgumentException("Unexpected temporal type: " + raw.getClass());
    }

    private UsuarioDto toDto(Tuple row) {
        UsuarioDto dto = new UsuarioDto();

        dto.id             = ((Number) row.get("id")).intValue();
        dto.email          = (String) row.get("email");
        dto.nombreCompleto = (String) row.get("nombre_completo");
        dto.cargo          = (String) row.get("cargo");
        dto.institucion    = (String) row.get("institucion");
        dto.rol            = (String) row.get("rol");

        Object entidadRaw = row.get("entidad_id");
        dto.entidadId = entidadRaw != null ? ((Number) entidadRaw).byteValue() : null;

        dto.activo = ((Number) row.get("activo")).intValue() == 1;

        Object ultimoAccesoRaw = row.get("ultimo_acceso");
        dto.ultimoAcceso = ultimoAccesoRaw != null ? toLocalDateTime(ultimoAccesoRaw) : null;

        // Solo mostrar link si el usuario aun no ha activado su cuenta
        if (dto.ultimoAcceso == null) {
            dto.activationLink = (String) row.get("activation_link");
        }

        dto.createdAt = toLocalDateTime(row.get("created_at"));

        return dto;
    }
}
