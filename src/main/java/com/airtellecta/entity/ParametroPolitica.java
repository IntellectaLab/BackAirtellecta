package com.airtellecta.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "parametros_politica")
public class ParametroPolitica extends PanacheEntityBase {

    @Id
    public String cvePolitica;

    public String nombre;

    public String categoria;

    public BigDecimal efectoPrevalenciaPct;

    public BigDecimal efectoInicioPct;

    public BigDecimal efectoCesacionPct;

    public String fuenteReferencia;
}
