-- ================================================================
-- AirTellecta — Validación de Ground Truths post-carga
-- Ejecutar después de LOAD DATA INFILE de los 9 CSVs.
-- Cada query debe retornar 'PASS'. Si retorna 'FAIL', la carga
-- tiene un problema de integridad.
-- ================================================================

USE intellecta;

-- ────────────────────────────────
-- GT-1: Filas pob_proyecciones = 832
-- ────────────────────────────────
SELECT IF(cnt = 832, 'PASS', CONCAT('FAIL: ', cnt)) AS gt1_pob_proyecciones
FROM (SELECT COUNT(*) AS cnt FROM pob_proyecciones) t;

-- ────────────────────────────────
-- GT-2: Filas defunciones ≈ 12,186
-- ────────────────────────────────
SELECT IF(cnt = 12186, 'PASS', CONCAT('FAIL: ', cnt)) AS gt2_defunciones_rows
FROM (SELECT COUNT(*) AS cnt FROM defunciones) t;

-- ────────────────────────────────
-- GT-3: SUM(F17) defunciones = 335
-- ────────────────────────────────
SELECT IF(total = 335, 'PASS', CONCAT('FAIL: ', total)) AS gt3_defunciones_f17
FROM (SELECT SUM(f17) AS total FROM defunciones) t;

-- ────────────────────────────────
-- GT-4: SUM(F17) urgencias = 878
--   (879 pre-filtro, 1 perdido en "No especificado")
-- ────────────────────────────────
SELECT IF(total = 878, 'PASS', CONCAT('FAIL: ', total)) AS gt4_urgencias_f17
FROM (SELECT SUM(f17) AS total FROM urgencias) t;

-- ────────────────────────────────
-- GT-5: Clínicas = 166
-- ────────────────────────────────
SELECT IF(cnt = 166, 'PASS', CONCAT('FAIL: ', cnt)) AS gt5_clinicas_total
FROM (SELECT COUNT(*) AS cnt FROM clinicas) t;

-- ────────────────────────────────
-- GT-6: Prevalencia fumadores ENCODAT ≈ 13.23%
-- ────────────────────────────────
SELECT IF(ABS(prev - 13.23) < 0.5, 'PASS', CONCAT('FAIL: ', prev, '%')) AS gt6_prevalencia
FROM (
  SELECT ROUND(
    SUM(CASE WHEN fumador_actual = 1 THEN ponde_ss ELSE 0 END)
    / SUM(ponde_ss) * 100, 2
  ) AS prev
  FROM encuesta_encodat
) t;

-- ────────────────────────────────
-- GT-7: Usuarios de vapeo = 174
-- ────────────────────────────────
SELECT IF(cnt = 174, 'PASS', CONCAT('FAIL: ', cnt)) AS gt7_vapeo
FROM (SELECT COUNT(*) AS cnt FROM encuesta_encodat WHERE usa_vape = 1) t;

-- ────────────────────────────────
-- GT-8: Uso dual = 101
-- ────────────────────────────────
SELECT IF(cnt = 101, 'PASS', CONCAT('FAIL: ', cnt)) AS gt8_uso_dual
FROM (SELECT COUNT(*) AS cnt FROM encuesta_encodat WHERE uso_dual = 1) t;

-- ────────────────────────────────
-- GT-9: Integridad FK ENSANUT 2018
--   Todos los adultos deben tener residente
-- ────────────────────────────────
SELECT IF(huerfanos = 0, 'PASS', CONCAT('FAIL: ', huerfanos, ' huérfanos')) AS gt9_fk_ensanut2018
FROM (
  SELECT COUNT(*) AS huerfanos
  FROM encuesta_ensanut_2018 a
  LEFT JOIN ensanut_2018_residentes r
    ON a.upm = r.upm AND a.viv_sel = r.viv_sel
    AND a.hogar = r.hogar AND a.numren = r.numren
  WHERE r.upm IS NULL
) t;

-- ────────────────────────────────
-- Resumen
-- ────────────────────────────────
SELECT '>>> Ejecutar las 9 queries arriba. Todas deben retornar PASS.' AS instruccion;
