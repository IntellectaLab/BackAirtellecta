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
@Table(name = "pob_proyecciones")
public class PobProyeccion extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer id;

    public Byte cveEntidad;

    public Short anio;

    public Byte sexo;

    @Column(name = "pob_0_4")
    public Integer pob04;

    @Column(name = "pob_5_9")
    public Integer pob59;

    @Column(name = "pob_10_14")
    public Integer pob1014;

    @Column(name = "pob_15_19")
    public Integer pob1519;

    @Column(name = "pob_20_24")
    public Integer pob2024;

    @Column(name = "pob_25_29")
    public Integer pob2529;

    @Column(name = "pob_30_34")
    public Integer pob3034;

    @Column(name = "pob_35_39")
    public Integer pob3539;

    @Column(name = "pob_40_44")
    public Integer pob4044;

    @Column(name = "pob_45_49")
    public Integer pob4549;

    @Column(name = "pob_50_54")
    public Integer pob5054;

    @Column(name = "pob_55_59")
    public Integer pob5559;

    @Column(name = "pob_60_64")
    public Integer pob6064;

    @Column(name = "pob_65_69")
    public Integer pob6569;

    @Column(name = "pob_70_74")
    public Integer pob7074;

    @Column(name = "pob_75_79")
    public Integer pob7579;

    @Column(name = "pob_80_84")
    public Integer pob8084;

    @Column(name = "pob_85_mas")
    public Integer pob85Mas;

    @Column(insertable = false, updatable = false)
    public Integer pobTotal;

    @Column(insertable = false, updatable = false)
    public LocalDateTime createdAt;
}
