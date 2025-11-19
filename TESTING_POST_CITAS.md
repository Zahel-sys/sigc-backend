# 🧪 Testing del Endpoint POST /citas

## 📋 Casos de Prueba Completos

### 1️⃣ CASO EXITOSO - Crear cita válida

**Descripción:** Todo está correcto, la cita debe crearse exitosamente

**Headers:**
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
Content-Type: application/json
```

**Body:**
```json
{
  "usuario": {
    "idUsuario": 1
  },
  "horario": {
    "idHorario": 1
  }
}
```

**Respuesta esperada (201 Created):**
```json
{
  "idCita": 10,
  "fechaCita": "2025-11-25",
  "horaCita": "08:00:00",
  "turno": "Mañana",
  "usuario": { "idUsuario": 1, "nombre": "admin" },
  "doctor": { "idDoctor": 1, "nombre": "Richard" },
  "horario": { "idHorario": 1, "disponible": false },
  "estado": "confirmada"
}
```

**Validaciones:**
- ✓ Token JWT válido
- ✓ Usuario existe (ID: 1)
- ✓ Horario existe (ID: 1)
- ✓ Horario disponible (antes de la reserva)
- ✓ No hay cita previa para este horario
- ✓ Horario no está en el pasado

---

### 2️⃣ ERROR 401 - Token no proporcionado

**Descripción:** Se envía la petición sin header Authorization

**Headers:**
```
Content-Type: application/json
```

**Body:**
```json
{
  "usuario": { "idUsuario": 1 },
  "horario": { "idHorario": 1 }
}
```

**Respuesta esperada (401 Unauthorized):**
```json
{
  "error": "Token JWT requerido en header Authorization",
  "timestamp": "2025-11-19T16:50:00"
}
```

---

### 3️⃣ ERROR 401 - Token inválido

**Descripción:** Token JWT es inválido o expirado

**Headers:**
```
Authorization: Bearer token_invalido_123
Content-Type: application/json
```

**Body:**
```json
{
  "usuario": { "idUsuario": 1 },
  "horario": { "idHorario": 1 }
}
```

**Respuesta esperada (401 Unauthorized):**
```json
{
  "error": "Token JWT inválido o expirado",
  "timestamp": "2025-11-19T16:50:00"
}
```

---

### 4️⃣ ERROR 400 - falta idPaciente

**Descripción:** No se envía el idPaciente en el body

**Headers:**
```
Authorization: Bearer eyJhbGc...
Content-Type: application/json
```

**Body:**
```json
{
  "horario": { "idHorario": 1 }
}
```

**Respuesta esperada (400 Bad Request):**
```json
{
  "error": "Debe proporcionar idPaciente",
  "timestamp": "2025-11-19T16:50:00"
}
```

---

### 5️⃣ ERROR 400 - Falta idHorario

**Descripción:** No se envía el idHorario en el body

**Headers:**
```
Authorization: Bearer eyJhbGc...
Content-Type: application/json
```

**Body:**
```json
{
  "usuario": { "idUsuario": 1 }
}
```

**Respuesta esperada (400 Bad Request):**
```json
{
  "error": "Debe proporcionar idHorario",
  "timestamp": "2025-11-19T16:50:00"
}
```

---

### 6️⃣ ERROR 404 - Paciente no encontrado

**Descripción:** El idPaciente no existe en la BD

**Headers:**
```
Authorization: Bearer eyJhbGc...
Content-Type: application/json
```

**Body:**
```json
{
  "usuario": { "idUsuario": 999 },
  "horario": { "idHorario": 1 }
}
```

**Respuesta esperada (404 Not Found):**
```json
{
  "error": "Paciente no encontrado",
  "timestamp": "2025-11-19T16:50:00"
}
```

---

### 7️⃣ ERROR 404 - Horario no encontrado

**Descripción:** El idHorario no existe en la BD

**Headers:**
```
Authorization: Bearer eyJhbGc...
Content-Type: application/json
```

**Body:**
```json
{
  "usuario": { "idUsuario": 1 },
  "horario": { "idHorario": 999 }
}
```

**Respuesta esperada (404 Not Found):**
```json
{
  "error": "Horario no encontrado",
  "timestamp": "2025-11-19T16:50:00"
}
```

---

### 8️⃣ ERROR 409 - Horario no disponible

**Descripción:** El horario ya fue reservado (disponible = false)

**Pasos previos:**
1. Crear una cita con horario ID 2
2. Intentar crear otra cita con el mismo horario ID 2

**Headers:**
```
Authorization: Bearer eyJhbGc...
Content-Type: application/json
```

**Body:**
```json
{
  "usuario": { "idUsuario": 1 },
  "horario": { "idHorario": 2 }
}
```

**Respuesta esperada (409 Conflict):**
```json
{
  "error": "El horario ya no está disponible",
  "timestamp": "2025-11-19T16:50:00"
}
```

---

### 9️⃣ ERROR 409 - Cita duplicada

**Descripción:** Ya existe una cita para ese horario

**Pasos previos:**
1. Crear una cita con horario ID 3 y usuario 1
2. Intentar crear otra cita con el mismo horario ID 3

**Headers:**
```
Authorization: Bearer eyJhbGc...
Content-Type: application/json
```

**Body:**
```json
{
  "usuario": { "idUsuario": 2 },
  "horario": { "idHorario": 3 }
}
```

**Respuesta esperada (409 Conflict):**
```json
{
  "error": "Ya existe una cita para este horario",
  "timestamp": "2025-11-19T16:50:00"
}
```

---

### 🔟 ERROR 422 - Horario en el pasado

**Descripción:** Se intenta reservar un horario cuya fecha ya pasó

**Requisito:** Tener un horario con fecha anterior a la actual

**Headers:**
```
Authorization: Bearer eyJhbGc...
Content-Type: application/json
```

**Body:**
```json
{
  "usuario": { "idUsuario": 1 },
  "horario": { "idHorario": 5 }  // horario con fecha pasada
}
```

**Respuesta esperada (422 Unprocessable Entity):**
```json
{
  "error": "No se puede reservar un horario en el pasado",
  "timestamp": "2025-11-19T16:50:00"
}
```

---

## 🔧 Comandos cURL para Testing

### Obtener token (necesario para todos los tests)
```bash
TOKEN=$(curl -s -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@sigc.com","password":"Admin123456"}' | jq -r '.token')

echo "Token obtenido: $TOKEN"
```

### Test 1: Crear cita exitosamente
```bash
curl -X POST http://localhost:8080/citas \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "usuario": {"idUsuario": 1},
    "horario": {"idHorario": 1}
  }' | jq .
