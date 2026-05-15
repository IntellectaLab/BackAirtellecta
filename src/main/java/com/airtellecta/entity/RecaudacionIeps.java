package com.airtellecta.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "recaudacion_ieps_anual")
public class RecaudacionIeps extends PanacheEntityBase {

    @Id
    public Short anio;

    public BigDecimal montoMdp;

    public String fuente;

    @Column(columnDefinition = "TEXT")
    public String urlInforme;
}
