-- ================================================================
-- AirTellecta — Seed inicial para tabla `configuracion`
-- Ejecutar después del DDL y antes de levantar el backend.
-- ================================================================

USE intellecta;

INSERT INTO configuracion (clave, valor, tipo, descripcion, modificable) VALUES
-- Sesión y seguridad
('session_timeout_seconds',    '900',      'number', 'Timeout de sesión inactiva (15 min)',                  1),
('max_login_attempts',         '5',        'number', 'Intentos de login antes de bloqueo temporal',          1),
('lockout_duration_seconds',   '900',      'number', 'Duración del bloqueo por intentos fallidos (15 min)',  1),

-- Retención de datos
('audit_log_retention_days',   '365',      'number', 'Días de retención del audit_log',                     1),
('reportes_retention_days',    '90',       'number', 'Días de retención de reportes generados',              1),

-- Ground truths (referencia, no modificables)
('gt_prevalencia_fumadores',   '13.23',    'number', 'Prevalencia nacional fumadores ENCODAT (%)',           0),
('gt_poblacion_fumadores',     '11281008', 'number', 'Población estimada fumadores ENCODAT',                 0),
('gt_defunciones_f17',         '335',      'number', 'Defunciones F17 acumuladas 2011-2023',                 0),
('gt_urgencias_f17',           '879',      'number', 'Urgencias F17 acumuladas 2011-2023 (pre-filtro)',      0),
('gt_clinicas_total',          '166',      'number', 'Total clínicas en directorio BeatQuest',               0),
('gt_vapeo_registros',         '174',      'number', 'Registros usa_vape=1 ENCODAT',                        0),
('gt_uso_dual_registros',      '101',      'number', 'Registros uso_dual=1 ENCODAT',                        0),

-- Aplicación
('app_nombre',                 'AirTellecta', 'string',  'Nombre del producto',                              0),
('equipo_nombre',              'INTELLECTA',  'string',  'Nombre del equipo',                                 0),
('version_schema',             'v5',          'string',  'Versión del schema MySQL activo',                   0),
('encuestas_incluidas',        'ENCODAT,ENSANUT2018,ENSANUT2023', 'string', 'Encuestas cargadas',            0),
('rango_inegi',                '2011-2023',   'string',  'Rango temporal datos INEGI',                        0),

-- Dashboard
('dashboard_cache_ttl_seconds','300',      'number', 'TTL del caché de queries del dashboard (5 min)',       1),
('min_n_cruce',                '50',       'number', 'n mínimo para mostrar cruces estadísticos',            1),
('mostrar_intervalos_confianza','false',   'boolean', 'Mostrar IC en panel de tendencias',                    1);
