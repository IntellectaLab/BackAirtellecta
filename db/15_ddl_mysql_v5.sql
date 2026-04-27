-- ================================================================
-- INTELLECTA — DDL MySQL 8.0 Completo
-- Schema v5: 23 tablas + 5 vistas
-- Fuente: 13_dbml_v5_completo.dbml
-- Generado: 2026-04-20
--
-- Orden de ejecución:
--   1. Catálogos (sin dependencias)
--   2. Tablas de hechos (dependen de catálogos)
--   3. Capa aplicación (dependen de catálogos + usuarios)
--   4. Vistas unificadas
--   5. Datos iniciales (INSERT catálogos + fuentes + configuración)
--
-- Reglas del proyecto:
--   - SIN triggers, stored procedures ni eventos
--   - snake_case en tablas y columnas
--   - Lógica de negocio en backend Quarkus, no en MySQL
-- ================================================================

CREATE DATABASE IF NOT EXISTS intellecta
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE intellecta;


-- ================================================================
--  E T A P A   1  —  C A T Á L O G O S  (7 tablas, 95 filas)
-- ================================================================

CREATE TABLE cat_entidades (
  cve_entidad TINYINT NOT NULL,
  nombre      VARCHAR(50) NOT NULL,
  abreviatura VARCHAR(5) NOT NULL,

  PRIMARY KEY (cve_entidad),
  UNIQUE KEY uq_ent_nombre (nombre),
  UNIQUE KEY uq_ent_abrev (abreviatura),

  CONSTRAINT chk_ent_cve CHECK (cve_entidad BETWEEN 1 AND 34)
) ENGINE=InnoDB;


CREATE TABLE cat_sexo (
  cve_sexo    TINYINT NOT NULL,
  descripcion VARCHAR(20) NOT NULL,

  PRIMARY KEY (cve_sexo),
  UNIQUE KEY uq_sexo_desc (descripcion),

  CONSTRAINT chk_sexo_cve CHECK (cve_sexo BETWEEN 1 AND 5)
) ENGINE=InnoDB;


CREATE TABLE cat_grupos_edad (
  cve_grupo TINYINT NOT NULL,
  etiqueta  VARCHAR(10) NOT NULL,
  edad_min  TINYINT NOT NULL,
  edad_max  TINYINT NOT NULL,

  PRIMARY KEY (cve_grupo),
  UNIQUE KEY uq_grp_etiqueta (etiqueta),

  CONSTRAINT chk_grp_rango CHECK (edad_min < edad_max)
) ENGINE=InnoDB;


CREATE TABLE cat_grupos_edad_quinquenal (
  cve_grupo TINYINT NOT NULL,
  etiqueta  VARCHAR(25) NOT NULL,
  edad_min  TINYINT DEFAULT NULL,
  edad_max  TINYINT DEFAULT NULL,

  PRIMARY KEY (cve_grupo),
  UNIQUE KEY uq_grpq_etiqueta (etiqueta)
) ENGINE=InnoDB;


CREATE TABLE cat_niveles_educativos (
  cve_nivel   TINYINT NOT NULL,
  descripcion VARCHAR(50) NOT NULL,

  PRIMARY KEY (cve_nivel)
) ENGINE=InnoDB;


CREATE TABLE cat_categorias_fumador (
  cve_categoria TINYINT NOT NULL,
  descripcion   VARCHAR(30) NOT NULL,

  PRIMARY KEY (cve_categoria),
  UNIQUE KEY uq_catfum_desc (descripcion),

  CONSTRAINT chk_catfum_cve CHECK (cve_categoria BETWEEN 1 AND 5)
) ENGINE=InnoDB;


CREATE TABLE cat_cie10 (
  cve       TINYINT NOT NULL,
  codigo    CHAR(3) NOT NULL,
  trastorno VARCHAR(60) NOT NULL,
  grupo     VARCHAR(15) NOT NULL,

  PRIMARY KEY (cve),
  UNIQUE KEY uq_cie_codigo (codigo),

  CONSTRAINT chk_cie_cve CHECK (cve BETWEEN 1 AND 10)
) ENGINE=InnoDB;


-- ================================================================
--  E T A P A   2  —  T A B L A S   D E   H E C H O S  (9 tablas)
-- ================================================================

-- ────────────────────────────────
-- CONAPO — Proyecciones de población
-- ────────────────────────────────

CREATE TABLE pob_proyecciones (
  id          INT NOT NULL AUTO_INCREMENT,
  cve_entidad TINYINT NOT NULL,
  anio        SMALLINT NOT NULL,
  sexo        TINYINT NOT NULL,
  pob_0_4     INT NOT NULL,
  pob_5_9     INT NOT NULL,
  pob_10_14   INT NOT NULL,
  pob_15_19   INT NOT NULL,
  pob_20_24   INT NOT NULL,
  pob_25_29   INT NOT NULL,
  pob_30_34   INT NOT NULL,
  pob_35_39   INT NOT NULL,
  pob_40_44   INT NOT NULL,
  pob_45_49   INT NOT NULL,
  pob_50_54   INT NOT NULL,
  pob_55_59   INT NOT NULL,
  pob_60_64   INT NOT NULL,
  pob_65_69   INT NOT NULL,
  pob_70_74   INT NOT NULL,
  pob_75_79   INT NOT NULL,
  pob_80_84   INT NOT NULL,
  pob_85_mas  INT NOT NULL,

  -- GENERATED STORED: suma de 18 quinquenales (BCNF, zero inconsistencia)
  pob_total INT GENERATED ALWAYS AS (
    pob_0_4 + pob_5_9 + pob_10_14 + pob_15_19 + pob_20_24 + pob_25_29 +
    pob_30_34 + pob_35_39 + pob_40_44 + pob_45_49 + pob_50_54 + pob_55_59 +
    pob_60_64 + pob_65_69 + pob_70_74 + pob_75_79 + pob_80_84 + pob_85_mas
  ) STORED,

  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

  PRIMARY KEY (id),
  UNIQUE KEY uq_pob_ent_anio_sexo (cve_entidad, anio, sexo),
  KEY idx_pob_anio (anio),

  CONSTRAINT fk_pob_ent  FOREIGN KEY (cve_entidad) REFERENCES cat_entidades(cve_entidad) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT fk_pob_sexo FOREIGN KEY (sexo)         REFERENCES cat_sexo(cve_sexo)         ON DELETE RESTRICT ON UPDATE CASCADE,

  CONSTRAINT chk_pob_anio CHECK (anio BETWEEN 2011 AND 2030),
  CONSTRAINT chk_pob_vals CHECK (
    pob_0_4 >= 0 AND pob_5_9 >= 0 AND pob_10_14 >= 0 AND pob_15_19 >= 0 AND
    pob_20_24 >= 0 AND pob_25_29 >= 0 AND pob_30_34 >= 0 AND pob_35_39 >= 0 AND
    pob_40_44 >= 0 AND pob_45_49 >= 0 AND pob_50_54 >= 0 AND pob_55_59 >= 0 AND
    pob_60_64 >= 0 AND pob_65_69 >= 0 AND pob_70_74 >= 0 AND pob_75_79 >= 0 AND
    pob_80_84 >= 0 AND pob_85_mas >= 0
  )
) ENGINE=InnoDB;


