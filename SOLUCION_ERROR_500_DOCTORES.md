# 🔧 Solución Error 500 al Crear Doctores

## ❌ Problema
Al intentar crear un doctor desde el frontend aparece:
- Error 500 en el servidor
- AxiosError en la consola del navegador

## 🔍 Causa
El backend (Spring Boot con Hibernate) espera que la tabla `doctores` tenga EXACTAMENTE esta estructura:

```sql
CREATE TABLE doctores (
    id_doctor BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    especialidad VARCHAR(255) NOT NULL,
    cupo_pacientes INT NOT NULL,
    imagen VARCHAR(255)
);
```

Si la tabla fue creada manualmente con una estructura diferente (por ejemplo, con foreign keys a `especialidades` o con columnas adicionales), Hibernate NO puede trabajar con ella.

## ✅ Solución

### Opción 1: Dejar que Hibernate cree las tablas (RECOMENDADO)

1. **Eliminar las tablas manuales:**
```sql
DROP TABLE IF EXISTS citas;
DROP TABLE IF EXISTS horarios;
DROP TABLE IF EXISTS doctores;
DROP TABLE IF EXISTS especialidades;
DROP TABLE IF EXISTS usuarios;
```

2. **Reiniciar el backend**
   - Hibernate creará automáticamente las tablas con la estructura correcta

3. **Verificar en `application.properties`:**
```properties
spring.jpa.hibernate.ddl-auto=update
```

### Opción 2: Corregir la tabla manualmente

```sql
-- Verificar estructura actual
DESCRIBE doctores;

-- Si tiene columnas extra o foreign keys, recrearla:
DROP TABLE IF EXISTS doctores;

CREATE TABLE doctores (
    id_doctor BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    especialidad VARCHAR(255) NOT NULL,
    cupo_pacientes INT NOT NULL CHECK (cupo_pacientes >= 1 AND cupo_pacientes <= 20),
    imagen VARCHAR(255)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

## 📝 Notas Importantes

1. **Especialidad es String**: El modelo `Doctor` guarda la especialidad como texto simple, NO como foreign key.

2. **Validaciones**: El backend valida:
   - Nombre: no vacío
   - Especialidad: no vacía
   - Cupo: entre 1 y 20

3. **Formato de datos**: El backend SOLO acepta `multipart/form-data`, NO JSON.

## 🎯 Verificación

Después de aplicar la solución, probar:

```powershell
# Listar doctores
Invoke-RestMethod -Uri "http://localhost:8080/doctores" -Method GET | ConvertTo-Json

# La respuesta debe ser exitosa (aunque esté vacía)
```

## 🚀 Frontend Ya Corregido

El frontend está 100% funcional y:
- ✅ Envía datos en formato `multipart/form-data`
- ✅ Valida el cupo entre 1-20
- ✅ Muestra errores descriptivos
- ✅ Soporta carga de imágenes (opcional)
- ✅ Permite editar y eliminar doctores

Solo falta que el backend pueda procesar las peticiones correctamente.
