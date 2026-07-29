-- Poliza Individual (1 riesgo)
INSERT INTO polizas (id, tipo, estado, fecha_inicio_vigencia, fecha_fin_vigencia, canon_mensual, prima, meses_vigencia)
VALUES (1, 'INDIVIDUAL', 'ACTIVA', '2026-01-01', '2026-12-31', 1500000.00, 18000000.00, 12);

INSERT INTO riesgos (id, poliza_id, descripcion, estado)
VALUES (1, 1, 'Arrendatario - inmueble Calle 80', 'ACTIVO');

-- Poliza Colectiva (2 riesgos)
INSERT INTO polizas (id, tipo, estado, fecha_inicio_vigencia, fecha_fin_vigencia, canon_mensual, prima, meses_vigencia)
VALUES (2, 'COLECTIVA', 'ACTIVA', '2026-02-01', '2027-01-31', 3200000.00, 38400000.00, 12);

INSERT INTO riesgos (id, poliza_id, descripcion, estado)
VALUES (2, 2, 'Arrendatario A - Edificio Norte Apto 501', 'ACTIVO');

INSERT INTO riesgos (id, poliza_id, descripcion, estado)
VALUES (3, 2, 'Arrendatario B - Edificio Norte Apto 502', 'ACTIVO');

-- Poliza Cancelada (para probar la regla de "no renovar cancelada")
INSERT INTO polizas (id, tipo, estado, fecha_inicio_vigencia, fecha_fin_vigencia, canon_mensual, prima, meses_vigencia)
VALUES (3, 'INDIVIDUAL', 'CANCELADA', '2025-06-01', '2026-05-31', 1200000.00, 14400000.00, 12);

ALTER TABLE polizas ALTER COLUMN id RESTART WITH 4;
ALTER TABLE riesgos ALTER COLUMN id RESTART WITH 4;
