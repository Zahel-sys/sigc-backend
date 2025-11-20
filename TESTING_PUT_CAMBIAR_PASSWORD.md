# Testing - PUT /usuarios/cambiar-password

## Endpoint Overview
- **Method**: PUT
- **URL**: `http://localhost:8080/usuarios/cambiar-password`
- **Authentication**: Required (Bearer Token)
- **Request Content-Type**: `application/json`

---

## Request Format

```json
{
  "passwordActual": "tu_password_actual",
  "passwordNueva": "tu_nuevo_password",
  "passwordConfirmar": "tu_nuevo_password"
}
```

### Field Validation
- `passwordActual`: No puede estar vacío
- `passwordNueva`: No puede estar vacío
- `passwordConfirmar`: No puede estar vacío

---

## Test Cases

### 1. Cambio de Password Exitoso (200 OK)
**Descripción**: Usuario autenticado cambia su contraseña correctamente.

```bash
$token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
$body = @{
    passwordActual = "password123"
    passwordNueva = "nuevoPassword456"
    passwordConfirmar = "nuevoPassword456"
} | ConvertTo-Json

Invoke-RestMethod `
  -Uri "http://localhost:8080/usuarios/cambiar-password" `
  -Method Put `
  -Headers @{"Authorization"="Bearer $token"} `
  -Body $body `
  -ContentType "application/json"
```

**Expected Response** (200):
```json
{
  "idUsuario": 1,
  "email": "usuario@example.com",
  "mensaje": "Contraseña cambiada exitosamente",
  "timestamp": "2025-11-19T19:30:00",
  "exitoso": true
}
```

---

### 2. Token JWT No Proporcionado (401 Unauthorized)
**Descripción**: Intento sin token en header Authorization.

```bash
$body = @{
    passwordActual = "password123"
    passwordNueva = "nuevoPassword456"
    passwordConfirmar = "nuevoPassword456"
} | ConvertTo-Json

Invoke-RestMethod `
  -Uri "http://localhost:8080/usuarios/cambiar-password" `
  -Method Put `
  -Body $body `
  -ContentType "application/json"
```

**Expected Response** (401):
```json
{
  "error": "Token JWT no proporcionado",
  "timestamp": "2025-11-19T19:30:00"
}
```

---

### 3. Token JWT Inválido (401 Unauthorized)
**Descripción**: Token malformado o expirado.

```bash
$token = "token_invalido_xyz"
$body = @{
    passwordActual = "password123"
    passwordNueva = "nuevoPassword456"
    passwordConfirmar = "nuevoPassword456"
} | ConvertTo-Json

Invoke-RestMethod `
  -Uri "http://localhost:8080/usuarios/cambiar-password" `
  -Method Put `
  -Headers @{"Authorization"="Bearer $token"} `
  -Body $body `
  -ContentType "application/json"
```

**Expected Response** (401):
```json
{
  "error": "Token JWT inválido o expirado",
  "timestamp": "2025-11-19T19:30:00"
}
```

---

### 4. Usuario No Encontrado (404 Not Found)
**Descripción**: El token es válido pero el usuario no existe en la BD.

```bash
# Token válido pero para usuario_id que no existe
$token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
$body = @{
    passwordActual = "password123"
    passwordNueva = "nuevoPassword456"
    passwordConfirmar = "nuevoPassword456"
} | ConvertTo-Json

Invoke-RestMethod `
  -Uri "http://localhost:8080/usuarios/cambiar-password" `
  -Method Put `
  -Headers @{"Authorization"="Bearer $token"} `
  -Body $body `
  -ContentType "application/json"
```

**Expected Response** (404):
```json
{
  "error": "Usuario no encontrado",
  "timestamp": "2025-11-19T19:30:00"
}
```

---

### 5. Password Actual Vacío (400 Bad Request)
**Descripción**: Campo `passwordActual` no proporcionado.

```bash
$token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
$body = @{
    passwordActual = ""
    passwordNueva = "nuevoPassword456"
    passwordConfirmar = "nuevoPassword456"
} | ConvertTo-Json

Invoke-RestMethod `
  -Uri "http://localhost:8080/usuarios/cambiar-password" `
  -Method Put `
  -Headers @{"Authorization"="Bearer $token"} `
  -Body $body `
  -ContentType "application/json"
```

**Expected Response** (400):
```json
{
  "error": "Todos los campos de password son requeridos",
  "timestamp": "2025-11-19T19:30:00"
}
```

---

### 6. Contraseña Actual Incorrecta (400 Bad Request)
**Descripción**: El `passwordActual` no coincide con la contraseña guardada.

```bash
$token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
$body = @{
    passwordActual = "passwordIncorrecto"
    passwordNueva = "nuevoPassword456"
    passwordConfirmar = "nuevoPassword456"
} | ConvertTo-Json

Invoke-RestMethod `
  -Uri "http://localhost:8080/usuarios/cambiar-password" `
  -Method Put `
  -Headers @{"Authorization"="Bearer $token"} `
  -Body $body `
  -ContentType "application/json"
```

**Expected Response** (400):
```json
{
  "error": "Contraseña actual incorrecta",
  "timestamp": "2025-11-19T19:30:00"
}
```

---

### 7. Nuevas Contraseñas No Coinciden (422 Unprocessable Entity)
**Descripción**: `passwordNueva` y `passwordConfirmar` no son iguales.

```bash
$token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
$body = @{
    passwordActual = "password123"
    passwordNueva = "nuevoPassword456"
    passwordConfirmar = "diferentPassword789"
} | ConvertTo-Json

