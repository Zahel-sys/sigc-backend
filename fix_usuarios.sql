USE sigc_db;
-- Primero, actualizar registros con fecha inválida
UPDATE usuarios SET fecha_registro = NOW() WHERE fecha_registro = '0000-00-00 00:00:00' OR fecha_registro IS NULL;
-- Si la columna no existe, no hay problema
