# 📚 Guía de Base de Datos SIGC

## 📋 Resumen

Este proyecto tiene **3 scripts SQL** compatibles entre sí:

| Archivo | Propósito | Cuándo usar |
|---------|-----------|-------------|
| `crear_bd_completa.sql` | Crea BD desde cero con estructura mejorada | Nueva instalación |
| `migrar_bd.sql` | Actualiza BD existente sin perder datos | Actualizar BD actual |
| `sigc_db.sql` | Dump de BD actual (solo referencia) | Backup/restauración |

## 🎯 Características Clave

### ✅ Compatibilidad Garantizada
- **Nombres de tablas**: Plural (`usuarios`, `doctores`, `horarios`, etc.)
- **Charset**: UTF-8 (`utf8mb4_general_ci`)
- **Engine**: InnoDB para transacciones
- **100% compatible** con las entidades JPA del backend

### 🔧 Mejoras Incluidas
- ✅ Índices optimizados para queries frecuentes
- ✅ Campos `fecha_creacion` en todas las tablas
- ✅ Constraints de integridad referencial
- ✅ Validación de tipos de datos
- ✅ Usuario admin pre-configurado

## 🚀 Uso

### Opción 1: Migración Segura (Recomendado)
Actualiza tu BD actual **sin perder datos**:

```powershell
# Con backup automático
.\migrar-base-datos.ps1 -backup

# Sin backup
.\migrar-base-datos.ps1

# Con credenciales personalizadas
.\migrar-base-datos.ps1 -dbUser "root" -dbPassword "tu_password" -backup
```

### Opción 2: Instalación Fresh
Crea la BD desde cero (⚠️ **borra todos los datos**):

```powershell
# Crear BD nueva
.\migrar-base-datos.ps1 -fresh

# Con backup previo
.\migrar-base-datos.ps1 -fresh -backup
```

### Opción 3: MySQL Manual

#### Migrar BD existente:
```bash
mysql -u root -p sigc_db < migrar_bd.sql
```

#### Crear BD desde cero:
```bash
mysql -u root -p < crear_bd_completa.sql
```

## 📊 Estructura de Tablas

### usuarios
```sql
- id_usuario (BIGINT, PK, AUTO_INCREMENT)
- nombre (VARCHAR 255)
- email (VARCHAR 255, UNIQUE)
- password (VARCHAR 255) -- BCrypt hash
- dni (VARCHAR 8)
- telefono (VARCHAR 9)
- rol (VARCHAR 50) -- 'PACIENTE', 'DOCTOR', 'ADMIN'
- activo (BIT 1)
- fecha_registro (TIMESTAMP)
```

### doctores
```sql
- id_doctor (INT, PK, AUTO_INCREMENT)
- nombre (VARCHAR 255)
- especialidad (VARCHAR 255)
- cupo_pacientes (INT)
- imagen (VARCHAR 255)
- fecha_creacion (TIMESTAMP)
```

### especialidades
```sql
- id_especialidad (BIGINT, PK, AUTO_INCREMENT)
- nombre (VARCHAR 255, UNIQUE)
- descripcion (TEXT)
- imagen (VARCHAR 255)
- fecha_creacion (TIMESTAMP)
```

### horarios
```sql
- id_horario (INT, PK, AUTO_INCREMENT)
- fecha (DATE)
- turno (VARCHAR 255)
- hora_inicio (TIME)
- hora_fin (TIME)
- disponible (TINYINT 1)
- id_doctor (INT, FK -> doctores)
- fecha_creacion (TIMESTAMP)
```

### citas
```sql
- id_cita (BIGINT, PK, AUTO_INCREMENT)
- fecha_cita (DATE)
- hora_cita (TIME)
- turno (VARCHAR 255)
- estado (VARCHAR 255) -- 'ACTIVA', 'CANCELADA', 'COMPLETADA'
- id_usuario (INT, FK -> usuarios)
- id_doctor (INT, FK -> doctores)
- id_horario (INT, FK -> horarios)
- fecha_creacion (TIMESTAMP)
```

### servicios
```sql
- id_servicio (BIGINT, PK, AUTO_INCREMENT)
- nombre_servicio (VARCHAR 255)
- descripcion (VARCHAR 255)
- duracion_minutos (INT)
- precio (DOUBLE)
- fecha_creacion (TIMESTAMP)
```

## 🔐 Credenciales por Defecto

**Usuario Administrador:**
- Email: `admin@sigc.com`
- Password: `Admin123456`
- Rol: `ADMIN`

## 🛡️ Seguridad

### Passwords
- Almacenados con **BCrypt** (hash + salt)
- Costo de hashing: 10 rondas
- Nunca almacenar passwords en texto plano

### Integridad Referencial
- **ON DELETE CASCADE**: Al eliminar un doctor, se eliminan sus horarios y citas
- **ON DELETE CASCADE**: Al eliminar un usuario, se eliminan sus citas

## 📈 Índices Optimizados

### Performance Queries
```sql
-- Usuarios
idx_usuarios_email       -- Login rápido
idx_usuarios_rol         -- Filtrar por rol

-- Doctores  
idx_doctores_especialidad -- Búsqueda por especialidad
idx_doctores_nombre       -- Búsqueda por nombre

-- Horarios
idx_horarios_fecha           -- Búsqueda por fecha
idx_horarios_doctor_fecha    -- Horarios de doctor en fecha
idx_horarios_disponible      -- Solo disponibles

-- Citas
idx_citas_usuario    -- Citas de un paciente
idx_citas_doctor     -- Citas de un doctor
idx_citas_fecha      -- Citas en fecha
idx_citas_estado     -- Filtrar por estado
```

## ⚙️ Configuración Backend

El backend está configurado en `application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/sigc_db?useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
```

## 🔧 Troubleshooting

### Error: "Table doesn't exist"
```powershell
# Verificar que las tablas usen nombres en plural
.\migrar-base-datos.ps1 -fresh
```

### Error: "Access denied"
```powershell
# Verificar credenciales
.\migrar-base-datos.ps1 -dbUser "tu_usuario" -dbPassword "tu_password"
```

### Error: "Unknown database"
```powershell
# Crear BD desde cero
.\migrar-base-datos.ps1 -fresh
```

## 📝 Notas Importantes

1. **Backup antes de migrar**: Siempre haz backup con `-backup`
2. **Nombres en plural**: Las tablas DEBEN usar plural (`doctores`, NO `doctor`)
3. **Charset UTF-8**: Usa `utf8mb4` para soporte completo de caracteres
4. **InnoDB**: Engine obligatorio para transacciones y FK

## 🎯 Próximos Pasos

Después de migrar la BD:

1. ✅ Verificar que el backend inicie sin errores
2. ✅ Probar login con `admin@sigc.com` / `Admin123456`
3. ✅ Registrar doctores desde el panel admin
4. ✅ Crear horarios para los doctores
5. ✅ Probar creación de citas

## 📞 Soporte

Si encuentras problemas:
1. Revisa los logs del backend
2. Verifica que MySQL esté corriendo
3. Confirma que las credenciales sean correctas
4. Revisa que las tablas existan: `SHOW TABLES;`
