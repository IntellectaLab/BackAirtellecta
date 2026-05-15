-- Fix encoding of state names with accented characters
-- Run this ONCE in MySQL: mysql -u root -p intellecta < fix_encoding_entidades.sql

SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;

UPDATE cat_entidades SET nombre = 'Ciudad de México'  WHERE cve_entidad = 9;
UPDATE cat_entidades SET nombre = 'México'             WHERE cve_entidad = 15;
UPDATE cat_entidades SET nombre = 'Michoacán'          WHERE cve_entidad = 16;
UPDATE cat_entidades SET nombre = 'Nuevo León'         WHERE cve_entidad = 19;
UPDATE cat_entidades SET nombre = 'Querétaro'          WHERE cve_entidad = 22;
UPDATE cat_entidades SET nombre = 'San Luis Potosí'    WHERE cve_entidad = 24;
UPDATE cat_entidades SET nombre = 'Yucatán'            WHERE cve_entidad = 31;

-- Verify
SELECT cve_entidad, nombre FROM cat_entidades ORDER BY cve_entidad;