-- ────────────────────────────────
-- INEGI — Defunciones
-- ────────────────────────────────

CREATE TABLE defunciones (
  id               INT NOT NULL AUTO_INCREMENT,
  anio             SMALLINT NOT NULL,
  cve_entidad      TINYINT NOT NULL,
  cve_grupo_etario TINYINT NOT NULL,
  cve_sexo         TINYINT NOT NULL,
  f10 SMALLINT NOT NULL DEFAULT 0,
  f11 SMALLINT NOT NULL DEFAULT 0,
  f12 SMALLINT NOT NULL DEFAULT 0,
  f14 SMALLINT NOT NULL DEFAULT 0,
  f15 SMALLINT NOT NULL DEFAULT 0,
  f17 SMALLINT NOT NULL DEFAULT 0,
  f18 SMALLINT NOT NULL DEFAULT 0,
  f19 SMALLINT NOT NULL DEFAULT 0,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

  PRIMARY KEY (id),
  KEY idx_def_anio_ent (anio, cve_entidad),
  KEY idx_def_ent (cve_entidad),
  KEY idx_def_anio (anio),
  KEY idx_def_anio_ent_sexo (anio, cve_entidad, cve_sexo),

  CONSTRAINT fk_def_ent   FOREIGN KEY (cve_entidad)      REFERENCES cat_entidades(cve_entidad)              ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT fk_def_grupo FOREIGN KEY (cve_grupo_etario)  REFERENCES cat_grupos_edad_quinquenal(cve_grupo)   ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT fk_def_sexo  FOREIGN KEY (cve_sexo)          REFERENCES cat_sexo(cve_sexo)                      ON DELETE RESTRICT ON UPDATE CASCADE,

  CONSTRAINT chk_def_anio CHECK (anio BETWEEN 2011 AND 2023),
  CONSTRAINT chk_def_fcodes CHECK (
    f10 >= 0 AND f11 >= 0 AND f12 >= 0 AND f14 >= 0 AND
    f15 >= 0 AND f17 >= 0 AND f18 >= 0 AND f19 >= 0
  )
) ENGINE=InnoDB;


-- ────────────────────────────────
-- INEGI — Urgencias
-- ────────────────────────────────

CREATE TABLE urgencias (
  id               INT NOT NULL AUTO_INCREMENT,
  anio             SMALLINT NOT NULL,
  cve_entidad      TINYINT NOT NULL,
  cve_grupo_etario TINYINT NOT NULL,
  cve_sexo         TINYINT NOT NULL,
  f10 SMALLINT NOT NULL DEFAULT 0,
  f11 SMALLINT NOT NULL DEFAULT 0,
  f12 SMALLINT NOT NULL DEFAULT 0,
  f13 SMALLINT NOT NULL DEFAULT 0,
  f14 SMALLINT NOT NULL DEFAULT 0,
  f15 SMALLINT NOT NULL DEFAULT 0,
  f16 SMALLINT NOT NULL DEFAULT 0,
  f17 SMALLINT NOT NULL DEFAULT 0,
  f18 SMALLINT NOT NULL DEFAULT 0,
  f19 SMALLINT NOT NULL DEFAULT 0,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

  PRIMARY KEY (id),
  KEY idx_urg_anio_ent (anio, cve_entidad),
  KEY idx_urg_ent (cve_entidad),
  KEY idx_urg_anio (anio),
  KEY idx_urg_anio_ent_sexo (anio, cve_entidad, cve_sexo),

  CONSTRAINT fk_urg_ent   FOREIGN KEY (cve_entidad)      REFERENCES cat_entidades(cve_entidad)              ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT fk_urg_grupo FOREIGN KEY (cve_grupo_etario)  REFERENCES cat_grupos_edad_quinquenal(cve_grupo)   ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT fk_urg_sexo  FOREIGN KEY (cve_sexo)          REFERENCES cat_sexo(cve_sexo)                      ON DELETE RESTRICT ON UPDATE CASCADE,

  CONSTRAINT chk_urg_anio CHECK (anio BETWEEN 2011 AND 2023),
  CONSTRAINT chk_urg_fcodes CHECK (
    f10 >= 0 AND f11 >= 0 AND f12 >= 0 AND f13 >= 0 AND f14 >= 0 AND
    f15 >= 0 AND f16 >= 0 AND f17 >= 0 AND f18 >= 0 AND f19 >= 0
  )
) ENGINE=InnoDB;


-- ────────────────────────────────
-- BeatQuest — Clínicas
-- ────────────────────────────────

CREATE TABLE clinicas (
  id                    SMALLINT NOT NULL AUTO_INCREMENT,
  cve_entidad           TINYINT NOT NULL,
  nombre                VARCHAR(150) NOT NULL,
  direccion             VARCHAR(300) NOT NULL,
  telefono              VARCHAR(20) DEFAULT NULL,
  extension             VARCHAR(10) DEFAULT NULL,
  delegacion_original   VARCHAR(50) NOT NULL,
  latitud               DECIMAL(10,7) DEFAULT NULL,
  longitud              DECIMAL(10,7) DEFAULT NULL,
  created_at            TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

  PRIMARY KEY (id),
  KEY idx_cli_ent (cve_entidad),

  CONSTRAINT fk_cli_ent FOREIGN KEY (cve_entidad) REFERENCES cat_entidades(cve_entidad) ON DELETE RESTRICT ON UPDATE CASCADE,

  CONSTRAINT chk_cli_lat CHECK (latitud BETWEEN 14.0 AND 33.0 OR latitud IS NULL),
  CONSTRAINT chk_cli_lng CHECK (longitud BETWEEN -118.0 AND -86.0 OR longitud IS NULL)
) ENGINE=InnoDB;


-- ────────────────────────────────
-- ENCODAT 2016-17 — Encuesta principal tabaco
-- ────────────────────────────────

