# 📋 Endpoint POST para Crear/Reservar Citas Médicas

## 🎯 Descripción
Este endpoint permite que los pacientes reserven citas médicas con un doctor en un horario disponible.

---

## 📍 URL
```
POST http://localhost:8080/citas
```

---

## 🔐 Headers Requeridos

```
Authorization: Bearer {token_jwt}
Content-Type: application/json
```

**Ejemplo:**
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

---

## 📤 Body (Request)

### Opción 1 - Usar "usuario":
```json
{
  "usuario": {
    "idUsuario": 1
  },
  "horario": {
    "idHorario": 5
  }
}
```

### Opción 2 - Usar "paciente" (alias compatible):
```json
{
  "paciente": {
    "idUsuario": 1
  },
  "horario": {
    "idHorario": 5
  }
}
```

---

## ✅ Validaciones Que Realiza

1. **Token JWT válido** ✓
   - Si no se envía: `401 Unauthorized`
   - Si es inválido/expirado: `401 Unauthorized`

2. **idPaciente proporcionado** ✓
   - Si no se envía: `400 Bad Request - "Debe proporcionar idPaciente"`

3. **idHorario proporcionado** ✓
   - Si no se envía: `400 Bad Request - "Debe proporcionar idHorario"`

4. **Paciente existe en BD** ✓
   - Si no existe: `404 Not Found - "Paciente no encontrado"`

5. **Horario existe en BD** ✓
   - Si no existe: `404 Not Found - "Horario no encontrado"`

6. **Horario está disponible** ✓
   - Si no está disponible: `409 Conflict - "El horario ya no está disponible"`

7. **No hay cita duplicada** ✓
   - Si ya existe cita para ese horario: `409 Conflict - "Ya existe una cita para este horario"`

8. **Horario no está en el pasado** ✓
   - Si la fecha/hora es anterior a ahora: `422 Unprocessable Entity - "No se puede reservar un horario en el pasado"`

---

## 📥 Respuesta Exitosa (201 Created)

```json
{
  "idCita": 10,
  "fechaCita": "2025-11-25",
  "horaCita": "08:00:00",
  "turno": "Mañana",
  "usuario": {
    "idUsuario": 1,
    "nombre": "Juan Pérez",
    "email": "juan@example.com",
    "rol": "PACIENTE"
  },
  "doctor": {
    "idDoctor": 2,
    "nombre": "Richard",
    "especialidad": "Cardiología",
    "cupoPacientes": 5
  },
  "horario": {
    "idHorario": 5,
    "fecha": "2025-11-25",
    "turno": "Mañana",
    "horaInicio": "08:00:00",
    "horaFin": "12:00:00",
    "disponible": false,
    "doctor": { ... }
  },
  "estado": "confirmada"
}
```

---

## ❌ Respuestas de Error

### 400 Bad Request
```json
{
  "error": "Debe proporcionar idPaciente",
  "timestamp": "2025-11-19T16:45:00"
}
```

### 401 Unauthorized
```json
{
  "error": "Token JWT inválido o expirado",
  "timestamp": "2025-11-19T16:45:00"
}
```

### 404 Not Found
```json
{
  "error": "Paciente no encontrado",
  "timestamp": "2025-11-19T16:45:00"
}
```
O
```json
{
  "error": "Horario no encontrado",
  "timestamp": "2025-11-19T16:45:00"
}
```

### 409 Conflict
```json
{
  "error": "El horario ya no está disponible",
  "timestamp": "2025-11-19T16:45:00"
}
```
O
```json
{
  "error": "Ya existe una cita para este horario",
  "timestamp": "2025-11-19T16:45:00"
}
```

### 422 Unprocessable Entity
```json
{
  "error": "No se puede reservar un horario en el pasado",
  "timestamp": "2025-11-19T16:45:00"
}
```

### 500 Internal Server Error
```json
{
  "error": "Error interno al crear la cita",
  "timestamp": "2025-11-19T16:45:00"
}
```

---

## 🔄 Lógica Importante

Cuando se crea exitosamente una cita:

1. ✓ El **horario se marca como no disponible** (`disponible = false`)
2. ✓ La **cita se asigna al paciente, doctor y horario** automáticamente
3. ✓ El **estado inicial es "confirmada"**
4. ✓ Se asigna automáticamente:
   - `fechaCita` = fecha del horario
   - `horaCita` = hora de inicio del horario
   - `turno` = turno del horario
   - `doctor` = doctor asociado al horario

---

## 💻 Ejemplo con JavaScript/Fetch

