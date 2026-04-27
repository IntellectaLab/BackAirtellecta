package com.airtellecta.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "cat_sexo")
public class CatSexo extends PanacheEntityBase {

    @Id
    public Byte cveSexo;

    public String descripcion;
}