Invoke-RestMethod `
  -Uri "http://localhost:8080/usuarios/cambiar-password" `
  -Method Put `
  -Headers @{"Authorization"="Bearer $token"} `
  -Body $body `
  -ContentType "application/json"
```

**Expected Response** (422):
```json
{
  "error": "Las nuevas contraseñas no coinciden",
  "timestamp": "2025-11-19T19:30:00"
}
```

---

### 8. Contraseña Nueva Igual a Actual (422 Unprocessable Entity)
**Descripción**: El nuevo password es igual al actual.

```bash
$token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
$body = @{
    passwordActual = "password123"
    passwordNueva = "password123"
    passwordConfirmar = "password123"
} | ConvertTo-Json

Invoke-RestMethod `
  -Uri "http://localhost:8080/usuarios/cambiar-password" `
  -Method Put `
  -Headers @{"Authorization"="Bearer $token"} `
  -Body $body `
  -ContentType "application/json"
```

**Expected Response** (422):
```json
{
  "error": "La nueva contraseña debe ser diferente a la actual",
  "timestamp": "2025-11-19T19:30:00"
}
```

---

### 9. Contraseña Muy Corta (422 Unprocessable Entity)
**Descripción**: Nueva contraseña tiene menos de 6 caracteres.

```bash
$token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
$body = @{
    passwordActual = "password123"
    passwordNueva = "pass1"
    passwordConfirmar = "pass1"
} | ConvertTo-Json

Invoke-RestMethod `
  -Uri "http://localhost:8080/usuarios/cambiar-password" `
  -Method Put `
  -Headers @{"Authorization"="Bearer $token"} `
  -Body $body `
  -ContentType "application/json"
```

**Expected Response** (422):
```json
{
  "error": "La contraseña debe tener al menos 6 caracteres",
  "timestamp": "2025-11-19T19:30:00"
}
```

---

### 10. Error de Servidor (500 Internal Server Error)
**Descripción**: Error interno al guardar la contraseña (ej: problema de BD).

**Expected Response** (500):
```json
{
  "error": "Error al cambiar la contraseña",
  "timestamp": "2025-11-19T19:30:00"
}
```

---

## How to Get a Valid Token

### Option 1: Register a New User
```bash
$body = @{
    nombre = "Juan Perez"
    email = "juan@example.com"
    password = "password123"
    rol = "PACIENTE"
} | ConvertTo-Json

$response = Invoke-RestMethod `
  -Uri "http://localhost:8080/auth/registrar" `
  -Method Post `
  -Body $body `
  -ContentType "application/json"

$token = $response.token
Write-Host "Token: $token"
```

### Option 2: Login with Existing User
```bash
$body = @{
    email = "usuario@example.com"
    password = "password123"
} | ConvertTo-Json

$response = Invoke-RestMethod `
  -Uri "http://localhost:8080/auth/login" `
  -Method Post `
  -Body $body `
  -ContentType "application/json"

$token = $response.token
Write-Host "Token: $token"
```

---

## Expected Behavior Summary

| Test Case | Status Code | Description |
|-----------|-------------|-------------|
| Exitoso | 200 | Cambio de contraseña completado |
| Token no proporcionado | 401 | Sin header Authorization |
| Token inválido | 401 | Token malformado o expirado |
| Usuario no encontrado | 404 | User ID del token no existe |
| Password actual vacío | 400 | Campos requeridos faltantes |
| Password actual incorrecto | 400 | Validación de contraseña fallida |
| Nuevas passwords no coinciden | 422 | passwordNueva != passwordConfirmar |
| Password nuevo igual al actual | 422 | No permite reutilizar contraseña |
| Password muy corto | 422 | Longitud < 6 caracteres |
| Error de servidor | 500 | Problema al guardar cambios |

---

## Security Notes

✅ **Características de Seguridad Implementadas:**
1. Token JWT obligatorio (401 si falta)
2. Validación del token (401 si es inválido)
3. Verificación de usuario existe (404 si no)
4. Validación de contraseña actual (400 si incorrecta)
5. Encriptación con BCryptPasswordEncoder
6. Validación de longitud mínima (6 caracteres)
7. Validación de coincidencia entre nuevas passwords
8. **NO expone la contraseña en la respuesta**

⚠️ **Recomendaciones Adicionales:**
- Usar HTTPS en producción (no HTTP)
- Implementar rate limiting para prevenir fuerza bruta
- Considerar log de cambios de contraseña
- Notificar por email después de cambio de password
- Validar complejidad de password (mayúscula, número, especial)

---

## Response Format Details

### Success Response (200)
```json
{
  "idUsuario": 1,
  "email": "usuario@example.com",
  "mensaje": "Contraseña cambiada exitosamente",
  "timestamp": "2025-11-19T19:30:00",
  "exitoso": true
}
```

### Error Response Format
```json
{
  "error": "Descripción del error",
  "timestamp": "2025-11-19T19:30:00"
}
```

**Key Points:**
- ✅ Respuesta exitosa incluye: idUsuario, email, mensaje, timestamp, exitoso
- ❌ Respuesta de error incluye: error, timestamp
- ✅ **No expone la contraseña en ningún caso**
- ✅ Incluye timestamp para auditoria
