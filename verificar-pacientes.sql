-- Consulta para verificar pacientes registrados en la BD
SELECT * FROM USUARIOS WHERE ROL = 'PACIENTE';

-- Consulta para verificar todos los usuarios
SELECT EMAIL, NOMBRE, ROL, ACTIVO, TELEFONO FROM USUARIOS ORDER BY ROL;
