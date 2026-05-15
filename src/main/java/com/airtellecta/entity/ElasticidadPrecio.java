package com.airtellecta.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "elasticidades_precio")
public class ElasticidadPrecio extends PanacheEntityBase {

    @Id
    public String grupoEdad;

    public BigDecimal elasticidad;

    public String fuente;
}
