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
@Table(name = "urgencias")
public class Urgencia extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer id;

    public Short anio;

    public Byte cveEntidad;

    public Byte cveGrupoEtario;

    public Byte cveSexo;

    public Short f10;
    public Short f11;
    public Short f12;
    public Short f13;
    public Short f14;
    public Short f15;
    public Short f16;
    public Short f17;
    public Short f18;
    public Short f19;

    @Column(insertable = false, updatable = false)
    public LocalDateTime createdAt;
}