```

### Test 2: Sin token
```bash
curl -X POST http://localhost:8080/citas \
  -H "Content-Type: application/json" \
  -d '{
    "usuario": {"idUsuario": 1},
    "horario": {"idHorario": 1}
  }' | jq .
```

### Test 3: Token inválido
```bash
curl -X POST http://localhost:8080/citas \
  -H "Authorization: Bearer invalid_token" \
  -H "Content-Type: application/json" \
  -d '{
    "usuario": {"idUsuario": 1},
    "horario": {"idHorario": 1}
  }' | jq .
```

### Test 4: Sin idPaciente
```bash
curl -X POST http://localhost:8080/citas \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "horario": {"idHorario": 1}
  }' | jq .
```

### Test 5: Sin idHorario
```bash
curl -X POST http://localhost:8080/citas \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "usuario": {"idUsuario": 1}
  }' | jq .
```

### Test 6: Usuario no existe
```bash
curl -X POST http://localhost:8080/citas \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "usuario": {"idUsuario": 999},
    "horario": {"idHorario": 1}
  }' | jq .
```

### Test 7: Horario no existe
```bash
curl -X POST http://localhost:8080/citas \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "usuario": {"idUsuario": 1},
    "horario": {"idHorario": 999}
  }' | jq .
```

---

## 📊 Tabla de Validaciones

| Validación | Tipo | Código | Mensaje |
|------------|------|--------|---------|
| Token no proporcionado | 401 | UNAUTHORIZED | "Token JWT requerido..." |
| Token inválido | 401 | UNAUTHORIZED | "Token JWT inválido o expirado" |
| falta idPaciente | 400 | BAD_REQUEST | "Debe proporcionar idPaciente" |
| falta idHorario | 400 | BAD_REQUEST | "Debe proporcionar idHorario" |
| Usuario no existe | 404 | NOT_FOUND | "Paciente no encontrado" |
| Horario no existe | 404 | NOT_FOUND | "Horario no encontrado" |
| Horario no disponible | 409 | CONFLICT | "El horario ya no está disponible" |
| Cita duplicada | 409 | CONFLICT | "Ya existe una cita para este horario" |
| Horario en pasado | 422 | UNPROCESSABLE_ENTITY | "No se puede reservar un horario..." |
| Éxito | 201 | CREATED | Objeto Cita completo |

---

## ✅ Checklist de Validaciones Implementadas

- [x] ✅ Validar token JWT
- [x] ✅ Validar que idPaciente sea proporcionado
- [x] ✅ Validar que idHorario sea proporcionado
- [x] ✅ Validar que paciente existe en BD
- [x] ✅ Validar que horario existe en BD
- [x] ✅ Validar que horario esté disponible
- [x] ✅ Validar que no hay cita duplicada
- [x] ✅ Validar que horario no está en pasado
- [x] ✅ Cambiar horario a no disponible
- [x] ✅ Crear cita con estado "confirmada"
- [x] ✅ Retornar respuesta correcta (201)

---

## 🚀 Orden Recomendado de Testing

1. ✅ Obtener token válido
2. ✅ Hacer una reserva exitosa (Test 1)
3. ✅ Intentar sin token (Test 2)
4. ✅ Intentar con token inválido (Test 3)
5. ✅ Intentar sin datos requeridos (Tests 4-5)
6. ✅ Intentar con IDs no existentes (Tests 6-7)
7. ✅ Intentar reservar horario no disponible (Test 8)
8. ✅ Intentar reservar horario en pasado (Test 10)

¡Todos los tests listos para ejecutar! 🎉
