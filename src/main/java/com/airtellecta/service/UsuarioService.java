package com.airtellecta.service;

import com.airtellecta.dto.request.ActualizarUsuarioRequest;
import com.airtellecta.dto.request.CrearUsuarioRequest;
import com.airtellecta.dto.response.UsuarioDto;
import com.airtellecta.entity.Usuario;
import com.airtellecta.repository.UsuarioRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@ApplicationScoped
public class UsuarioService {

    @Inject
    UsuarioRepository repository;

    @Inject
    FirebaseAuth firebaseAuth;

    // -------------------------------------------------------------------------
    // Queries
    // -------------------------------------------------------------------------

    public List<UsuarioDto> listarTodos() {
        return repository.listarTodos();
    }

    public UsuarioDto obtenerPorId(int id) {
        return repository.buscarPorId(id);
    }

    // -------------------------------------------------------------------------
    // Commands
    // -------------------------------------------------------------------------

    @Transactional
    public UsuarioDto crear(CrearUsuarioRequest req, String adminUid, String ip) {
        // 1. Create Firebase user with random password (never shared)
        String randomPassword = UUID.randomUUID().toString();
        UserRecord firebaseUser;
        try {
            firebaseUser = firebaseAuth.createUser(
                    new UserRecord.CreateRequest()
                            .setEmail(req.email)
                            .setPassword(randomPassword)
                            .setDisplayName(req.nombreCompleto)
                            .setEmailVerified(true)
            );
        } catch (FirebaseAuthException e) {
            throw new RuntimeException("Error al crear usuario en Firebase: " + e.getMessage(), e);
        }

        // 2. Set custom claim; on failure clean up the Firebase user
        try {
            firebaseAuth.setCustomUserClaims(firebaseUser.getUid(), Map.of("role", req.rol));
        } catch (FirebaseAuthException e) {
            try {
                firebaseAuth.deleteUser(firebaseUser.getUid());
            } catch (FirebaseAuthException cleanup) {
                // best-effort cleanup
            }
            throw new RuntimeException("Error al asignar claims en Firebase: " + e.getMessage(), e);
        }

        // 3. Generate password reset link (Firebase sends email automatically is not supported
        //    via Admin SDK — we generate the link and return it to the admin)
        String activationLink;
        try {
            activationLink = firebaseAuth.generatePasswordResetLink(req.email);
        } catch (FirebaseAuthException e) {
            throw new RuntimeException("Error al generar link de activacion: " + e.getMessage(), e);
        }

        // 4. Persist in DB
        Usuario u = new Usuario();
        u.firebaseUid    = firebaseUser.getUid();
        u.email          = req.email;
        u.nombreCompleto = req.nombreCompleto;
        u.cargo          = req.cargo;
        u.institucion    = req.institucion;
        u.rol            = req.rol;
        u.entidadId      = req.entidadId;
        u.activo         = (byte) 1;
        u.activationLink = activationLink;

        repository.insertar(u);

        // 5. Audit log (sin password — seguridad B2G)
        Usuario admin = repository.buscarPorFirebaseUid(adminUid);
        Integer adminId = admin != null ? admin.id : null;

        String detalle = String.format(
                "{\"email\":\"%s\",\"rol\":\"%s\"}",
                req.email, req.rol
        );
        repository.insertarAudit(adminId, "CREAR_USUARIO", "usuarios", req.email, detalle, ip);

        // 6. Return DTO with activation link
        UsuarioDto dto = new UsuarioDto();
        dto.email          = u.email;
        dto.nombreCompleto = u.nombreCompleto;
        dto.cargo          = u.cargo;
        dto.institucion    = u.institucion;
        dto.rol            = u.rol;
        dto.entidadId      = u.entidadId;
        dto.activo         = true;
        dto.activationLink = activationLink;
        return dto;
    }

    @Transactional
    public UsuarioDto actualizar(int id, ActualizarUsuarioRequest req, String adminUid, String ip) {
        // 1. Fetch existing record
        UsuarioDto existing = repository.buscarPorId(id);

        // 2. Coalesce values (use req if non-null, else existing)
        String nuevoNombre    = req.nombreCompleto != null ? req.nombreCompleto : existing.nombreCompleto;
        String nuevoCargo     = req.cargo          != null ? req.cargo          : existing.cargo;
        String nuevaInst      = req.institucion    != null ? req.institucion    : existing.institucion;
        String nuevoRol       = req.rol            != null ? req.rol            : existing.rol;
        Byte   nuevoEntidadId = req.entidadId      != null ? req.entidadId      : existing.entidadId;
        Boolean nuevoActivo   = req.activo         != null ? req.activo         : existing.activo;

        // 3. Sync Firebase if role or activo changed
        if (req.rol != null && !req.rol.equals(existing.rol)) {
            try {
                UserRecord fbUser = firebaseAuth.getUserByEmail(existing.email);
                firebaseAuth.setCustomUserClaims(fbUser.getUid(), Map.of("role", req.rol));
            } catch (FirebaseAuthException e) {
                throw new RuntimeException("Error al actualizar claims en Firebase: " + e.getMessage(), e);
            }
        }

        if (req.activo != null) {
            if (!req.activo) {
                // Deactivate: disable in Firebase
                try {
                    UserRecord fbUser = firebaseAuth.getUserByEmail(existing.email);
                    firebaseAuth.updateUser(new UserRecord.UpdateRequest(fbUser.getUid()).setDisabled(true));
                } catch (FirebaseAuthException e) {
                    throw new RuntimeException("Error al deshabilitar usuario en Firebase: " + e.getMessage(), e);
                }
            } else if (req.activo && !existing.activo) {
                // Re-activate: enable in Firebase
                try {
                    UserRecord fbUser = firebaseAuth.getUserByEmail(existing.email);
                    firebaseAuth.updateUser(new UserRecord.UpdateRequest(fbUser.getUid()).setDisabled(false));
                } catch (FirebaseAuthException e) {
                    throw new RuntimeException("Error al rehabilitar usuario en Firebase: " + e.getMessage(), e);
                }
            }
        }

        // 4. Persist changes
        repository.actualizar(id, nuevoNombre, nuevoCargo, nuevaInst, nuevoRol, nuevoEntidadId, nuevoActivo);

        // 5. Audit log
        Usuario admin = repository.buscarPorFirebaseUid(adminUid);
        Integer adminId = admin != null ? admin.id : null;

        String detalle = String.format(
                "{\"id\":%d,\"rol\":\"%s\",\"activo\":%b}",
                id, nuevoRol, nuevoActivo
        );
        repository.insertarAudit(adminId, "ACTUALIZAR_USUARIO", "usuarios", String.valueOf(id), detalle, ip);

        // 6. Return updated record
        return repository.buscarPorId(id);
    }

    @Transactional
    public void registrarAcceso(String firebaseUid) {
        repository.registrarAcceso(firebaseUid);
    }
}
