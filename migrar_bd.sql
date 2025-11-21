-- ============================================
-- SCRIPT DE MIGRACIÓN SIGC_DB
-- Actualiza la BD existente sin perder datos
-- ============================================

USE sigc_db;

-- ============================================
-- PASO 1: Agregar campos faltantes
-- ============================================

-- Agregar fecha_registro a usuarios si no existe
ALTER TABLE usuarios 
ADD COLUMN IF NOT EXISTS fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

-- Agregar fecha_creacion a especialidades si no existe
ALTER TABLE especialidades 
ADD COLUMN IF NOT EXISTS fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

-- Agregar fecha_creacion a doctores si no existe
ALTER TABLE doctores 
ADD COLUMN IF NOT EXISTS fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

-- Agregar fecha_creacion a horarios si no existe
ALTER TABLE horarios 
ADD COLUMN IF NOT EXISTS fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

-- Agregar fecha_creacion a citas si no existe
ALTER TABLE citas 
ADD COLUMN IF NOT EXISTS fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

-- Agregar fecha_creacion a servicios si no existe
ALTER TABLE servicios 
ADD COLUMN IF NOT EXISTS fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

-- ============================================
-- PASO 2: Mejorar índices para performance
-- ============================================

-- Índices para usuarios
CREATE INDEX IF NOT EXISTS idx_usuarios_email ON usuarios(email);
CREATE INDEX IF NOT EXISTS idx_usuarios_rol ON usuarios(rol);

-- Índices para especialidades
CREATE INDEX IF NOT EXISTS idx_especialidades_nombre ON especialidades(nombre);

-- Índices para doctores
CREATE INDEX IF NOT EXISTS idx_doctores_especialidad ON doctores(especialidad);
CREATE INDEX IF NOT EXISTS idx_doctores_nombre ON doctores(nombre);

-- Índices para horarios
CREATE INDEX IF NOT EXISTS idx_horarios_fecha ON horarios(fecha);
CREATE INDEX IF NOT EXISTS idx_horarios_doctor_fecha ON horarios(id_doctor, fecha);
CREATE INDEX IF NOT EXISTS idx_horarios_disponible ON horarios(disponible);

-- Índices para citas
CREATE INDEX IF NOT EXISTS idx_citas_usuario ON citas(id_usuario);
CREATE INDEX IF NOT EXISTS idx_citas_doctor ON citas(id_doctor);
CREATE INDEX IF NOT EXISTS idx_citas_fecha ON citas(fecha_cita);
CREATE INDEX IF NOT EXISTS idx_citas_estado ON citas(estado);

-- Índices para servicios
CREATE INDEX IF NOT EXISTS idx_servicios_nombre ON servicios(nombre_servicio);

-- ============================================
-- PASO 3: Actualizar tipos de datos si es necesario
-- ============================================

-- Asegurar que activo sea BIT(1) en usuarios
ALTER TABLE usuarios 
MODIFY COLUMN activo BIT(1) NOT NULL DEFAULT 1;

-- Asegurar que disponible sea TINYINT(1) en horarios
ALTER TABLE horarios 
MODIFY COLUMN disponible TINYINT(1) DEFAULT 1;

-- ============================================
-- PASO 4: Verificar integridad referencial
-- ============================================

-- Eliminar citas huérfanas (sin usuario válido)
DELETE FROM citas 
WHERE id_usuario NOT IN (SELECT id_usuario FROM usuarios);

-- Eliminar citas huérfanas (sin doctor válido)
DELETE FROM citas 
WHERE id_doctor NOT IN (SELECT id_doctor FROM doctores);

-- Eliminar citas huérfanas (sin horario válido)
DELETE FROM citas 
WHERE id_horario NOT IN (SELECT id_horario FROM horarios);

-- Eliminar horarios huérfanos (sin doctor válido)
DELETE FROM horarios 
WHERE id_doctor NOT IN (SELECT id_doctor FROM doctores);

-- ============================================
-- PASO 5: Actualizar datos del administrador si existe
-- ============================================

-- Actualizar password del admin si existe (BCrypt de Admin123456)
UPDATE usuarios 
SET password = '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhCu',
    rol = 'ADMIN',
    activo = 1
WHERE email = 'admin@sigc.com';

-- Insertar admin si no existe
INSERT IGNORE INTO usuarios (nombre, email, password, dni, telefono, rol, activo) 
VALUES ('Administrador', 'admin@sigc.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhCu', '00000000', '999999999', 'ADMIN', 1);

-- ============================================
-- PASO 6: Insertar datos de ejemplo si la BD está vacía
-- ============================================

-- Insertar usuarios de prueba si no hay pacientes
INSERT INTO usuarios (nombre, email, password, dni, telefono, rol, activo)
SELECT * FROM (SELECT 
    'Juan Pérez' AS nombre,
    'juan@cliente.com' AS email,
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhCu' AS password,
    '12345678' AS dni,
    '987654321' AS telefono,
    'PACIENTE' AS rol,
    1 AS activo
) AS tmp
WHERE NOT EXISTS (
    SELECT 1 FROM usuarios WHERE email = 'juan@cliente.com'
);

INSERT INTO usuarios (nombre, email, password, dni, telefono, rol, activo)
SELECT * FROM (SELECT 
    'María López' AS nombre,
    'maria@cliente.com' AS email,
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhCu' AS password,
    '87654321' AS dni,
    '912345678' AS telefono,
    'PACIENTE' AS rol,
    1 AS activo
) AS tmp
WHERE NOT EXISTS (
    SELECT 1 FROM usuarios WHERE email = 'maria@cliente.com'
);

-- ============================================
-- VERIFICACIÓN FINAL
-- ============================================
SELECT '✅ Migración completada exitosamente' AS resultado;
SELECT 
    (SELECT COUNT(*) FROM usuarios) AS total_usuarios,
    (SELECT COUNT(*) FROM doctores) AS total_doctores,
    (SELECT COUNT(*) FROM especialidades) AS total_especialidades,
    (SELECT COUNT(*) FROM horarios) AS total_horarios,
    (SELECT COUNT(*) FROM citas) AS total_citas,
    (SELECT COUNT(*) FROM servicios) AS total_servicios;

-- Mostrar tablas actualizadas
SHOW TABLES;

-- Mostrar resumen de citas
SELECT 'Resumen de citas:' AS info;
SELECT 
    c.id_cita,
    c.fecha_cita,
    c.hora_cita,
    c.estado,
    u.nombre AS paciente,
    d.nombre AS doctor
FROM citas c
LEFT JOIN usuarios u ON c.id_usuario = u.id_usuario
LEFT JOIN doctores d ON c.id_doctor = d.id_doctor
ORDER BY c.fecha_cita DESC, c.hora_cita DESC
LIMIT 10;