CREATE TABLE encuesta_encodat (
  id          INT NOT NULL AUTO_INCREMENT,
  id_pers     VARCHAR(25) NOT NULL,
  cve_entidad TINYINT NOT NULL,

  -- Demográficas
  cve_sexo             TINYINT NOT NULL,
  ds3                  TINYINT NOT NULL,
  ds8                  TINYINT DEFAULT NULL,

  -- FK a catálogos
  cve_categoria_fumador TINYINT DEFAULT NULL,
  cve_grupo_edad        TINYINT DEFAULT NULL,

  -- Tabaco
  tb01 TINYINT DEFAULT NULL,
  tb02 TINYINT DEFAULT NULL,
  tb03 TINYINT DEFAULT NULL,
  tb05 TINYINT DEFAULT NULL,
  tb06 TINYINT DEFAULT NULL,
  tb07 TINYINT DEFAULT NULL,
  tb08 TINYINT DEFAULT NULL,
  tb09 TINYINT DEFAULT NULL,
  tb21 TINYINT DEFAULT NULL,
  tb22 TINYINT DEFAULT NULL,
  tb28 TINYINT DEFAULT NULL,
  tb29 TINYINT DEFAULT NULL,
  tb31 TINYINT DEFAULT NULL,
  tb33 TINYINT DEFAULT NULL,
  tb34 TINYINT DEFAULT NULL,
  tb40 TINYINT DEFAULT NULL,
  tb41 TINYINT DEFAULT NULL,
  tb42 TINYINT DEFAULT NULL,
  tb46 TINYINT DEFAULT NULL,

  -- Vapeo
  tg1 TINYINT DEFAULT NULL,
  tg2 TINYINT DEFAULT NULL,
  tg3 TINYINT DEFAULT NULL,
  tg4 TINYINT DEFAULT NULL,
  tg5 TINYINT DEFAULT NULL,

  -- Percepción de riesgo
  tp1  TINYINT DEFAULT NULL,
  tp3a TINYINT DEFAULT NULL,
  tp3b TINYINT DEFAULT NULL,

  -- Factor de expansión
  ponde_ss DECIMAL(18,10) NOT NULL,

  -- Columnas derivadas del CSV fuente (pre-computadas por preparador original)
  usa_vape   TINYINT DEFAULT NULL,
  uso_dual   TINYINT DEFAULT NULL,

  -- GENERATED VIRTUAL: derivadas con fórmula verificable
  fumador_actual  TINYINT GENERATED ALWAYS AS (CASE WHEN cve_categoria_fumador IN (3,4) THEN 1 ELSE 0 END) VIRTUAL,
  inicio_temprano TINYINT GENERATED ALWAYS AS (CASE WHEN tb06 < 18 THEN 1 ELSE 0 END) VIRTUAL,
  conoce_vape     TINYINT GENERATED ALWAYS AS (CASE WHEN tg1 = 1 THEN 1 ELSE 0 END) VIRTUAL,

  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

  PRIMARY KEY (id),
  UNIQUE KEY uq_enc_idpers (id_pers),
  KEY idx_enc_ent (cve_entidad),
  KEY idx_enc_sexo (cve_sexo),
  KEY idx_enc_cat_fumador (cve_categoria_fumador),
  KEY idx_enc_ent_sexo (cve_entidad, cve_sexo),
  KEY idx_enc_fumador_ent (cve_categoria_fumador, cve_entidad),
  KEY idx_enc_grupo_edad (cve_grupo_edad),

  CONSTRAINT fk_enc_ent     FOREIGN KEY (cve_entidad)           REFERENCES cat_entidades(cve_entidad)              ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT fk_enc_sexo    FOREIGN KEY (cve_sexo)              REFERENCES cat_sexo(cve_sexo)                      ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT fk_enc_fumador FOREIGN KEY (cve_categoria_fumador) REFERENCES cat_categorias_fumador(cve_categoria)   ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT fk_enc_grupo   FOREIGN KEY (cve_grupo_edad)        REFERENCES cat_grupos_edad(cve_grupo)              ON DELETE RESTRICT ON UPDATE CASCADE,


  CONSTRAINT chk_enc_edad   CHECK (ds3 BETWEEN 12 AND 65),
  CONSTRAINT chk_enc_ponde  CHECK (ponde_ss > 0),
  CONSTRAINT chk_enc_tb02   CHECK (tb02 IN (1, 2, 3) OR tb02 IS NULL)
) ENGINE=InnoDB;


-- ────────────────────────────────
-- ENSANUT 2018 — Residentes
-- ────────────────────────────────

CREATE TABLE ensanut_2018_residentes (
  id              INT NOT NULL AUTO_INCREMENT,
  upm             SMALLINT NOT NULL,
  viv_sel         SMALLINT NOT NULL,
  hogar           TINYINT NOT NULL,
  numren          TINYINT NOT NULL,
  sexo            TINYINT NOT NULL,
  edad            TINYINT NOT NULL,
  cve_entidad     TINYINT NOT NULL,
  nivel_educativo TINYINT DEFAULT NULL,
  factor          INT NOT NULL,
  created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

  PRIMARY KEY (id),
  UNIQUE KEY uq_res18_clave (upm, viv_sel, hogar, numren),
  KEY idx_res18_ent (cve_entidad),

  CONSTRAINT fk_res18_ent FOREIGN KEY (cve_entidad) REFERENCES cat_entidades(cve_entidad) ON DELETE RESTRICT ON UPDATE CASCADE,

  CONSTRAINT chk_res18_sexo   CHECK (sexo IN (1, 2)),
  CONSTRAINT chk_res18_edad   CHECK (edad >= 0),
  CONSTRAINT chk_res18_factor CHECK (factor > 0)
) ENGINE=InnoDB;


-- ────────────────────────────────
-- ENSANUT 2018 — Adultos
-- ────────────────────────────────

