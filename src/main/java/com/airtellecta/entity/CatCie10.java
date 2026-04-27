package com.airtellecta.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "cat_cie10")
public class CatCie10 extends PanacheEntityBase {

    @Id
    public Byte cve;

    public String codigo;

    public String trastorno;

    public String grupo;
}
