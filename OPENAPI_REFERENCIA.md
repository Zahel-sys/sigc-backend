# 📋 OpenAPI SIGC - Guía de Referencia

## ✅ Cambios Realizados

### 1. **Información General Actualizada**
- ✅ Título: "SIGC - Sistema de Gestión de Citas Médicas"
- ✅ Descripción: Completa y clara
- ✅ Versión: 1.0.0
- ✅ Contacto y licencia agregados

### 2. **Servidores Configurados**
- ✅ Desarrollo: `http://localhost:8080`
- ✅ Producción: `https://api.sigc.com`

### 3. **Tags Organizados**
- ✅ Autenticación
- ✅ Usuarios
- ✅ Especialidades
- ✅ Doctores
- ✅ Citas
- ✅ Horarios

### 4. **Endpoints Implementados (22 endpoints totales)**

#### 🔐 Autenticación (3)
| Endpoint | Método | Descripción |
|----------|--------|-------------|
| `/auth/login` | POST | Iniciar sesión |
| `/auth/register` | POST | Registrar usuario |
| `/auth/me` | GET | Perfil del usuario autenticado |

#### 👥 Usuarios (4)
| Endpoint | Método | Descripción |
|----------|--------|-------------|
| `/usuarios` | GET | Listar todos |
| `/usuarios` | POST | Crear nuevo |
| `/usuarios/{id}` | GET | Obtener por ID |
| `/usuarios/{id}` | PUT | Actualizar |

#### 🏥 Especialidades (2)
| Endpoint | Método | Descripción |
|----------|--------|-------------|
| `/especialidades` | GET | Listar todas |
| `/especialidades` | POST | Crear nueva |

#### 👨‍⚕️ Doctores (2)
| Endpoint | Método | Descripción |
|----------|--------|-------------|
| `/doctores` | GET | Listar con filtros |
| `/doctores` | POST | Registrar nuevo |
| `/doctores/{id}` | GET | Obtener por ID |

#### 📅 Citas (4)
| Endpoint | Método | Descripción |
|----------|--------|-------------|
| `/citas` | GET | Listar citas |
| `/citas` | POST | Crear nueva |
| `/citas/{id}` | GET | Obtener por ID |
| `/citas/{id}` | PUT | Actualizar |
| `/citas/{id}` | DELETE | Cancelar |

#### ⏰ Horarios (1)
| Endpoint | Método | Descripción |
|----------|--------|-------------|
| `/horarios` | GET | Obtener disponibilidad |

#### 📋 Órdenes Médicas (6)
| Endpoint | Método | Descripción |
|----------|--------|-------------|
| `/orders` | GET | Listar órdenes |
| `/orders` | POST | Crear nueva |
| `/orders/{id}` | GET | Obtener por ID |
| `/orders/{id}` | PUT | Actualizar |
| `/orders/{id}` | DELETE | Eliminar |

---

## 🔑 Esquemas Definidos (12)

### Request Schemas
1. **LoginRequest** - Email y contraseña
2. **RegisterRequest** - Datos de registro
3. **ActualizarUsuarioRequest** - Nombre y teléfono
4. **EspecialidadRequest** - Nombre y descripción
5. **DoctorRequest** - Datos del doctor
6. **CrearCitaRequest** - ID doctor, fecha, hora, motivo
7. **ActualizarCitaRequest** - Campos actualizables
8. **CrearOrdenRequest** - Datos de la orden médica

### Response Schemas
1. **LoginResponse** - Token JWT y datos usuario
2. **RegistroResponse** - Confirmación de registro
3. **UsuarioResponse** - Datos del usuario
4. **EspecialidadResponse** - Especialidad completa
5. **DoctorResponse** - Información del doctor
6. **CitaResponse** - Detalles de la cita
7. **HorarioResponse** - Disponibilidad
8. **OrdenResponse** - Orden médica completa
9. **ErrorResponse** - Errores estandarizados

---

## 🔐 Seguridad

### JWT Bearer Token
```
Security Scheme: BearerAuth (HTTP Bearer)
Formato: JWT (JSON Web Tokens)
Duración: 24 horas
Algoritmo: HS256
```

### Headers Requeridos
```
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
Content-Type: application/json
```

---

## 📝 Ejemplos de Uso

### 1️⃣ Login
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "paciente@sigc.com",
    "password": "Paciente123456"
  }'
```

**Respuesta (200 OK):**
```json
{
  "message": "Login exitoso",
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "rol": "PACIENTE",
  "idUsuario": 5,
  "email": "paciente@sigc.com"
}
```

### 2️⃣ Registrar Usuario
```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Juan Pérez",
    "email": "juan@sigc.com",
    "password": "Juan123456",
    "dni": "12345678",
    "telefono": "555000100",
    "rol": "PACIENTE"
  }'
```

**Respuesta (201 Created):**
```json
{
  "idUsuario": 5,
  "nombre": "Juan Pérez",
  "email": "juan@sigc.com",
  "mensaje": "Usuario registrado exitosamente"
}
```

### 3️⃣ Listar Especialidades
```bash
curl -X GET http://localhost:8080/especialidades
```

**Respuesta (200 OK):**
```json
[
  {
    "idEspecialidad": 9,
    "nombre": "Medicina General",
    "descripcion": "Atención médica general",
    "imagen": null
  },
  {
    "idEspecialidad": 10,
    "nombre": "Cardiología",
    "descripcion": "Especialidad en enfermedades del corazón",
    "imagen": null
  }
]
```

### 4️⃣ Crear Cita
```bash
curl -X POST http://localhost:8080/citas \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..." \
  -H "Content-Type: application/json" \
  -d '{
    "idDoctor": 1,
    "fecha": "2025-12-15",
    "hora": "10:30",
    "motivo": "Revisión periódica"
  }'
