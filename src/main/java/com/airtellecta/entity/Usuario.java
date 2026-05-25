package com.airtellecta.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "usuarios")
public class Usuario extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer id;

    @Column(name = "firebase_uid", nullable = false, unique = true, length = 128)
    public String firebaseUid;

    @Column(nullable = false, unique = true, length = 255)
    public String email;

    @Column(name = "nombre_completo", nullable = false, length = 200)
    public String nombreCompleto;

    @Column(length = 100)
    public String cargo;

    @Column(length = 200)
    public String institucion;

    @Column(nullable = false)
    public String rol;

    @Column(name = "entidad_id")
    public Byte entidadId;

    @Column(nullable = false)
    public Byte activo;

    @Column(name = "activation_link", columnDefinition = "TEXT")
    public String activationLink;

    @Column(name = "ultimo_acceso")
    public LocalDateTime ultimoAcceso;

    @Column(name = "created_at", nullable = false, updatable = false)
    public LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    public LocalDateTime updatedAt;

}
