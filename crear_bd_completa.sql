-- ============================================
-- SCRIPT COMPLETO SIGC - CREA Y ACTUALIZA TABLAS
-- Compatible con todas las versiones de MySQL
-- ============================================

-- Crear base de datos si no existe
CREATE DATABASE IF NOT EXISTS sigc_db CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE sigc_db;

-- ============================================
-- VERIFICACIÓN INICIAL
-- ============================================
SELECT 'Iniciando configuración de base de datos...' AS mensaje;

-- ============================================
-- CREAR TABLA: usuarios (si no existe)
-- ============================================
CREATE TABLE IF NOT EXISTS usuarios (
    id_usuario BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    dni VARCHAR(8) NOT NULL,
    telefono VARCHAR(9) NOT NULL,
    rol VARCHAR(50) NOT NULL DEFAULT 'PACIENTE',
    activo BIT(1) NOT NULL DEFAULT 1,
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Agregar índices en usuarios si no existen
SET @sql = (
    SELECT IF(
        COUNT(*) = 0,
        'CREATE INDEX idx_email ON usuarios(email);',
        'SELECT "El índice idx_email ya existe" AS info;'
    )
    FROM INFORMATION_SCHEMA.STATISTICS
    WHERE TABLE_SCHEMA = 'sigc_db'
    AND TABLE_NAME = 'usuarios'
    AND INDEX_NAME = 'idx_email'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = (
    SELECT IF(
        COUNT(*) = 0,
        'CREATE INDEX idx_rol ON usuarios(rol);',
        'SELECT "El índice idx_rol ya existe" AS info;'
    )
    FROM INFORMATION_SCHEMA.STATISTICS
    WHERE TABLE_SCHEMA = 'sigc_db'
    AND TABLE_NAME = 'usuarios'
    AND INDEX_NAME = 'idx_rol'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SELECT 'Tabla usuarios configurada ✓' AS mensaje;

-- ============================================
-- CREAR TABLA: especialidades (si no existe)
-- ============================================
CREATE TABLE IF NOT EXISTS especialidades (
    id_especialidad BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL UNIQUE,
    descripcion TEXT,
    imagen VARCHAR(255),
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Agregar índice en especialidades
SET @sql = (
    SELECT IF(
        COUNT(*) = 0,
        'CREATE INDEX idx_nombre ON especialidades(nombre);',
        'SELECT "El índice idx_nombre ya existe en especialidades" AS info;'
    )
    FROM INFORMATION_SCHEMA.STATISTICS
    WHERE TABLE_SCHEMA = 'sigc_db'
    AND TABLE_NAME = 'especialidades'
    AND INDEX_NAME = 'idx_nombre'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SELECT 'Tabla especialidades configurada ✓' AS mensaje;

-- ============================================
-- CREAR TABLA: doctores (si no existe)
-- ============================================
CREATE TABLE IF NOT EXISTS doctores (
    id_doctor INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    especialidad VARCHAR(255) NOT NULL,
    cupo_pacientes INT DEFAULT 10,
    imagen VARCHAR(255),
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Agregar índices en doctores
SET @sql = (
    SELECT IF(
        COUNT(*) = 0,
        'CREATE INDEX idx_especialidad ON doctores(especialidad);',
        'SELECT "El índice idx_especialidad ya existe" AS info;'
    )
    FROM INFORMATION_SCHEMA.STATISTICS
    WHERE TABLE_SCHEMA = 'sigc_db'
    AND TABLE_NAME = 'doctores'
    AND INDEX_NAME = 'idx_especialidad'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = (
    SELECT IF(
        COUNT(*) = 0,
        'CREATE INDEX idx_nombre ON doctores(nombre);',
        'SELECT "El índice idx_nombre ya existe en doctores" AS info;'
    )
    FROM INFORMATION_SCHEMA.STATISTICS
    WHERE TABLE_SCHEMA = 'sigc_db'
    AND TABLE_NAME = 'doctores'
    AND INDEX_NAME = 'idx_nombre'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SELECT 'Tabla doctores configurada ✓' AS mensaje;

-- ============================================
-- CREAR TABLA: horarios (si no existe)
-- ============================================
CREATE TABLE IF NOT EXISTS horarios (
    id_horario INT AUTO_INCREMENT PRIMARY KEY,
    fecha DATE NOT NULL,
    turno VARCHAR(255) NOT NULL,
    hora_inicio TIME NOT NULL,
    hora_fin TIME NOT NULL,
    disponible TINYINT(1) DEFAULT 1,
    id_doctor INT,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_doctor) REFERENCES doctores(id_doctor) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Agregar índices en horarios
SET @sql = (
    SELECT IF(
        COUNT(*) = 0,
        'CREATE INDEX idx_fecha ON horarios(fecha);',
        'SELECT "El índice idx_fecha ya existe en horarios" AS info;'
    )
    FROM INFORMATION_SCHEMA.STATISTICS
    WHERE TABLE_SCHEMA = 'sigc_db'
    AND TABLE_NAME = 'horarios'
    AND INDEX_NAME = 'idx_fecha'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = (
    SELECT IF(
        COUNT(*) = 0,
        'CREATE INDEX idx_doctor_fecha ON horarios(id_doctor, fecha);',
        'SELECT "El índice idx_doctor_fecha ya existe" AS info;'
    )
    FROM INFORMATION_SCHEMA.STATISTICS
    WHERE TABLE_SCHEMA = 'sigc_db'
    AND TABLE_NAME = 'horarios'
    AND INDEX_NAME = 'idx_doctor_fecha'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = (
    SELECT IF(
        COUNT(*) = 0,
        'CREATE INDEX idx_disponible ON horarios(disponible);',
        'SELECT "El índice idx_disponible ya existe" AS info;'
    )
    FROM INFORMATION_SCHEMA.STATISTICS
    WHERE TABLE_SCHEMA = 'sigc_db'
    AND TABLE_NAME = 'horarios'
    AND INDEX_NAME = 'idx_disponible'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SELECT 'Tabla horarios configurada ✓' AS mensaje;

-- ============================================
-- CREAR TABLA: citas (si no existe)
-- ============================================
CREATE TABLE IF NOT EXISTS citas (
    id_cita BIGINT AUTO_INCREMENT PRIMARY KEY,
    fecha_cita DATE NOT NULL,
    hora_cita TIME NOT NULL,
    turno VARCHAR(255),
    estado VARCHAR(255) DEFAULT 'ACTIVA',
    id_usuario BIGINT,
    id_doctor INT,
    id_horario INT,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario) ON DELETE CASCADE,
    FOREIGN KEY (id_doctor) REFERENCES doctores(id_doctor) ON DELETE CASCADE,
    FOREIGN KEY (id_horario) REFERENCES horarios(id_horario) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Agregar índices en citas
SET @sql = (
    SELECT IF(
        COUNT(*) = 0,
        'CREATE INDEX idx_usuario ON citas(id_usuario);',
        'SELECT "El índice idx_usuario ya existe en citas" AS info;'
    )
    FROM INFORMATION_SCHEMA.STATISTICS
    WHERE TABLE_SCHEMA = 'sigc_db'
    AND TABLE_NAME = 'citas'
    AND INDEX_NAME = 'idx_usuario'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = (
    SELECT IF(
        COUNT(*) = 0,
        'CREATE INDEX idx_doctor ON citas(id_doctor);',
        'SELECT "El índice idx_doctor ya existe en citas" AS info;'
    )
    FROM INFORMATION_SCHEMA.STATISTICS
    WHERE TABLE_SCHEMA = 'sigc_db'
    AND TABLE_NAME = 'citas'
    AND INDEX_NAME = 'idx_doctor'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = (
    SELECT IF(
        COUNT(*) = 0,
        'CREATE INDEX idx_fecha ON citas(fecha_cita);',
        'SELECT "El índice idx_fecha ya existe en citas" AS info;'
    )
    FROM INFORMATION_SCHEMA.STATISTICS
    WHERE TABLE_SCHEMA = 'sigc_db'
    AND TABLE_NAME = 'citas'
    AND INDEX_NAME = 'idx_fecha'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = (
    SELECT IF(
        COUNT(*) = 0,
        'CREATE INDEX idx_estado ON citas(estado);',
        'SELECT "El índice idx_estado ya existe" AS info;'
    )
    FROM INFORMATION_SCHEMA.STATISTICS
    WHERE TABLE_SCHEMA = 'sigc_db'
    AND TABLE_NAME = 'citas'
    AND INDEX_NAME = 'idx_estado'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SELECT 'Tabla citas configurada ✓' AS mensaje;

-- ============================================
-- CREAR TABLA: servicios (si no existe)
-- ============================================
CREATE TABLE IF NOT EXISTS servicios (
    id_servicio BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre_servicio VARCHAR(255),
    descripcion VARCHAR(255),
    duracion_minutos INT NOT NULL,
    precio DOUBLE NOT NULL,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Agregar índice en servicios
SET @sql = (
    SELECT IF(
        COUNT(*) = 0,
        'CREATE INDEX idx_nombre ON servicios(nombre_servicio);',
        'SELECT "El índice idx_nombre ya existe en servicios" AS info;'
    )
    FROM INFORMATION_SCHEMA.STATISTICS
    WHERE TABLE_SCHEMA = 'sigc_db'
    AND TABLE_NAME = 'servicios'
    AND INDEX_NAME = 'idx_nombre'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SELECT 'Tabla servicios configurada ✓' AS mensaje;

-- ============================================
-- INSERTAR DATOS INICIALES (si no existen)
-- ============================================

-- Insertar usuario administrador si no existe
INSERT IGNORE INTO usuarios (nombre, email, password, dni, telefono, rol, activo) VALUES 
('Administrador', 'admin@sigc.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhCu', '00000000', '999999999', 'ADMIN', 1);

SELECT 'Usuario administrador configurado ✓' AS mensaje;

-- Insertar especialidades si no existen
INSERT IGNORE INTO especialidades (nombre, descripcion, imagen) VALUES
('Medicina General', 'Atención médica general y preventiva', '/uploads/especialidades/medicina-general.jpg'),
('Cardiología', 'Atención del corazón y sistema circulatorio', '/uploads/especialidades/cardiologia.jpg'),
('Neurología', 'Especialidad médica que trata los trastornos del sistema nervioso', '/uploads/especialidades/neurologia.jpg'),
('Pediatría', 'Atención médica de niños y adolescentes', '/uploads/especialidades/pediatria.jpg'),
('Ginecología', 'Salud reproductiva y cuidado femenino', '/uploads/especialidades/ginecologia.jpg'),
('Dermatología', 'Tratamiento de enfermedades de la piel', '/uploads/especialidades/dermatologia.jpg'),
('Oftalmología', 'Especialidad médica que estudia las enfermedades del ojo', '/uploads/especialidades/oftalmologia.jpg'),
('Traumatología', 'Especialidad que trata lesiones del aparato locomotor', '/uploads/especialidades/traumatologia.jpg'),
('Odontología', 'Cuidado dental y salud bucal', '/uploads/especialidades/odontologia.jpg');

SELECT 'Especialidades configuradas ✓' AS mensaje;

-- Insertar doctores de ejemplo si no existen
INSERT IGNORE INTO doctores (nombre, especialidad, cupo_pacientes, imagen) VALUES
('Dra. María González', 'Medicina General', 15, '/uploads/doctores/maria-gonzalez.jpg'),
('Dr. Carlos Méndez', 'Cardiología', 12, '/uploads/doctores/carlos-mendez.jpg'),
('Dra. Ana Torres', 'Neurología', 8, '/uploads/doctores/ana-torres.jpg'),
('Dr. Ricardo López', 'Pediatría', 10, '/uploads/doctores/ricardo-lopez.jpg'),
('Dra. Sofía Ramírez', 'Ginecología', 10, '/uploads/doctores/sofia-ramirez.jpg'),
('Dr. Luis Vega', 'Dermatología', 12, '/uploads/doctores/luis-vega.jpg');

SELECT 'Doctores de ejemplo configurados ✓' AS mensaje;

-- ============================================
-- VERIFICACIÓN FINAL Y RESUMEN
-- ============================================
SELECT '============================================' AS separador;
SELECT 'CONFIGURACIÓN COMPLETADA' AS titulo;
SELECT '============================================' AS separador;

SELECT 
    'usuarios' AS tabla,
    COUNT(*) AS total_registros
FROM usuarios
UNION ALL
SELECT 
    'especialidades' AS tabla,
    COUNT(*) AS total_registros
FROM especialidades
UNION ALL
SELECT 
    'doctores' AS tabla,
    COUNT(*) AS total_registros
FROM doctores
UNION ALL
SELECT 
    'horarios' AS tabla,
    COUNT(*) AS total_registros
FROM horarios
UNION ALL
SELECT 
    'citas' AS tabla,
    COUNT(*) AS total_registros
FROM citas
UNION ALL
SELECT 
    'servicios' AS tabla,
    COUNT(*) AS total_registros
FROM servicios;

SELECT '============================================' AS separador;
SELECT '✅ Base de datos configurada exitosamente' AS resultado;
SELECT '============================================' AS separador;

-- Mostrar información de acceso
SELECT 'INFORMACIÓN DE ACCESO' AS info;
SELECT 
    email AS 'Email Administrador',
    'Admin123456' AS 'Password',
    rol AS 'Rol'
FROM usuarios 
WHERE rol = 'ADMIN' 
LIMIT 1;

SELECT '============================================' AS separador;