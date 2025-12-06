# 🧪 Validación y Pruebas OpenAPI SIGC

## 📋 Checklist de Endpoints

### ✅ Autenticación
- [x] POST `/auth/login` - Iniciar sesión
- [x] POST `/auth/register` - Registrar usuario
- [x] GET `/auth/me` - Obtener perfil

### ✅ Usuarios
- [x] GET `/usuarios` - Listar todos
- [x] POST `/usuarios` - Crear nuevo
- [x] GET `/usuarios/{id}` - Obtener por ID
- [x] PUT `/usuarios/{id}` - Actualizar

### ✅ Especialidades
- [x] GET `/especialidades` - Listar todas
- [x] POST `/especialidades` - Crear nueva

### ✅ Doctores
- [x] GET `/doctores` - Listar con filtros
- [x] POST `/doctores` - Registrar nuevo
- [x] GET `/doctores/{id}` - Obtener por ID

### ✅ Citas
- [x] GET `/citas` - Listar citas
- [x] POST `/citas` - Crear nueva
- [x] GET `/citas/{id}` - Obtener por ID
- [x] PUT `/citas/{id}` - Actualizar
- [x] DELETE `/citas/{id}` - Cancelar

### ✅ Horarios
- [x] GET `/horarios` - Obtener disponibilidad

### ✅ Órdenes Médicas
- [x] GET `/orders` - Listar órdenes
- [x] POST `/orders` - Crear nueva
- [x] GET `/orders/{id}` - Obtener por ID
- [x] PUT `/orders/{id}` - Actualizar
- [x] DELETE `/orders/{id}` - Eliminar

---

## 🧬 Flujo de Prueba Completo

### Paso 1: Login/Registro

#### Opción A - Login (Usuario Existente)
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "paciente@sigc.com",
    "password": "Paciente123456"
  }'
```

**Guardar el token en variable:**
```bash
TOKEN="eyJhbGciOiJIUzI1NiJ9..."
```

#### Opción B - Registrar (Nuevo Usuario)
```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "María García",
    "email": "maria@sigc.com",
    "password": "Maria123456",
    "dni": "87654321",
    "telefono": "666000200",
    "rol": "PACIENTE"
  }'
```

### Paso 2: Obtener Perfil

```bash
curl -X GET http://localhost:8080/auth/me \
  -H "Authorization: Bearer $TOKEN"
```

**Respuesta esperada:**
```json
{
  "idUsuario": 5,
  "nombre": "Juan Pérez",
  "email": "paciente@sigc.com",
  "dni": "12345678",
  "telefono": "555000100",
  "rol": "PACIENTE",
  "activo": true,
  "fechaRegistro": "2025-12-05T18:43:52"
}
```

### Paso 3: Explorar Especialidades

```bash
curl -X GET http://localhost:8080/especialidades
```

**Respuesta esperada:**
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

### Paso 4: Listar Doctores

```bash
# Sin filtros
curl -X GET http://localhost:8080/doctores

# Con filtro de especialidad
curl -X GET "http://localhost:8080/doctores?especialidad=Cardiología"

# Solo disponibles
curl -X GET "http://localhost:8080/doctores?disponibles=true"
```

### Paso 5: Obtener Horarios Disponibles

```bash
curl -X GET "http://localhost:8080/horarios?idDoctor=1&fecha=2025-12-15"
```

**Respuesta esperada:**
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

### Paso 6: Crear Cita

```bash
curl -X POST http://localhost:8080/citas \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "idDoctor": 1,
    "fecha": "2025-12-15",
    "hora": "10:30",
    "motivo": "Revisión periódica"
  }'
```

**Respuesta esperada:**
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

### Paso 7: Listar Mis Citas

```bash
curl -X GET http://localhost:8080/citas \
  -H "Authorization: Bearer $TOKEN"
```

### Paso 8: Obtener Cita Específica

```bash
curl -X GET http://localhost:8080/citas/1 \
  -H "Authorization: Bearer $TOKEN"
```

### Paso 9: Actualizar Cita

```bash
curl -X PUT http://localhost:8080/citas/1 \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "fecha": "2025-12-16",
    "hora": "14:00",
    "estado": "CONFIRMADA"
  }'
```

### Paso 10: Crear Orden Médica

```bash
curl -X POST http://localhost:8080/orders \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "idPaciente": 5,
    "idDoctor": 1,
    "descripcion": "Prescripción de medicamentos",
    "medicamentos": ["Ibuprofeno 400mg", "Paracetamol 500mg"],
    "observaciones": "Tomar con las comidas"
  }'
```

### Paso 11: Listar Órdenes

```bash
curl -X GET http://localhost:8080/orders \
  -H "Authorization: Bearer $TOKEN"
```

### Paso 12: Cancelar Cita

```bash
curl -X DELETE http://localhost:8080/citas/1 \
  -H "Authorization: Bearer $TOKEN"
