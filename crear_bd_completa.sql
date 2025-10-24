-- Eliminar base de datos si existe y recrearla
DROP DATABASE IF EXISTS sigc_db;
CREATE DATABASE sigc_db;
USE sigc_db;

-- Tabla Usuario (singular como en tu especificación)
CREATE TABLE usuario (
    id_usuario BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    dni VARCHAR(8) NOT NULL,
    telefono VARCHAR(9) NOT NULL,
    rol ENUM('PACIENTE', 'DOCTOR', 'ADMIN') DEFAULT 'PACIENTE',
    activo BOOLEAN DEFAULT TRUE,
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabla Especialidad (singular)
CREATE TABLE especialidad (
    id_especialidad BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE,
    descripcion TEXT,
    imagen VARCHAR(255)
);

-- Tabla Doctor (singular)
CREATE TABLE doctor (
    id_doctor BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    especialidad VARCHAR(100) NOT NULL,
    cupo_pacientes INT DEFAULT 10,
    imagen VARCHAR(255)
);

-- Tabla Horario (singular)
CREATE TABLE horario (
    id_horario BIGINT AUTO_INCREMENT PRIMARY KEY,
    fecha DATE NOT NULL,
    turno VARCHAR(20),
    hora_inicio TIME NOT NULL,
    hora_fin TIME NOT NULL,
    disponible BOOLEAN DEFAULT TRUE,
    id_doctor BIGINT,
    FOREIGN KEY (id_doctor) REFERENCES doctor(id_doctor) ON DELETE CASCADE
);

-- Tabla Cita (singular)
CREATE TABLE cita (
    id_cita BIGINT AUTO_INCREMENT PRIMARY KEY,
    fecha DATE NOT NULL,
    hora TIME NOT NULL,
    estado ENUM('ACTIVA', 'CANCELADA', 'COMPLETADA') DEFAULT 'ACTIVA',
    id_usuario BIGINT,
    id_doctor BIGINT,
    id_horario BIGINT,
    FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario) ON DELETE CASCADE,
    FOREIGN KEY (id_doctor) REFERENCES doctor(id_doctor) ON DELETE CASCADE,
    FOREIGN KEY (id_horario) REFERENCES horario(id_horario) ON DELETE CASCADE
);

-- Insertar admin por defecto (password: Admin123456)
INSERT INTO usuario (nombre, email, password, dni, telefono, rol, activo) VALUES 
('Administrador', 'admin@sigc.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhCu', '00000000', '999999999', 'ADMIN', TRUE);

-- Datos de prueba para especialidades
INSERT INTO especialidad (nombre, descripcion, imagen) VALUES
('Cardiología', 'Especialidad médica que se encarga del estudio, diagnóstico y tratamiento de las enfermedades del corazón', '/images/especialidades/cardiologia.jpg'),
('Pediatría', 'Rama de la medicina que se especializa en la salud y enfermedades de los niños', '/images/especialidades/pediatria.jpg'),
('Neurología', 'Especialidad médica que trata los trastornos del sistema nervioso', '/images/especialidades/neurologia.jpg');

-- Datos de prueba para doctores
INSERT INTO doctor (nombre, especialidad, cupo_pacientes, imagen) VALUES
('Dr. Juan Pérez', 'Cardiología', 10, '/images/doctores/default.jpg'),
('Dra. María García', 'Pediatría', 15, '/images/doctores/default.jpg'),
('Dr. Carlos López', 'Neurología', 12, '/images/doctores/default.jpg');

SELECT 'Base de datos creada exitosamente' AS resultado;
