-- Script para crear/verificar usuario admin en BD persistente

-- 1. Verificar si admin ya existe
SELECT * FROM USUARIOS WHERE EMAIL='admin@sigc.com';

-- 2. Si no existe, crear admin (contraseña: Admin123456)
-- Hash BCrypt: $2a$10$2qm6lXKPMQkZKCK3.VfIme8o9Y6bkHaKVMH8nZU3Oo9E8WvqmqX.e
INSERT INTO USUARIOS (EMAIL, PASSWORD, NOMBRE, DNI, TELEFONO, ROL, ACTIVO, FECHA_CREACION)
VALUES (
    'admin@sigc.com',
    '$2a$10$2qm6lXKPMQkZKCK3.VfIme8o9Y6bkHaKVMH8nZU3Oo9E8WvqmqX.e',
    'Administrador del Sistema',
    '00000000',
    '999999999',
    'ADMIN',
    1,
    CURRENT_TIMESTAMP
);

-- 3. Crear paciente de prueba (contraseña: Paciente123456)
INSERT INTO USUARIOS (EMAIL, PASSWORD, NOMBRE, DNI, TELEFONO, ROL, ACTIVO, FECHA_CREACION)
VALUES (
    'paciente@sigc.com',
    '$2a$10$2qm6lXKPMQkZKCK3.VfIme8o9Y6bkHaKVMH8nZU3Oo9E8WvqmqX.e',
    'Juan Rodríguez Pérez',
    '12345678',
    '987654321',
    'PACIENTE',
    1,
    CURRENT_TIMESTAMP
);

-- 4. Verificar que se crearon
SELECT EMAIL, ROL FROM USUARIOS WHERE ROL IN ('ADMIN', 'PACIENTE');