```

---

## 🔍 Validaciones Esperadas

### Email Válido Requerido
```bash
# ❌ Error - Email inválido
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email": "invalid", ...}'
```

Respuesta: `400 Bad Request`

### Teléfono Exactamente 9 Dígitos
```bash
# ❌ Error - Teléfono con 8 dígitos
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{"telefono": "55500010", ...}'
```

Respuesta: `400 Bad Request`

### DNI Exactamente 8 Dígitos
```bash
# ❌ Error - DNI con 9 dígitos
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{"dni": "123456789", ...}'
```

Respuesta: `400 Bad Request`

### Email Único
```bash
# ❌ Error - Email duplicado
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email": "paciente@sigc.com", ...}'
```

Respuesta: `409 Conflict`

### Horario Disponible
```bash
# ❌ Error - Horario no disponible
curl -X POST http://localhost:8080/citas \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "idDoctor": 1,
    "fecha": "2025-12-15",
    "hora": "10:30",
    "motivo": "Revisión"
  }'
```

Respuesta: `409 Conflict` (si el horario ya está reservado)

---

## 🔐 Seguridad - Rutas Protegidas

### Sin Token - Error 401
```bash
# ❌ Error - Sin token
curl -X GET http://localhost:8080/citas
```

Respuesta: `401 Unauthorized`

### Con Token Inválido - Error 401
```bash
# ❌ Error - Token inválido
curl -X GET http://localhost:8080/citas \
  -H "Authorization: Bearer invalid_token"
```

Respuesta: `401 Unauthorized`

### Con Token Válido - OK 200
```bash
# ✅ OK - Token válido
curl -X GET http://localhost:8080/citas \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..."
```

Respuesta: `200 OK`

---

## 📊 Casos de Prueba Detallados

### Test 1: Flujo Completo de Paciente

```bash
#!/bin/bash

# 1. Registrar nuevo paciente
REGISTRO=$(curl -s -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Test User",
    "email": "test@sigc.com",
    "password": "Test123456",
    "dni": "99999999",
    "telefono": "999999999",
    "rol": "PACIENTE"
  }')

echo "Registro: $REGISTRO"

# 2. Login
LOGIN=$(curl -s -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email": "test@sigc.com", "password": "Test123456"}')

TOKEN=$(echo $LOGIN | jq -r '.token')
echo "Token: $TOKEN"

# 3. Obtener perfil
curl -s -X GET http://localhost:8080/auth/me \
  -H "Authorization: Bearer $TOKEN" | jq

# 4. Ver especialidades
curl -s -X GET http://localhost:8080/especialidades | jq '.[0:2]'

# 5. Ver doctores
curl -s -X GET http://localhost:8080/doctores | jq '.[0]'

# 6. Crear cita
CITA=$(curl -s -X POST http://localhost:8080/citas \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "idDoctor": 1,
    "fecha": "2025-12-20",
    "hora": "11:00",
    "motivo": "Consulta general"
  }')

echo "Cita creada: $CITA"
```

### Test 2: Errores y Validaciones

```bash
#!/bin/bash

echo "=== Test de Validaciones ==="

# Email inválido
echo "1. Email inválido:"
curl -s -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email": "invalid", "password": "Pass123", "nombre": "Test", "dni": "12345678", "telefono": "555000100"}' \
  | jq '.error'

# Teléfono con formato incorrecto
echo "2. Teléfono incorrecto:"
curl -s -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email": "test@test.com", "password": "Pass123", "nombre": "Test", "dni": "12345678", "telefono": "555-0001"}' \
  | jq '.error'

# Email duplicado
echo "3. Email duplicado:"
curl -s -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email": "paciente@sigc.com", "password": "Pass123", "nombre": "Test", "dni": "11111111", "telefono": "555000100"}' \
  | jq '.error'

# Sin token
echo "4. Sin token:"
curl -s -X GET http://localhost:8080/citas | jq '.error'

echo "=== Fin de pruebas ==="
```

---

## ✅ Criterios de Aceptación

- [x] Todos los 22 endpoints documentados
- [x] Ejemplos en cada endpoint
- [x] Validaciones claras
- [x] Códigos de error documentados
- [x] Seguridad JWT implementada
- [x] Esquemas completos con tipos
- [x] Nombres coherentes con el dominio
- [x] Respuestas estructuradas
- [x] CORS configurado
- [x] Sincronizado con código backend

---

## 🚀 Próximos Pasos

1. Verificar con `http://localhost:8080/swagger-ui.html`
2. Importar en Postman o Insomnia
3. Ejecutar flujos de prueba
4. Validar todas las respuestas
5. Documentar desviaciones si las hay

---

**Última actualización**: 2025-12-05  
**Estado**: ✅ Completamente configurado y documentado