CREATE TABLE encuesta_ensanut_2018 (
  id               INT NOT NULL AUTO_INCREMENT,
  upm              SMALLINT NOT NULL,
  viv_sel          SMALLINT NOT NULL,
  hogar            TINYINT NOT NULL,
  numren           TINYINT NOT NULL,
  sexo             TINYINT NOT NULL,
  edad             TINYINT NOT NULL,
  cve_entidad      TINYINT NOT NULL,
  factor_expansion INT NOT NULL,

  -- Tabaco (P13)
  p13_1   TINYINT DEFAULT NULL,
  p13_2   TINYINT DEFAULT NULL,
  p13_3   TINYINT DEFAULT NULL,
  p13_4   TINYINT DEFAULT NULL,
  p13_5   SMALLINT DEFAULT NULL,
  p13_6   TINYINT DEFAULT NULL,
  p13_6_1 TINYINT DEFAULT NULL,
  p13_7_1 TINYINT DEFAULT NULL,
  p13_7_2 TINYINT DEFAULT NULL,
  p13_8   TINYINT DEFAULT NULL,
  p13_8_1 TINYINT DEFAULT NULL,
  p13_9   TINYINT DEFAULT NULL,
  p13_10  TINYINT DEFAULT NULL,

  -- Alcohol
  p13_11   TINYINT DEFAULT NULL,
  p13_12_1 TINYINT DEFAULT NULL,
  p13_12_2 TINYINT DEFAULT NULL,
  p13_13m  TINYINT DEFAULT NULL,
  p13_13a  TINYINT DEFAULT NULL,
  p13_14   TINYINT DEFAULT NULL,

  -- CES-D (7 ítems)
  p2_1_1 TINYINT DEFAULT NULL,
  p2_1_2 TINYINT DEFAULT NULL,
  p2_1_3 TINYINT DEFAULT NULL,
  p2_1_4 TINYINT DEFAULT NULL,
  p2_1_5 TINYINT DEFAULT NULL,
  p2_1_6 TINYINT DEFAULT NULL,
  p2_1_7 TINYINT DEFAULT NULL,

  -- Suicidio / Autolesión
  p12_7   TINYINT DEFAULT NULL,
  p12_7_1 TINYINT DEFAULT NULL,
  p12_8   TINYINT DEFAULT NULL,
  p12_8_1 TINYINT DEFAULT NULL,

  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

  PRIMARY KEY (id),
  UNIQUE KEY uq_ens18_clave (upm, viv_sel, hogar, numren),
  KEY idx_ens18_ent (cve_entidad),
  KEY idx_ens18_ent_sexo (cve_entidad, sexo),
  KEY idx_ens18_fuma (p13_2),

  CONSTRAINT fk_ens18_ent    FOREIGN KEY (cve_entidad) REFERENCES cat_entidades(cve_entidad) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT fk_ens18_res18  FOREIGN KEY (upm, viv_sel, hogar, numren) REFERENCES ensanut_2018_residentes(upm, viv_sel, hogar, numren) ON DELETE RESTRICT ON UPDATE CASCADE,

  CONSTRAINT chk_ens18_sexo   CHECK (sexo IN (1, 2)),
  CONSTRAINT chk_ens18_edad   CHECK (edad >= 20),
  CONSTRAINT chk_ens18_factor CHECK (factor_expansion > 0)
) ENGINE=InnoDB;


-- ────────────────────────────────
-- ENSANUT 2023 — Residentes
-- ────────────────────────────────

CREATE TABLE ensanut_2023_residentes (
  id              INT NOT NULL AUTO_INCREMENT,
  folio_i         VARCHAR(20) NOT NULL,
  folio_int       VARCHAR(25) NOT NULL,
  cve_entidad     TINYINT NOT NULL,
  sexo            TINYINT NOT NULL,
  edad            TINYINT NOT NULL,
  anio_nacimiento SMALLINT NOT NULL,
  parentesco      TINYINT DEFAULT NULL,
  ponde_f         DECIMAL(18,10) NOT NULL,
  created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

  PRIMARY KEY (id),
  UNIQUE KEY uq_res23_clave (folio_i, folio_int),
  KEY idx_res23_ent (cve_entidad),
  KEY idx_res23_hogar (folio_i),

  CONSTRAINT fk_res23_ent FOREIGN KEY (cve_entidad) REFERENCES cat_entidades(cve_entidad) ON DELETE RESTRICT ON UPDATE CASCADE,

  CONSTRAINT chk_res23_sexo   CHECK (sexo IN (1, 2)),
  CONSTRAINT chk_res23_edad   CHECK (edad >= 0),
  CONSTRAINT chk_res23_ponde  CHECK (ponde_f > 0)
) ENGINE=InnoDB;


-- ────────────────────────────────
-- ENSANUT 2023 — Adultos
-- ────────────────────────────────

CREATE TABLE encuesta_ensanut_2023 (
  id          INT NOT NULL AUTO_INCREMENT,
  folio_i     VARCHAR(20) NOT NULL,
  folio_int   VARCHAR(25) NOT NULL,
  sexo        TINYINT NOT NULL,
  edad        TINYINT NOT NULL,
  cve_entidad TINYINT NOT NULL,
  ponde_f     DECIMAL(18,10) NOT NULL,

  -- Tabaco (a13xx)
  a1301  TINYINT DEFAULT NULL,
  a1302  TINYINT DEFAULT NULL,
  a1303  TINYINT DEFAULT NULL,
  a1304  TINYINT DEFAULT NULL,
  a1305  TINYINT DEFAULT NULL,
  a1306p SMALLINT DEFAULT NULL,
  a1306t TINYINT DEFAULT NULL,
  a1307  TINYINT DEFAULT NULL,

  -- Alcohol
  a1308 TINYINT DEFAULT NULL,
  a1309 TINYINT DEFAULT NULL,
  a1310 TINYINT DEFAULT NULL,
  a1311 TINYINT DEFAULT NULL,
  a1312 TINYINT DEFAULT NULL,
  a1313 TINYINT DEFAULT NULL,
  a1314 TINYINT DEFAULT NULL,

  -- CES-D (7 ítems)
  a0211 TINYINT DEFAULT NULL,
  a0212 TINYINT DEFAULT NULL,
  a0213 TINYINT DEFAULT NULL,
  a0214 TINYINT DEFAULT NULL,
  a0215 TINYINT DEFAULT NULL,
  a0216 TINYINT DEFAULT NULL,
  a0217 TINYINT DEFAULT NULL,

  -- Solo 2023
  a0202 TINYINT DEFAULT NULL,
  a0205 TINYINT DEFAULT NULL,

  -- Suicidio / Autolesión
  a1211 TINYINT DEFAULT NULL,
  a1213 TINYINT DEFAULT NULL,

  -- Drogas ilícitas (exclusivo 2023)
  a13a1  TINYINT DEFAULT NULL,
  a13a3a TINYINT DEFAULT NULL,
  a13a3b TINYINT DEFAULT NULL,
  a13a3c TINYINT DEFAULT NULL,
  a13a3d TINYINT DEFAULT NULL,
  a13a7a TINYINT DEFAULT NULL,
  a13a8a TINYINT DEFAULT NULL,
  a13a8b TINYINT DEFAULT NULL,
  a13a8c TINYINT DEFAULT NULL,
  a13a8d TINYINT DEFAULT NULL,
  a13a8e TINYINT DEFAULT NULL,
  a13a8f TINYINT DEFAULT NULL,
  a13a8g TINYINT DEFAULT NULL,
  a13a8h TINYINT DEFAULT NULL,
  a13a8i TINYINT DEFAULT NULL,

  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

  PRIMARY KEY (id),
  UNIQUE KEY uq_ens23_clave (folio_i, folio_int),
  KEY idx_ens23_ent (cve_entidad),
  KEY idx_ens23_ent_sexo (cve_entidad, sexo),
  KEY idx_ens23_fuma (a1301),
  KEY idx_ens23_alcohol (a1308),

  CONSTRAINT fk_ens23_ent    FOREIGN KEY (cve_entidad) REFERENCES cat_entidades(cve_entidad) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT fk_ens23_res23  FOREIGN KEY (folio_i, folio_int) REFERENCES ensanut_2023_residentes(folio_i, folio_int) ON DELETE RESTRICT ON UPDATE CASCADE,

  CONSTRAINT chk_ens23_sexo  CHECK (sexo IN (1, 2)),
  CONSTRAINT chk_ens23_edad  CHECK (edad BETWEEN 20 AND 120),
  CONSTRAINT chk_ens23_ponde CHECK (ponde_f > 0)
) ENGINE=InnoDB;


