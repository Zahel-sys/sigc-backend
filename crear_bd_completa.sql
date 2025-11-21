-- ============================================
-- SCRIPT DE BASE DE DATOS SIGC - VERSIÓN MEJORADA
-- Compatible con el backend actual (nombres en plural)
-- ============================================

-- Eliminar base de datos si existe y recrearla
DROP DATABASE IF EXISTS sigc_db;
CREATE DATABASE sigc_db CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE sigc_db;

-- ============================================
-- TABLA: usuarios
-- ============================================
CREATE TABLE usuarios (
    id_usuario BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    dni VARCHAR(8) NOT NULL,
    telefono VARCHAR(9) NOT NULL,
    rol VARCHAR(50) NOT NULL DEFAULT 'PACIENTE',
    activo BIT(1) NOT NULL DEFAULT 1,
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_email (email),
    INDEX idx_rol (rol)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ============================================
-- TABLA: especialidades
-- ============================================
CREATE TABLE especialidades (
    id_especialidad BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL UNIQUE,
    descripcion TEXT,
    imagen VARCHAR(255),
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_nombre (nombre)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ============================================
-- TABLA: doctores
-- ============================================
CREATE TABLE doctores (
    id_doctor INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    especialidad VARCHAR(255) NOT NULL,
    cupo_pacientes INT DEFAULT 10,
    imagen VARCHAR(255),
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_especialidad (especialidad),
    INDEX idx_nombre (nombre)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ============================================
-- TABLA: horarios
-- ============================================
CREATE TABLE horarios (
    id_horario INT AUTO_INCREMENT PRIMARY KEY,
    fecha DATE NOT NULL,
    turno VARCHAR(255) NOT NULL,
    hora_inicio TIME NOT NULL,
    hora_fin TIME NOT NULL,
    disponible TINYINT(1) DEFAULT 1,
    id_doctor INT,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_doctor) REFERENCES doctores(id_doctor) ON DELETE CASCADE,
    INDEX idx_fecha (fecha),
    INDEX idx_doctor_fecha (id_doctor, fecha),
    INDEX idx_disponible (disponible)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ============================================
-- TABLA: citas
-- ============================================
CREATE TABLE citas (
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
    FOREIGN KEY (id_horario) REFERENCES horarios(id_horario) ON DELETE CASCADE,
    INDEX idx_usuario (id_usuario),
    INDEX idx_doctor (id_doctor),
    INDEX idx_fecha (fecha_cita),
    INDEX idx_estado (estado)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ============================================
-- TABLA: servicios
-- ============================================
CREATE TABLE servicios (
    id_servicio BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre_servicio VARCHAR(255),
    descripcion VARCHAR(255),
    duracion_minutos INT NOT NULL,
    precio DOUBLE NOT NULL,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_nombre (nombre_servicio)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ============================================
-- DATOS INICIALES
-- ============================================

-- Usuario administrador (password: Admin123456 - BCrypt hash)
INSERT INTO usuarios (nombre, email, password, dni, telefono, rol, activo) VALUES 
('Administrador', 'admin@sigc.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhCu', '00000000', '999999999', 'ADMIN', 1);

-- Especialidades médicas
INSERT INTO especialidades (nombre, descripcion, imagen) VALUES
('Cardiología', 'Atención del corazón y sistema circulatorio', '/uploads/especialidades/cardiologia.jpg'),
('Pediatría', 'Atención médica de niños y adolescentes', '/uploads/especialidades/pediatria.jpg'),
('Odontología', 'Cuidado dental y salud bucal', '/uploads/especialidades/odontologia.jpg'),
('Dermatología', 'Tratamiento de enfermedades de la piel', '/uploads/especialidades/dermatologia.jpg'),
('Ginecología', 'Salud reproductiva y cuidado femenino', '/uploads/especialidades/ginecologia.jpg'),
('Neurología', 'Especialidad médica que trata los trastornos del sistema nervioso', '/uploads/especialidades/neurologia.jpg');

-- Doctores (usando los mismos de la BD actual)
INSERT INTO doctores (nombre, especialidad, cupo_pacientes, imagen) VALUES
('Dr. Ricardo López', 'Cardiología', 10, '/uploads/doctores/ricardo-lopez.jpg'),
('Dra. Sofía Torres', 'Pediatría', 15, '/uploads/doctores/sofia-torres.jpg'),
('Dr. Luis Ramos', 'Odontología', 8, '/uploads/doctores/luis-ramos.jpg'),
('Dra. Carmen Vega', 'Dermatología', 12, '/uploads/doctores/carmen-vega.jpg'),
('Dra. Ana Gutiérrez', 'Ginecología', 10, '/uploads/doctores/ana-gutierrez.jpg');

-- Horarios de ejemplo
INSERT INTO horarios (fecha, turno, hora_inicio, hora_fin, disponible, id_doctor) VALUES
('2025-11-25', 'Mañana', '09:00:00', '12:00:00', 1, 1),
('2025-11-25', 'Tarde', '14:00:00', '17:00:00', 1, 2),
('2025-11-26', 'Mañana', '09:00:00', '12:00:00', 1, 3),
('2025-11-27', 'Tarde', '14:00:00', '17:00:00', 1, 4),
('2025-11-28', 'Mañana', '08:30:00', '11:30:00', 1, 5);

-- Usuarios de prueba (pacientes)
INSERT INTO usuarios (nombre, email, password, dni, telefono, rol, activo) VALUES
('Juan Pérez', 'juan@cliente.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhCu', '12345678', '987654321', 'PACIENTE', 1),
('María López', 'maria@cliente.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhCu', '87654321', '912345678', 'PACIENTE', 1),
('Carlos Ramírez', 'carlos@cliente.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhCu', '65432198', '934567890', 'PACIENTE', 1);

-- Citas de ejemplo
INSERT INTO citas (fecha_cita, hora_cita, turno, estado, id_usuario, id_doctor, id_horario) VALUES
('2025-11-25', '09:00:00', 'Mañana', 'ACTIVA', 2, 1, 1),
('2025-11-25', '14:00:00', 'Tarde', 'ACTIVA', 3, 2, 2),
('2025-11-26', '09:00:00', 'Mañana', 'CANCELADA', 4, 3, 3),
('2025-11-27', '14:00:00', 'Tarde', 'ACTIVA', 2, 4, 4),
('2025-11-28', '08:30:00', 'Mañana', 'COMPLETADA', 3, 5, 5);

-- ============================================
-- VERIFICACIÓN
-- ============================================
SELECT '✅ Base de datos creada exitosamente' AS resultado;
SELECT 
    (SELECT COUNT(*) FROM usuarios) AS total_usuarios,
    (SELECT COUNT(*) FROM doctores) AS total_doctores,
    (SELECT COUNT(*) FROM especialidades) AS total_especialidades,
    (SELECT COUNT(*) FROM horarios) AS total_horarios,
    (SELECT COUNT(*) FROM citas) AS total_citas,
    (SELECT COUNT(*) FROM servicios) AS total_servicios;

-- Mostrar resumen de datos insertados
SELECT 'Usuarios creados:' AS info;
SELECT id_usuario, nombre, email, rol FROM usuarios;

SELECT 'Doctores creados:' AS info;
SELECT id_doctor, nombre, especialidad, cupo_pacientes FROM doctores;

SELECT 'Especialidades creadas:' AS info;
SELECT id_especialidad, nombre, descripcion FROM especialidades;

SELECT 'Horarios creados:' AS info;
SELECT id_horario, fecha, turno, hora_inicio, hora_fin, disponible, id_doctor FROM horarios;

SELECT 'Citas creadas:' AS info;
SELECT id_cita, fecha_cita, hora_cita, estado, id_usuario, id_doctor, id_horario FROM citas;
