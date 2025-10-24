# 📋 Estructura del Proyecto - Sistema de Registro de Usuarios

## 🗂️ Estructura de Archivos Creados/Modificados

```
sigc-backend/
│
├── src/main/java/com/sigc/backend/
│   │
│   ├── controller/
│   │   └── AuthController.java          ✅ MODIFICADO
│   │       • Endpoint POST /auth/register
│   │       • Endpoint POST /auth/login
│   │       • Validación con @Valid
│   │       • CORS configurado para localhost:5173 y 5174
│   │
│   ├── service/
│   │   └── UsuarioService.java          ✅ NUEVO
│   │       • Lógica de negocio para registro
│   │       • Validación de email duplicado
│   │       • Encriptación BCrypt de contraseñas
│   │       • Logging con SLF4J
│   │
│   ├── dto/
│   │   ├── RegistroRequest.java         ✅ NUEVO
│   │   │   • DTO para recibir datos de registro
│   │   │   • Validaciones con Jakarta Bean Validation
│   │   │   • Campos: nombre, email, password, dni, telefono, rol
│   │   │
│   │   └── RegistroResponse.java        ✅ NUEVO
│   │       • DTO para respuesta exitosa
│   │       • Campos: idUsuario, nombre, email, mensaje
│   │       • NO incluye password (seguridad)
│   │
│   ├── model/
│   │   └── Usuario.java                 ✅ MODIFICADO
│   │       • Entidad JPA con tabla 'usuarios'
│   │       • Campo password para hash BCrypt (255 chars)
│   │       • Campo fecha_registro con @CreationTimestamp
│   │       • Constraints de base de datos (unique, not null)
│   │
│   ├── repository/
│   │   └── UsuarioRepository.java       ✅ EXISTENTE
│   │       • Método findByEmail() ya implementado
│   │       • Extiende JpaRepository
│   │
│   ├── security/
│   │   └── SecurityConfig.java          ✅ MODIFICADO
│   │       • Bean PasswordEncoder (BCrypt)
│   │       • Configuración CORS global
│   │       • Permite /auth/** sin autenticación
│   │       • Desactiva CSRF para API REST
│   │
│   └── exception/
│       ├── EmailDuplicadoException.java ✅ NUEVO
│       │   • Excepción custom para email duplicado
│       │
│       └── GlobalExceptionHandler.java  ✅ NUEVO
│           • @RestControllerAdvice
│           • Maneja validaciones (400)
│           • Maneja email duplicado (409)
│           • Maneja errores genéricos (500)
│
├── src/main/resources/
│   └── application.properties           ✅ EXISTENTE
│       • Conexión MySQL puerto 3306
│       • Configuración JPA/Hibernate
│
├── database/
│   └── schema.sql                       ✅ NUEVO
│       • Script SQL para crear tabla usuarios
│       • Índices para email, rol, activo
│
├── TESTING_GUIDE.md                     ✅ NUEVO
│   • Guía completa de pruebas
│   • Ejemplos con curl, Postman, fetch, axios
│   • Casos de éxito y error
│
└── README_PROJECT_STRUCTURE.md          ✅ ESTE ARCHIVO
    • Documentación de la estructura
```

---

## 📦 Paquetes y Responsabilidades

| Paquete | Responsabilidad | Archivos |
|---------|----------------|----------|
| `controller` | Endpoints REST, manejo de HTTP | AuthController |
| `service` | Lógica de negocio, validaciones | UsuarioService |
| `dto` | Objetos de transferencia de datos | RegistroRequest, RegistroResponse |
| `model` | Entidades JPA, mapeo a BD | Usuario |
| `repository` | Acceso a datos, queries | UsuarioRepository |
| `security` | Autenticación, encriptación, CORS | SecurityConfig |
| `exception` | Excepciones custom, manejo global | EmailDuplicadoException, GlobalExceptionHandler |

---

## 🔄 Flujo de Registro

```
Frontend (React)
    │
    │ POST /auth/register
    │ { nombre, email, password, dni, telefono, rol }
    ▼
AuthController.java
    │
    │ @Valid → Validaciones automáticas
    ▼
UsuarioService.java
    │
    ├─► ¿Email existe? → EmailDuplicadoException (409)
    │
    ├─► passwordEncoder.encode(password) → Hash BCrypt
    │
    ├─► usuarioRepository.save(usuario)
    │
    └─► Retorna RegistroResponse (201)
    │
    ▼
Frontend recibe
    │
    └─► { idUsuario, nombre, email, mensaje }
```

---

## 🛡️ Seguridad Implementada

