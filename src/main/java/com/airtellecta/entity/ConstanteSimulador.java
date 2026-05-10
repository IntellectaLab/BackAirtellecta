package com.airtellecta.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "constantes_simulador")
public class ConstanteSimulador extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer id;

    public String clave;

    @Column(precision = 18, scale = 4)
    public BigDecimal valor;

    public String unidad;

    public String categoria;

    @Column(name = "anio_referencia")
    public Short anioReferencia;

    @Column(columnDefinition = "TEXT")
    public String descripcion;

    @Column(columnDefinition = "TEXT", nullable = false)
    public String fuente;

    public String fuenteUrl;

    public Boolean activo;

    @Column(name = "created_by")
    public Integer createdBy;

    @Column(name = "updated_by")
    public Integer updatedBy;

    @Column(name = "created_at", insertable = false, updatable = false)
    public LocalDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    public LocalDateTime updatedAt;
}