-- ================================================================
--  E T A P A   3  —  C A P A   D E   A P L I C A C I Ó N  (7 tablas)
-- ================================================================

-- ────────────────────────────────
-- Usuarios
-- ────────────────────────────────

CREATE TABLE usuarios (
  id              INT NOT NULL AUTO_INCREMENT,
  firebase_uid    VARCHAR(128) NOT NULL,
  email           VARCHAR(255) NOT NULL,
  nombre_completo VARCHAR(200) NOT NULL,
  cargo           VARCHAR(100) DEFAULT NULL,
  institucion     VARCHAR(200) DEFAULT NULL,
  rol             ENUM('admin', 'analista', 'visualizador') NOT NULL DEFAULT 'visualizador',
  entidad_id      TINYINT DEFAULT NULL,
  activo          TINYINT NOT NULL DEFAULT 1,
  ultimo_acceso   TIMESTAMP NULL DEFAULT NULL,
  created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

  PRIMARY KEY (id),
  UNIQUE KEY uq_usuario_firebase (firebase_uid),
  UNIQUE KEY uq_usuario_email (email),
  KEY idx_usuario_rol (rol),
  KEY idx_usuario_entidad (entidad_id),

  CONSTRAINT fk_usr_ent FOREIGN KEY (entidad_id) REFERENCES cat_entidades(cve_entidad) ON DELETE RESTRICT ON UPDATE CASCADE,

  CONSTRAINT chk_usr_activo CHECK (activo IN (0, 1))
) ENGINE=InnoDB;


-- ────────────────────────────────
-- Audit log (append-only)
-- ────────────────────────────────

CREATE TABLE audit_log (
  id                BIGINT NOT NULL AUTO_INCREMENT,
  usuario_id        INT DEFAULT NULL,
  accion            VARCHAR(50) NOT NULL,
  entidad_afectada  VARCHAR(50) DEFAULT NULL,
  registro_id       VARCHAR(50) DEFAULT NULL,
  detalle           JSON DEFAULT NULL,
  ip_address        VARCHAR(45) DEFAULT NULL,
  created_at        TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

  PRIMARY KEY (id),
  KEY idx_audit_usuario (usuario_id),
  KEY idx_audit_accion (accion),
  KEY idx_audit_fecha (created_at),
  KEY idx_audit_usuario_fecha (usuario_id, created_at)

  -- FK LÓGICA: usuario_id → usuarios.id
  -- Sin constraint físico: append-only no debe bloquear operaciones
) ENGINE=InnoDB;


-- ────────────────────────────────
-- Fuentes de datos (metadata Panel 5)
-- ────────────────────────────────

CREATE TABLE fuentes_datos (
  id               SMALLINT NOT NULL AUTO_INCREMENT,
  nombre           VARCHAR(150) NOT NULL,
  siglas           VARCHAR(30) NOT NULL,
  institucion      VARCHAR(150) NOT NULL,
  anio_publicacion SMALLINT NOT NULL,
  total_registros  INT DEFAULT NULL,
  descripcion      TEXT DEFAULT NULL,
  url_origen       VARCHAR(500) DEFAULT NULL,
  activa           TINYINT NOT NULL DEFAULT 1,
  created_at       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

  PRIMARY KEY (id),
  KEY idx_fuente_siglas (siglas),

  CONSTRAINT chk_fuente_activa CHECK (activa IN (0, 1))
) ENGINE=InnoDB;


-- ────────────────────────────────
-- Cargas de datos (ETL tracking)
-- ────────────────────────────────