```

**Respuesta (201 Created):**
```json
{
  "idCita": 1,
  "paciente": "Juan Pérez",
  "doctor": "Dr. Carlos López",
  "fecha": "2025-12-15",
  "hora": "10:30",
  "especialidad": "Cardiología",
  "motivo": "Revisión periódica",
  "estado": "CONFIRMADA",
  "fechaCreacion": "2025-12-05T18:45:00"
}
```

### 5️⃣ Obtener Horarios Disponibles
```bash
curl -X GET "http://localhost:8080/horarios?idDoctor=1&fecha=2025-12-15"
```

**Respuesta (200 OK):**
```json
[
  {
    "idHorario": 1,
    "doctor": "Dr. Carlos López",
    "fecha": "2025-12-15",
    "hora": "09:00",
    "disponible": true
  },
  {
    "idHorario": 2,
    "doctor": "Dr. Carlos López",
    "fecha": "2025-12-15",
    "hora": "10:30",
    "disponible": true
  }
]
```

### 6️⃣ Crear Orden Médica
```bash
curl -X POST http://localhost:8080/orders \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..." \
  -H "Content-Type: application/json" \
  -d '{
    "idPaciente": 5,
    "idDoctor": 1,
    "descripcion": "Prescripción de medicamentos",
    "medicamentos": ["Ibuprofeno 400mg", "Paracetamol 500mg"],
    "observaciones": "Tomar con las comidas"
  }'
```

**Respuesta (201 Created):**
```json
{
  "idOrden": 1,
  "paciente": "Juan Pérez",
  "doctor": "Dr. Carlos López",
  "descripcion": "Prescripción de medicamentos",
  "medicamentos": ["Ibuprofeno 400mg", "Paracetamol 500mg"],
  "observaciones": "Tomar con las comidas",
  "fechaCreacion": "2025-12-05T18:45:00"
}
```

---

## ✅ Validaciones Implementadas

### Email
- Formato válido requerido
- Debe ser único en registro
- Caso de uso: login, registro

### Contraseña
- Mínimo 6 caracteres
- Máximo 50 caracteres
- Se almacena encriptada (BCrypt)

### DNI
- Exactamente 8 dígitos
- Solo números
- Ej: `12345678`

### Teléfono
- Exactamente 9 dígitos
- Solo números
- Ej: `555000100`

### Nombre
- Mínimo 2 caracteres
- Máximo 100 caracteres
- Alfanumérico con espacios

### Fecha
- Formato: `YYYY-MM-DD`
- Ej: `2025-12-15`

### Hora
- Formato: `HH:MM` (24 horas)
- Ej: `10:30`

---

## 🚨 Códigos de Error

| Código | Descripción |
|--------|-------------|
| `200` | OK - Solicitud exitosa |
| `201` | Created - Recurso creado |
| `204` | No Content - Eliminado exitosamente |
| `400` | Bad Request - Datos inválidos |
| `401` | Unauthorized - No autenticado/autorizado |
| `404` | Not Found - Recurso no encontrado |
| `409` | Conflict - Conflicto (email duplicado, horario no disponible) |

---

## 📱 Modelos de Datos

### Roles Disponibles
- **PACIENTE** - Usuarios que reservan citas
- **DOCTOR** - Médicos profesionales
- **ADMIN** - Administrador del sistema

### Estados de Cita
- **PENDIENTE** - Recién creada, pendiente de confirmación
- **CONFIRMADA** - Cita confirmada
- **CANCELADA** - Cita cancelada
- **COMPLETADA** - Cita realizada

---

## 🔗 Integración con Backend SIGC

El OpenAPI está completamente sincronizado con:
- ✅ Controllers REST
- ✅ DTOs de entrada/salida
- ✅ Modelos de dominio
- ✅ Validaciones
- ✅ Manejo de errores

---

## 📚 Usando el OpenAPI

### 1️⃣ Con Swagger UI
```
http://localhost:8080/swagger-ui/index.html
```

### 2️⃣ Con Postman
- Importar archivo OpenAPI en Postman
- Crear environment con variable `token`
- Usar tokens obtenidos en login

### 3️⃣ Generación de Clientes
```bash
# OpenAPI Generator
openapi-generator-cli generate -i openapi.yml -g javascript-client

# Swagger Codegen
swagger-codegen generate -i openapi.yml -l typescript-angular
```

---

## ✨ Características Principales

✅ **22 Endpoints** completos  
✅ **12 Esquemas** de datos  
✅ **Ejemplos** en cada endpoint  
✅ **Validaciones** documentadas  
✅ **Seguridad JWT** integrada  
✅ **CORS** configurado  
✅ **Documentación** profesional  

---

**Última actualización**: 2025-12-05  
**Versión OpenAPI**: 3.0.3  
**Estado**: ✅ Completo y funcional