```javascript
// 1. Obtener el token JWT (después de login)
const token = localStorage.getItem('token'); // o sessionStorage

// 2. Hacer la petición
fetch('http://localhost:8080/citas', {
  method: 'POST',
  headers: {
    'Authorization': `Bearer ${token}`,
    'Content-Type': 'application/json'
  },
  body: JSON.stringify({
    usuario: {
      idUsuario: 1
    },
    horario: {
      idHorario: 5
    }
  })
})
.then(response => {
  if (response.status === 201) {
    return response.json();
  } else if (response.status === 401) {
    throw new Error('Token inválido o expirado');
  } else if (response.status === 404) {
    throw new Error('Paciente u horario no encontrado');
  } else if (response.status === 409) {
    throw new Error('Horario no disponible o cita duplicada');
  } else if (response.status === 422) {
    throw new Error('La fecha está en el pasado');
  } else {
    throw new Error('Error desconocido');
  }
})
.then(data => {
  console.log('✅ Cita creada:', data);
  console.log('ID Cita:', data.idCita);
  console.log('Fecha:', data.fechaCita);
  console.log('Estado:', data.estado);
})
.catch(error => {
  console.error('❌ Error:', error.message);
});
```

---

## 💻 Ejemplo con React

```jsx
import React, { useState } from 'react';

function ReservarCita({ doctorId, horarioId }) {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [citaCreada, setCitaCreada] = useState(null);

  const handleReservar = async () => {
    setLoading(true);
    setError(null);
    
    try {
      const token = localStorage.getItem('token');
      
      if (!token) {
        throw new Error('No hay token de autenticación');
      }

      const response = await fetch('http://localhost:8080/citas', {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({
          usuario: {
            idUsuario: 1  // Cambiar por el ID real del paciente
          },
          horario: {
            idHorario: horarioId
          }
        })
      });

      const data = await response.json();

      if (!response.ok) {
        throw new Error(data.error || 'Error al crear la cita');
      }

      setCitaCreada(data);
      console.log('✅ Cita creada:', data);
    } catch (err) {
      setError(err.message);
      console.error('❌ Error:', err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div>
      <button onClick={handleReservar} disabled={loading}>
        {loading ? 'Reservando...' : 'Reservar Cita'}
      </button>
      
      {error && <p style={{ color: 'red' }}>❌ Error: {error}</p>}
      
      {citaCreada && (
        <div style={{ color: 'green', marginTop: '10px' }}>
          <p>✅ ¡Cita reservada exitosamente!</p>
          <p>ID: {citaCreada.idCita}</p>
          <p>Fecha: {citaCreada.fechaCita}</p>
          <p>Hora: {citaCreada.horaCita}</p>
          <p>Estado: {citaCreada.estado}</p>
        </div>
      )}
    </div>
  );
}

export default ReservarCita;
```

---

## 🧪 Ejemplos de Testing (cURL)

### ✅ Caso exitoso
```bash
curl -X POST http://localhost:8080/citas \
  -H "Authorization: Bearer eyJhbGc..." \
  -H "Content-Type: application/json" \
  -d '{
    "usuario": {"idUsuario": 1},
    "horario": {"idHorario": 5}
  }'
```

### ❌ Sin token
```bash
curl -X POST http://localhost:8080/citas \
  -H "Content-Type: application/json" \
  -d '{
    "usuario": {"idUsuario": 1},
    "horario": {"idHorario": 5}
  }'
# Respuesta: 401 Unauthorized
```

### ❌ Token inválido
```bash
curl -X POST http://localhost:8080/citas \
  -H "Authorization: Bearer token_invalido" \
  -H "Content-Type: application/json" \
  -d '{
    "usuario": {"idUsuario": 1},
    "horario": {"idHorario": 5}
  }'
# Respuesta: 401 Unauthorized
```

### ❌ Paciente no existe
```bash
curl -X POST http://localhost:8080/citas \
  -H "Authorization: Bearer eyJhbGc..." \
  -H "Content-Type: application/json" \
  -d '{
    "usuario": {"idUsuario": 999},
    "horario": {"idHorario": 5}
  }'
# Respuesta: 404 Not Found
```

---

## 📊 Resumen de Respuestas

| Código | Descripción | Causa |
|--------|-------------|-------|
| 201 | Cita creada exitosamente | ✅ Todas las validaciones pasaron |
| 400 | Bad Request | ❌ Faltan datos requeridos |
| 401 | Unauthorized | ❌ Token JWT inválido/expirado |
| 404 | Not Found | ❌ Paciente u horario no existe |
| 409 | Conflict | ❌ Horario no disponible o cita duplicada |
| 422 | Unprocessable Entity | ❌ Horario en el pasado |
| 500 | Server Error | ❌ Error interno del servidor |

---

## ✨ Notas Importantes

- El **token JWT** es obligatorio y debe incluirse en el header `Authorization` con prefijo `Bearer `
- El **estado inicial** de la cita es automáticamente "confirmada"
- Al crear la cita, el **horario se marca como no disponible** automáticamente
- No se pueden crear **citas duplicadas** para el mismo horario
- No se puede reservar en **horarios del pasado**
- Se valida que ambos **paciente y horario existan** en la BD

¡Listo para usar! 🚀
