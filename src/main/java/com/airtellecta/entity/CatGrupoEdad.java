package com.airtellecta.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "cat_grupos_edad")
public class CatGrupoEdad extends PanacheEntityBase {

    @Id
    public Byte cveGrupo;

    public String etiqueta;

    public Byte edadMin;

    public Byte edadMax;
}
