-- ================================================================
-- AirTellecta — Carga de datos desde CSVs limpios (etl/output/)
-- Ejecutar desde la raiz del proyecto:
--   mysql --local-infile=1 -u root -p intellecta < db/load_data.sql
-- ================================================================

USE intellecta;

SET GLOBAL local_infile = 1;

-- 1. Proyecciones de poblacion CONAPO (832 filas)
LOAD DATA LOCAL INFILE 'etl/output/pob_proyecciones.csv'
INTO TABLE pob_proyecciones
FIELDS TERMINATED BY ',' ENCLOSED BY '"'
LINES TERMINATED BY '\n'
IGNORE 1 ROWS
(cve_entidad, anio, sexo, pob_0_4, pob_5_9, pob_10_14, pob_15_19,
 pob_20_24, pob_25_29, pob_30_34, pob_35_39, pob_40_44, pob_45_49,
 pob_50_54, pob_55_59, pob_60_64, pob_65_69, pob_70_74, pob_75_79,
 pob_80_84, pob_85_mas);

-- 2. Defunciones INEGI (12,186 filas)
LOAD DATA LOCAL INFILE 'etl/output/defunciones.csv'
INTO TABLE defunciones
FIELDS TERMINATED BY ',' ENCLOSED BY '"'
LINES TERMINATED BY '\n'
IGNORE 1 ROWS
(anio, cve_entidad, cve_grupo_etario, cve_sexo,
 f10, f11, f12, f14, f15, f17, f18, f19);

-- 3. Urgencias INEGI (13,708 filas)
LOAD DATA LOCAL INFILE 'etl/output/urgencias.csv'
INTO TABLE urgencias
FIELDS TERMINATED BY ',' ENCLOSED BY '"'
LINES TERMINATED BY '\n'
IGNORE 1 ROWS
(anio, cve_entidad, cve_grupo_etario, cve_sexo,
 f10, f11, f12, f13, f14, f15, f16, f17, f18, f19);

-- 4. Clinicas BeatQuest (166 filas)
LOAD DATA LOCAL INFILE 'etl/output/clinicas.csv'
INTO TABLE clinicas
FIELDS TERMINATED BY ',' ENCLOSED BY '"'
LINES TERMINATED BY '\n'
IGNORE 1 ROWS
(cve_entidad, nombre, direccion, telefono, extension, delegacion_original);

-- 5. Encuesta ENCODAT 2016-2017 (56,877 filas)
LOAD DATA LOCAL INFILE 'etl/output/encuesta_encodat.csv'
INTO TABLE encuesta_encodat
FIELDS TERMINATED BY ',' ENCLOSED BY '"'
LINES TERMINATED BY '\n'
IGNORE 1 ROWS
(id_pers, cve_entidad, cve_sexo, ds3, ds8, cve_categoria_fumador,
 cve_grupo_edad, tb01, tb02, tb03, tb05, tb06, tb07, tb08, tb09,
 tb21, tb22, tb28, tb29, tb31, tb33, tb34, tb40, tb41, tb42, tb46,
 tg1, tg2, tg3, tg4, tg5, tp1, tp3a, tp3b, ponde_ss, usa_vape, uso_dual);

-- 6. ENSANUT 2018 residentes (158,044 filas)
LOAD DATA LOCAL INFILE 'etl/output/ensanut_2018_residentes.csv'
INTO TABLE ensanut_2018_residentes
FIELDS TERMINATED BY ',' ENCLOSED BY '"'
LINES TERMINATED BY '\n'
IGNORE 1 ROWS
(upm, viv_sel, hogar, numren, sexo, edad, cve_entidad, nivel_educativo, factor);

-- 7. ENSANUT 2018 adultos (43,070 filas)
LOAD DATA LOCAL INFILE 'etl/output/encuesta_ensanut_2018.csv'
INTO TABLE encuesta_ensanut_2018
FIELDS TERMINATED BY ',' ENCLOSED BY '"'
LINES TERMINATED BY '\n'
IGNORE 1 ROWS
(upm, viv_sel, hogar, numren, sexo, edad, cve_entidad, factor_expansion,
 p13_1, p13_2, p13_3, p13_4, p13_5, p13_6, p13_6_1, p13_7_1, p13_7_2,
 p13_8, p13_8_1, p13_9, p13_10, p13_11, p13_12_1, p13_12_2, p13_13m,
 p13_13a, p13_14, p2_1_1, p2_1_2, p2_1_3, p2_1_4, p2_1_5, p2_1_6,
 p2_1_7, p12_7, p12_7_1, p12_8, p12_8_1);

-- 8. ENSANUT 2023 residentes (20,018 filas)
LOAD DATA LOCAL INFILE 'etl/output/ensanut_2023_residentes.csv'
INTO TABLE ensanut_2023_residentes
FIELDS TERMINATED BY ',' ENCLOSED BY '"'
LINES TERMINATED BY '\n'
IGNORE 1 ROWS
(folio_i, folio_int, cve_entidad, sexo, edad, anio_nacimiento, parentesco, ponde_f);

-- 9. ENSANUT 2023 adultos (6,772 filas)
LOAD DATA LOCAL INFILE 'etl/output/encuesta_ensanut_2023.csv'
INTO TABLE encuesta_ensanut_2023
FIELDS TERMINATED BY ',' ENCLOSED BY '"'
LINES TERMINATED BY '\n'
IGNORE 1 ROWS
(folio_i, folio_int, sexo, edad, cve_entidad, ponde_f,
 a1301, a1302, a1303, a1304, a1305, a1306p, a1306t, a1307, a1308,
 a1309, a1310, a1311, a1312, a1313, a1314, a0211, a0212, a0213,
 a0214, a0215, a0216, a0217, a0202, a0205, a1211, a1213, a13a1,
 a13a3a, a13a3b, a13a3c, a13a3d, a13a7a, a13a8a, a13a8b, a13a8c,
 a13a8d, a13a8e, a13a8f, a13a8g, a13a8h, a13a8i);
