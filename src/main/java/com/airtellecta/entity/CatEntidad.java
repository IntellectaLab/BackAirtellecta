package com.airtellecta.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "cat_entidades")
public class CatEntidad extends PanacheEntityBase {

    @Id
    public Byte cveEntidad;

    public String nombre;

    public String abreviatura;
}