1. **Encriptación BCrypt**
   - Contraseñas hasheadas con BCryptPasswordEncoder
   - Fuerza: 10 rondas
   - Hash de ~60 caracteres almacenado

2. **Validación de Duplicados**
   - Email único a nivel de BD (constraint)
   - Validación en servicio antes de insertar
   - Error 409 Conflict si ya existe

3. **Validación de Datos**
   - Jakarta Bean Validation (@Valid)
   - Validaciones en Request y Entity
   - Mensajes personalizados de error

4. **CORS Configurado**
   - Solo localhost:5173 y 5174
   - Métodos permitidos: GET, POST, PUT, DELETE
   - Headers: Content-Type, Authorization

5. **Sin Exposición de Passwords**
   - RegistroResponse no incluye password
   - Logs no muestran contraseñas

---

## 🧪 Endpoints Disponibles

### POST /auth/register
- **Request**: `RegistroRequest` JSON
- **Response Exitosa**: `RegistroResponse` (201 Created)
- **Errores**:
  - 400 Bad Request → Validación fallida
  - 409 Conflict → Email duplicado
  - 500 Internal Server Error → Error inesperado

### POST /auth/login
- **Request**: `{ email, password }` JSON
- **Response Exitosa**: `{ token, rol, message }` (200 OK)
- **Errores**:
  - 401 Unauthorized → Credenciales inválidas

---

## 📊 Modelo de Datos

### Tabla: usuarios

| Campo | Tipo | Constraints | Descripción |
|-------|------|------------|-------------|
| id_usuario | BIGINT | PK, AUTO_INCREMENT | ID único |
| nombre | VARCHAR(100) | NOT NULL | Nombre completo |
| email | VARCHAR(100) | NOT NULL, UNIQUE | Email (login) |
| password | VARCHAR(255) | NOT NULL | Hash BCrypt |
| dni | VARCHAR(8) | NOT NULL | DNI peruano |
| telefono | VARCHAR(9) | NOT NULL | Teléfono móvil |
| rol | VARCHAR(20) | NOT NULL, DEFAULT 'PACIENTE' | PACIENTE/DOCTOR/ADMIN |
| activo | BOOLEAN | NOT NULL, DEFAULT TRUE | Estado del usuario |
| fecha_registro | TIMESTAMP | NOT NULL, AUTO | Fecha de creación |

**Índices**:
- `idx_email` en `email` (búsquedas rápidas de login)
- `idx_rol` en `rol` (filtros por rol)
- `idx_activo` en `activo` (filtros por estado)

---

## 🚀 Cómo Ejecutar

### 1. Crear la Base de Datos
```bash
mysql -u root -p < database/schema.sql
```

### 2. Iniciar el Backend
```bash
.\mvnw.cmd spring-boot:run
```

### 3. Verificar que esté corriendo
```bash
curl http://localhost:8080/actuator/health
```
(Si tienes actuator configurado)

### 4. Probar el Endpoint
```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Test User",
    "email": "test@example.com",
    "password": "password123",
    "dni": "12345678",
    "telefono": "987654321",
    "rol": "PACIENTE"
  }'
```

---

## 📝 Dependencias Maven

```xml
<!-- Ya incluidas en pom.xml -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>

<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
</dependency>

<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
</dependency>
```

---

## ✅ Checklist de Implementación

- [x] Endpoint POST /auth/register creado
- [x] Validación de email duplicado
- [x] Encriptación BCrypt de contraseñas
- [x] DTOs separados (Request/Response)
- [x] Manejo de excepciones con @ControllerAdvice
- [x] CORS configurado para frontend
- [x] Logging con SLF4J
- [x] Script SQL para tabla
- [x] Guía de pruebas completa
- [x] Compilación exitosa

---

## 🎯 Próximos Pasos Recomendados

1. **Seguridad Avanzada**
   - Implementar rate limiting
   - Añadir captcha en registro
   - Validación de contraseña fuerte

2. **Funcionalidades Adicionales**
   - Confirmación por email
   - Recuperación de contraseña
   - Actualización de perfil

3. **Testing**
   - Tests unitarios para UsuarioService
   - Tests de integración para AuthController
   - Tests de validación

4. **Producción**
   - Cambiar CORS a dominio específico
   - Configurar usuarios de BD con menos privilegios
   - Implementar JWT en todos los endpoints protegidos

---

## 📞 Soporte

Para más información, revisa:
- `TESTING_GUIDE.md` → Guía de pruebas detallada
- `database/schema.sql` → Script de base de datos
- Logs de Spring Boot → `logs/spring.log` (si está configurado)
