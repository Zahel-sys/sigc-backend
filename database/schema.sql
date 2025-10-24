-- ========================================
-- SCRIPT SQL: Tabla usuarios para SIGC
-- Base de datos: MySQL 8.0
-- ========================================

-- Crear base de datos si no existe
CREATE DATABASE IF NOT EXISTS sigc_db 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

USE sigc_db;

-- Crear tabla usuarios
CREATE TABLE IF NOT EXISTS usuarios (
    id_usuario BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL COMMENT 'Hash BCrypt de la contraseña',
    dni VARCHAR(8) NOT NULL,
    telefono VARCHAR(9) NOT NULL,
    rol VARCHAR(20) NOT NULL DEFAULT 'PACIENTE' COMMENT 'PACIENTE, DOCTOR o ADMIN',
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    fecha_registro TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_email (email),
    INDEX idx_rol (rol),
    INDEX idx_activo (activo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Verificar estructura
DESCRIBE usuarios;

-- Consultar registros (debe estar vacía al inicio)
SELECT * FROM usuarios;
