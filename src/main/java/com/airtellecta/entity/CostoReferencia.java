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
@Table(name = "costos_referencia")
public class CostoReferencia extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer id;

    public String cveCie10;

    public BigDecimal costoPromedioPacienteMxn;

    public Short anioBase;

    public BigDecimal factorInflacion;

    @Column(insertable = false, updatable = false)
    public BigDecimal costoAjustado2025;

    public String fuente;

    public String fuenteDoi;

    @Column(columnDefinition = "TEXT")
    public String fuenteUrl;

    @Column(columnDefinition = "TEXT")
    public String notas;
}
