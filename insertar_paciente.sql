-- Script para insertar usuario paciente
-- Ejecutar en H2 Console con la JDBC URL: jdbc:h2:~/sigc_database/db

INSERT INTO USUARIOS (
    NOMBRE, 
    EMAIL, 
    PASSWORD, 
    DNI, 
    TELEFONO, 
    ROL, 
    ACTIVO
) VALUES (
    'Juan Rodríguez Pérez',
    'paciente@sigc.com',
    '$2a$10$rJ/wSdJdMb8nFiunHdIge.jsl3HUgLrAEHX/2D8EkIFZelVv5k78S',
    '12345678',
    '987654321',
    'PACIENTE',
    1
);

-- Verificar que se insertó correctamente
SELECT * FROM USUARIOS WHERE EMAIL = 'paciente@sigc.com';
