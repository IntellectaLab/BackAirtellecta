package com.airtellecta.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "encuesta_encodat")
public class EncuestaEncodat extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer id;

    public String idPers;

    public Byte cveEntidad;

    public Byte cveSexo;

    public Byte ds3;

    public Byte ds8;

    public Byte cveCategoriaFumador;

    public Byte cveGrupoEdad;

    public Byte tb01;
    public Byte tb02;
    public Byte tb03;
    public Byte tb05;
    public Byte tb06;
    public Byte tb07;
    public Byte tb08;
    public Byte tb09;
    public Byte tb21;
    public Byte tb22;
    public Byte tb28;

    public Byte tg1;
    public Byte tg2;
    public Byte tg3;
    public Byte tg4;
    public Byte tg5;

    public Byte tp1;
    public Byte tp3a;
    public Byte tp3b;

    @Column(precision = 18, scale = 10)
    public BigDecimal pondeSs;

    public Byte usaVape;

    public Byte usoDual;

    @Column(insertable = false, updatable = false)
    public Byte fumadorActual;

    @Column(insertable = false, updatable = false)
    public Byte inicioTemprano;

    @Column(insertable = false, updatable = false)
    public Byte conoceVape;

    @Column(insertable = false, updatable = false)
    public LocalDateTime createdAt;
}
