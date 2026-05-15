package com.airtellecta.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "campanas")
public class Campana extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer id;

    public String nombre;

    public Short anioInicio;

    public Short anioFin;

    public BigDecimal presupuestoMxn;

    public String region;

    public String poblacionObjetivo;

    public String tipoMedio;

    public String fuente;

    @Column(columnDefinition = "TEXT")
    public String urlEvidencia;
}