CREATE TABLE cargas_datos (
  id                    INT NOT NULL AUTO_INCREMENT,
  usuario_id            INT NOT NULL,
  fuente_id             SMALLINT DEFAULT NULL,
  nombre_archivo        VARCHAR(255) NOT NULL,
  tamano_bytes          BIGINT NOT NULL,
  formato               VARCHAR(10) NOT NULL,
  total_registros       INT DEFAULT NULL,
  registros_insertados  INT DEFAULT NULL,
  registros_rechazados  INT DEFAULT NULL,
  estado                ENUM('pendiente', 'procesando', 'completado', 'error') NOT NULL DEFAULT 'pendiente',
  errores               JSON DEFAULT NULL,
  url_archivo           VARCHAR(500) DEFAULT NULL,
  created_at            TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  completed_at          TIMESTAMP NULL DEFAULT NULL,

  PRIMARY KEY (id),
  KEY idx_carga_usuario (usuario_id),
  KEY idx_carga_estado (estado),
  KEY idx_carga_fecha (created_at),

  CONSTRAINT fk_carga_usr    FOREIGN KEY (usuario_id) REFERENCES usuarios(id)      ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT fk_carga_fuente FOREIGN KEY (fuente_id)  REFERENCES fuentes_datos(id)  ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB;


-- ────────────────────────────────
-- Reportes generados
-- ────────────────────────────────

CREATE TABLE reportes_generados (
  id                INT NOT NULL AUTO_INCREMENT,
  usuario_id        INT NOT NULL,
  tipo_reporte      ENUM('resumen_nacional', 'mapa_estatal', 'mortalidad_urgencias', 'red_atencion', 'fuentes_datos', 'tendencias', 'salud_mental') NOT NULL,
  formato           ENUM('pdf', 'excel', 'csv') NOT NULL DEFAULT 'pdf',
  filtros_aplicados JSON DEFAULT NULL,
  nombre_archivo    VARCHAR(255) NOT NULL,
  url_archivo       VARCHAR(500) DEFAULT NULL,
  tamano_bytes      INT DEFAULT NULL,
  created_at        TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

  PRIMARY KEY (id),
  KEY idx_reporte_usuario (usuario_id),
  KEY idx_reporte_fecha (created_at),
  KEY idx_reporte_tipo (tipo_reporte),

  CONSTRAINT fk_reporte_usr FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB;


-- ────────────────────────────────
-- Configuración (key-value)
-- ────────────────────────────────

CREATE TABLE configuracion (
  id          SMALLINT NOT NULL AUTO_INCREMENT,
  clave       VARCHAR(100) NOT NULL,
  valor       TEXT NOT NULL,
  tipo        ENUM('string', 'number', 'json', 'boolean') NOT NULL DEFAULT 'string',
  descripcion VARCHAR(255) DEFAULT NULL,
  modificable TINYINT NOT NULL DEFAULT 1,
  updated_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  updated_by  INT DEFAULT NULL,

  PRIMARY KEY (id),
  UNIQUE KEY uq_config_clave (clave),

  -- FK LÓGICA: updated_by → usuarios.id (sin constraint físico)
  CONSTRAINT chk_config_mod CHECK (modificable IN (0, 1))
) ENGINE=InnoDB;


-- ────────────────────────────────
-- Anotaciones (valor agregado B2G)
-- ────────────────────────────────

CREATE TABLE anotaciones (
  id               INT NOT NULL AUTO_INCREMENT,
  usuario_id       INT NOT NULL,
  panel            VARCHAR(50) NOT NULL,
  filtros_contexto JSON DEFAULT NULL,
  texto            TEXT NOT NULL,
  activa           TINYINT NOT NULL DEFAULT 1,
  created_at       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

  PRIMARY KEY (id),
  KEY idx_anot_panel_activa (panel, activa),
  KEY idx_anot_usuario (usuario_id),

  CONSTRAINT fk_anot_usr FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE RESTRICT ON UPDATE CASCADE,

  CONSTRAINT chk_anot_activa CHECK (activa IN (0, 1))
) ENGINE=InnoDB;


-- ================================================================
--  E T A P A   4  —  V I S T A S   U N I F I C A D A S  (5 vistas)
-- ================================================================

-- ────────────────────────────────
-- Vista 1: Tabaco armonizado multi-año
-- Uso: Panel 6 (Tendencias)
-- ────────────────────────────────

CREATE OR REPLACE VIEW v_encuesta_tabaco AS

SELECT
  2016 AS anio_encuesta,
  id_pers AS clave_individual,
  cve_entidad,
  cve_sexo,
  ds3 AS edad,
  ponde_ss AS factor_expansion,
  CASE WHEN tb02 IN (1, 2) THEN 1 ELSE 0 END AS fuma_actualmente,
  tb06 AS edad_inicio_tabaco,
  COALESCE(tb07, tb08) AS cigarros_dia,
  CASE WHEN tg2 = 1 THEN 1 ELSE 0 END AS usa_vapeo,
  'ENCODAT' AS fuente
FROM encuesta_encodat

UNION ALL

SELECT
  2018 AS anio_encuesta,
  CONCAT(upm, '-', viv_sel, '-', hogar, '-', numren) AS clave_individual,
  cve_entidad,
  sexo AS cve_sexo,
  edad,
  factor_expansion,
  CASE WHEN p13_2 IN (1, 2) THEN 1 ELSE 0 END AS fuma_actualmente,
  p13_5 AS edad_inicio_tabaco,
  p13_6 AS cigarros_dia,
  CASE WHEN p13_9 = 1 THEN 1 ELSE 0 END AS usa_vapeo,
  'ENSANUT_2018' AS fuente
FROM encuesta_ensanut_2018

UNION ALL

SELECT
  2023 AS anio_encuesta,
  folio_int AS clave_individual,
  cve_entidad,
  sexo AS cve_sexo,
  edad,
  ponde_f AS factor_expansion,
  CASE WHEN a1301 IN (1, 2) THEN 1 ELSE 0 END AS fuma_actualmente,
  a1302 AS edad_inicio_tabaco,
  a1303 AS cigarros_dia,
  CASE WHEN a1307 = 1 THEN 1 ELSE 0 END AS usa_vapeo,
  'ENSANUT_2023' AS fuente
FROM encuesta_ensanut_2023;


-- ────────────────────────────────
-- Vista 2: Prevalencia estatal
-- Uso: Panel 2 (Mapa Epidemiológico)
-- ────────────────────────────────

CREATE OR REPLACE VIEW v_prevalencia_estatal AS

SELECT
  e.cve_entidad,
  ce.nombre AS entidad,
  ce.abreviatura,
  SUM(CASE WHEN e.tb02 IN (1, 2) THEN e.ponde_ss ELSE 0 END) AS fumadores_estimados,
  p.pob_total,
  ROUND(
    SUM(CASE WHEN e.tb02 IN (1, 2) THEN e.ponde_ss ELSE 0 END)
    / NULLIF(p.pob_total, 0) * 100000, 2
  ) AS tasa_100k,
  ROUND(
    SUM(CASE WHEN e.tb02 IN (1, 2) THEN e.ponde_ss ELSE 0 END)
    / NULLIF(SUM(e.ponde_ss), 0) * 100, 2
  ) AS prev_pct
FROM encuesta_encodat e
JOIN cat_entidades ce ON e.cve_entidad = ce.cve_entidad
LEFT JOIN (
  SELECT cve_entidad, SUM(pob_total) AS pob_total
  FROM pob_proyecciones
  WHERE anio = 2016
  GROUP BY cve_entidad
) p ON e.cve_entidad = p.cve_entidad
WHERE e.cve_entidad BETWEEN 1 AND 32
GROUP BY e.cve_entidad, ce.nombre, ce.abreviatura, p.pob_total;


-- ────────────────────────────────
-- Vista 3: Mortalidad anual por sustancia
-- Uso: Panel 3 (Mortalidad y Urgencias)
-- ────────────────────────────────

CREATE OR REPLACE VIEW v_mortalidad_anual AS

SELECT
  d.anio,
  d.cve_entidad,
  ce.nombre AS entidad,
  ce.abreviatura,
  SUM(d.f10) AS f10_alcohol,
  SUM(d.f11) AS f11_opioides,
  SUM(d.f12) AS f12_cannabis,
  SUM(d.f14) AS f14_cocaina,
  SUM(d.f15) AS f15_estimulantes,
  SUM(d.f17) AS f17_tabaco,
  SUM(d.f18) AS f18_inhalantes,
  SUM(d.f19) AS f19_multiples,
  SUM(d.f10 + d.f11 + d.f12 + d.f14 + d.f15 + d.f17 + d.f18 + d.f19) AS total_defunciones
FROM defunciones d
JOIN cat_entidades ce ON d.cve_entidad = ce.cve_entidad
GROUP BY d.anio, d.cve_entidad, ce.nombre, ce.abreviatura;


-- ────────────────────────────────
-- Vista 4: Salud mental armonizada 2018↔2023
-- Uso: Panel 7 (Salud Mental)
-- ────────────────────────────────

CREATE OR REPLACE VIEW v_salud_mental AS

SELECT
  2018 AS anio,
  cve_entidad,
  sexo AS cve_sexo,
  edad,
  factor_expansion,
  (COALESCE(p2_1_1, 0) + COALESCE(p2_1_2, 0) + COALESCE(p2_1_3, 0) +
   COALESCE(p2_1_4, 0) + COALESCE(p2_1_5, 0) + COALESCE(p2_1_6, 0) +
   COALESCE(p2_1_7, 0)) AS cesd_score,
  p12_7 AS ideacion_suicida,
  p12_8 AS autolesion,
  p13_11 AS consume_alcohol,
  p13_14 AS binge_drinking,
  'ENSANUT_2018' AS fuente
FROM encuesta_ensanut_2018

UNION ALL

SELECT
  2023 AS anio,
  cve_entidad,
  sexo AS cve_sexo,
  edad,
  ponde_f AS factor_expansion,
  (COALESCE(a0211, 0) + COALESCE(a0212, 0) + COALESCE(a0213, 0) +
   COALESCE(a0214, 0) + COALESCE(a0215, 0) + COALESCE(a0216, 0) +
   COALESCE(a0217, 0)) AS cesd_score,
  a1211 AS ideacion_suicida,
  a1213 AS autolesion,
  a1308 AS consume_alcohol,
  CASE
    WHEN sexo = 1 THEN a1311
    WHEN sexo = 2 THEN a1312
  END AS binge_drinking,
  'ENSANUT_2023' AS fuente
FROM encuesta_ensanut_2023;


-- ────────────────────────────────
-- Vista 5: Clínicas por entidad
-- Uso: Panel 4 (Red de Atención)
-- ────────────────────────────────

CREATE OR REPLACE VIEW v_clinicas_por_entidad AS

SELECT
  c.cve_entidad,
  ce.nombre AS nombre_entidad,
  ce.abreviatura,
  COUNT(*) AS total_clinicas,
  GROUP_CONCAT(c.nombre ORDER BY c.nombre SEPARATOR '; ') AS lista_clinicas
FROM clinicas c
JOIN cat_entidades ce ON c.cve_entidad = ce.cve_entidad
GROUP BY c.cve_entidad, ce.nombre, ce.abreviatura;


-- ================================================================
--  E T A P A   5  —  D A T O S   I N I C I A L E S
-- ================================================================

-- ────────────────────────────────
-- cat_entidades (34 filas)
-- ────────────────────────────────

INSERT INTO cat_entidades (cve_entidad, nombre, abreviatura) VALUES
(1,  'Aguascalientes',        'AGS'),
(2,  'Baja California',       'BC'),
(3,  'Baja California Sur',   'BCS'),
(4,  'Campeche',              'CAM'),
(5,  'Coahuila',              'COAH'),
(6,  'Colima',                'COL'),
(7,  'Chiapas',               'CHIS'),
(8,  'Chihuahua',             'CHIH'),
(9,  'Ciudad de México',      'CDMX'),
(10, 'Durango',               'DGO'),
(11, 'Guanajuato',            'GTO'),
(12, 'Guerrero',              'GRO'),
(13, 'Hidalgo',               'HGO'),
(14, 'Jalisco',               'JAL'),
(15, 'México',                'MEX'),
(16, 'Michoacán',             'MICH'),
(17, 'Morelos',               'MOR'),
(18, 'Nayarit',               'NAY'),
(19, 'Nuevo León',            'NL'),
(20, 'Oaxaca',                'OAX'),
(21, 'Puebla',                'PUE'),
(22, 'Querétaro',             'QRO'),
(23, 'Quintana Roo',          'QROO'),
(24, 'San Luis Potosí',       'SLP'),
(25, 'Sinaloa',               'SIN'),
(26, 'Sonora',                'SON'),
(27, 'Tabasco',               'TAB'),
(28, 'Tamaulipas',            'TAMPS'),
(29, 'Tlaxcala',              'TLAX'),
(30, 'Veracruz',              'VER'),
(31, 'Yucatán',               'YUC'),
(32, 'Zacatecas',             'ZAC'),
(33, 'No Especificado',       'NE'),
(34, 'Hospitales Federales',  'HF');


-- ────────────────────────────────
-- cat_sexo (5 filas)
-- ────────────────────────────────

INSERT INTO cat_sexo (cve_sexo, descripcion) VALUES
(1, 'Hombre'),
(2, 'Mujer'),
(3, 'Intersexual'),
(4, 'No Especificado'),
(5, 'Se Ignora');


-- ────────────────────────────────
-- cat_grupos_edad (6 filas — ENCODAT)
-- ────────────────────────────────

INSERT INTO cat_grupos_edad (cve_grupo, etiqueta, edad_min, edad_max) VALUES
(1, '12-17', 12, 17),
(2, '18-24', 18, 24),
(3, '25-34', 25, 34),
(4, '35-44', 35, 44),
(5, '45-54', 45, 54),
(6, '55-65', 55, 65);


-- ────────────────────────────────
-- cat_grupos_edad_quinquenal (21 filas — INEGI/CONAPO)
-- ────────────────────────────────

INSERT INTO cat_grupos_edad_quinquenal (cve_grupo, etiqueta, edad_min, edad_max) VALUES
(1,  'Menores de 1 año', 0,    0),
(2,  '0 a 4 años',       0,    4),
(3,  '5 a 9 años',       5,    9),
(4,  '10 a 14 años',     10,   14),
(5,  '15 a 19 años',     15,   19),
(6,  '20 a 24 años',     20,   24),
(7,  '25 a 29 años',     25,   29),
(8,  '30 a 34 años',     30,   34),
(9,  '35 a 39 años',     35,   39),
(10, '40 a 44 años',     40,   44),
(11, '45 a 49 años',     45,   49),
(12, '50 a 54 años',     50,   54),
(13, '55 a 59 años',     55,   59),
(14, '60 a 64 años',     60,   64),
(15, '65 a 69 años',     65,   69),
(16, '70 a 74 años',     70,   74),
(17, '75 a 79 años',     75,   79),
(18, '80 a 84 años',     80,   84),
(19, '85 y más',         85,   NULL),
(20, 'No especificado',  NULL, NULL),
(21, '1 a 4 años',       1,    4);


-- ────────────────────────────────
-- cat_niveles_educativos (14 filas — INEGI)
-- ────────────────────────────────

INSERT INTO cat_niveles_educativos (cve_nivel, descripcion) VALUES
(0,  'Sin escolaridad'),
(1,  'Preescolar'),
(2,  'Primaria incompleta'),
(3,  'Primaria completa'),
(4,  'Secundaria incompleta'),
(5,  'Secundaria completa'),
(6,  'Preparatoria incompleta'),
(7,  'Preparatoria completa'),
(8,  'Normal básica'),
(9,  'Estudios técnicos'),
(10, 'Licenciatura'),
(11, 'Posgrado'),
(12, 'No especificado'),
(13, 'No sabe');


-- ────────────────────────────────
-- cat_categorias_fumador (5 filas)
-- ────────────────────────────────

INSERT INTO cat_categorias_fumador (cve_categoria, descripcion) VALUES
(1, 'Nunca fumó'),
(2, 'Ex-fumador'),
(3, 'Fumador ocasional'),
(4, 'Fumador diario'),
(5, 'Frecuencia desconocida');


-- ────────────────────────────────
-- cat_cie10 (10 filas — F10 a F19)
-- ────────────────────────────────

INSERT INTO cat_cie10 (cve, codigo, trastorno, grupo) VALUES
(1,  'F10', 'Trastornos por uso de alcohol',           'Alcohol'),
(2,  'F11', 'Trastornos por uso de opioides',          'Opioides'),
(3,  'F12', 'Trastornos por uso de cannabinoides',     'Cannabis'),
(4,  'F13', 'Trastornos por uso de sedantes',          'Sedantes'),
(5,  'F14', 'Trastornos por uso de cocaína',           'Cocaína'),
(6,  'F15', 'Trastornos por uso de estimulantes',      'Estimulantes'),
(7,  'F16', 'Trastornos por uso de alucinógenos',      'Alucinógenos'),
(8,  'F17', 'Trastornos por uso de tabaco',            'Tabaco'),
(9,  'F18', 'Trastornos por uso de disolventes',       'Inhalantes'),
(10, 'F19', 'Trastornos por uso de múltiples drogas',  'Múltiples');


-- ────────────────────────────────
-- fuentes_datos (7 filas — Panel 5)
-- ────────────────────────────────

INSERT INTO fuentes_datos (nombre, siglas, institucion, anio_publicacion, total_registros, descripcion, url_origen) VALUES
('ENCODAT 2016-2017',        'ENCODAT',   'Instituto Nacional de Psiquiatría Ramón de la Fuente Muñiz', 2017, 56877,  'Encuesta Nacional de Consumo de Drogas, Alcohol y Tabaco. Cobertura: nacional, 12-65 años.', 'https://www.inprfm.gob.mx/'),
('ENSANUT 2018 Adultos',     'ENSANUT',   'Instituto Nacional de Salud Pública',                        2018, 43070,  'Encuesta Nacional de Salud y Nutrición — módulo adultos 20+ años.',                            'https://ensanut.insp.mx/'),
('ENSANUT 2018 Residentes',  'ENSANUT',   'Instituto Nacional de Salud Pública',                        2018, 158044, 'Residentes del hogar ENSANUT 2018. JOIN con adultos por (UPM, VIV_SEL, HOGAR, NUMREN).',       'https://ensanut.insp.mx/'),
('ENSANUT 2023 Adultos',     'ENSANUT',   'Instituto Nacional de Salud Pública',                        2023, 6772,   'Encuesta Nacional de Salud y Nutrición 2023 — módulo adultos. Muestra 84% menor que 2018.',    'https://ensanut.insp.mx/'),
('INEGI Defunciones F10-F19','INEGI',     'Instituto Nacional de Estadística y Geografía',               2023, 12473,  'Defunciones registradas por trastornos de sustancias (CIE-10 F10-F19), 2011-2023.',            'https://www.inegi.org.mx/'),
('INEGI Urgencias F10-F19',  'INEGI',     'Instituto Nacional de Estadística y Geografía',               2023, 24462,  'Atenciones de urgencias por trastornos de sustancias (CIE-10 F10-F19), 2011-2023.',            'https://www.inegi.org.mx/'),
('CONAPO Proyecciones',      'CONAPO',    'Consejo Nacional de Población',                               2018, 1280,   'Proyecciones de población a mitad de año, agregadas de municipal a estatal. 2011-2030.',        'https://www.gob.mx/conapo'),
('BeatQuest Clínicas',       'BEATQUEST', 'BeatQuest',                                                   2024, 166,    'Directorio de clínicas de atención a adicciones. 31 estados + 4 zonas CDMX.',                  NULL);


-- ────────────────────────────────
-- configuracion (6 filas iniciales)
-- ────────────────────────────────

INSERT INTO configuracion (clave, valor, tipo, descripcion, modificable) VALUES
('session_timeout_minutes',  '15',    'number',  'Minutos de inactividad para cierre automático de sesión (Plan de Pruebas)', 1),
('log_retention_days',       '365',   'number',  'Días de retención de registros en audit_log',                                1),
('max_upload_size_mb',       '100',   'number',  'Tamaño máximo de archivo para carga de datos (MB)',                          1),
('app_version',              '1.0.0', 'string',  'Versión actual del sistema',                                                 0),
('ground_truth_prevalencia', '13.23', 'number',  'Prevalencia nacional fumadores ENCODAT — validación ETL',                    0),
('ground_truth_def_f17',     '335',   'number',  'Defunciones F17 acumuladas 2011-2023 — validación ETL',                      0);


-- ================================================================
--  V A L I D A C I Ó N   P O S T - E J E C U C I Ó N
-- ================================================================

-- Ejecutar después de CREATE + INSERT para verificar:

-- SELECT 'cat_entidades'             t, COUNT(*) n FROM cat_entidades
-- UNION ALL SELECT 'cat_sexo',                 COUNT(*) FROM cat_sexo
-- UNION ALL SELECT 'cat_grupos_edad',          COUNT(*) FROM cat_grupos_edad
-- UNION ALL SELECT 'cat_grupos_edad_quinquenal',COUNT(*) FROM cat_grupos_edad_quinquenal
-- UNION ALL SELECT 'cat_niveles_educativos',   COUNT(*) FROM cat_niveles_educativos
-- UNION ALL SELECT 'cat_categorias_fumador',   COUNT(*) FROM cat_categorias_fumador
-- UNION ALL SELECT 'cat_cie10',                COUNT(*) FROM cat_cie10
-- UNION ALL SELECT 'fuentes_datos',            COUNT(*) FROM fuentes_datos
-- UNION ALL SELECT 'configuracion',            COUNT(*) FROM configuracion;
-- Esperado: 34, 5, 6, 21, 14, 5, 10, 8, 6

-- SHOW TABLES;
-- Esperado: 23 tablas

-- SELECT TABLE_NAME, TABLE_ROWS FROM information_schema.TABLES
-- WHERE TABLE_SCHEMA = 'intellecta' ORDER BY TABLE_NAME;
