# 🔧 SOLUCIÓN ERROR 500 - GET /usuarios/{email}

## 🚨 Problema Detectado

El frontend estaba recibiendo **ERROR 500** al intentar cargar datos del usuario en el dashboard:

```
GET http://localhost:5175/usuarios/1flores@gmail.com 500 (Internal Server Error)
```

### 🔍 Causa Raíz

1. **Token JWT antiguo**: El usuario hizo login **antes** de la corrección del JWT
2. **Token viejo contenía**: `{sub: '1flores@gmail.com'}` (email en lugar de ID)
3. **Frontend intentaba**: `GET /usuarios/1flores@gmail.com`
4. **Backend esperaba**: `GET /usuarios/{id}` donde `id` es un `Long` (número)
5. **Resultado**: Error 500 porque Spring no puede convertir un email a Long

---

## ✅ Solución Implementada

### 1. Nuevo Endpoint: `/usuarios/email/{email}`

Añadido en `UsuarioController.java`:

```java
/**
 * GET /usuarios/email/{email}
 * Obtiene un usuario por su email
 * Útil para retrocompatibilidad con tokens antiguos
 */
@GetMapping("/email/{email}")
public ResponseEntity<?> obtenerUsuarioPorEmail(@PathVariable String email) {
    try {
        log.info("Obteniendo usuario por email: {}", email);
        Usuario usuario = usuarioRepository.findByEmail(email);
        if (usuario != null) {
            log.info("Usuario encontrado: {}", usuario.getIdUsuario());
            return ResponseEntity.ok(usuario);
        } else {
            log.warn("Usuario no encontrado con email: {}", email);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Usuario no encontrado con email: " + email);
        }
    } catch (Exception e) {
        log.error("Error al obtener usuario por email {}: {}", email, e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error al buscar el usuario");
    }
}
```

### 2. Endpoints Disponibles

| Endpoint | Parámetro | Descripción |
|----------|-----------|-------------|
| `GET /usuarios/{id}` | `Long id` | Busca usuario por ID numérico (NUEVO ESTÁNDAR) |
| `GET /usuarios/email/{email}` | `String email` | Busca usuario por email (RETROCOMPATIBILIDAD) |

---

## 🧪 Pruebas Realizadas

### ✅ Test: Buscar usuario por email

**Request:**
```http
GET http://localhost:8080/usuarios/email/testjwt@test.com
```

**Response:** (200 OK)
```json
{
  "idUsuario": 12,
  "nombre": "Test JWT",
  "email": "testjwt@test.com",
  "dni": "88888888",
  "telefono": "999888777",
  "rol": "PACIENTE",
  "activo": true,
  "fechaRegistro": "2025-10-24T08:43:28.557924"
}
```

### ✅ Test: Buscar usuario por ID

**Request:**
```http
GET http://localhost:8080/usuarios/12
```

**Response:** (200 OK)
```json
{
  "idUsuario": 12,
  "nombre": "Test JWT",
  ...
}
```

---

## 🎯 Solución para el Frontend

### Opción 1: Cerrar Sesión y Volver a Hacer Login (RECOMENDADO)

**Pasos en el frontend:**

1. Hacer clic en "Cerrar sesión"
2. Hacer login nuevamente
3. El nuevo token tendrá: `{sub: "12", email: "testjwt@test.com", rol: "PACIENTE"}`
4. El frontend podrá usar el ID directamente: `GET /usuarios/12`

**Código JavaScript/TypeScript:**
```javascript
// Limpiar localStorage
localStorage.removeItem('token');
localStorage.removeItem('idUsuario');
localStorage.removeItem('email');
localStorage.removeItem('nombre');
localStorage.removeItem('rol');

// Redirigir al login
window.location.href = '/login';
```

### Opción 2: Actualizar Frontend para Usar Nuevo Endpoint (TEMPORAL)

Si no puedes cerrar sesión ahora, actualiza temporalmente el frontend:

```javascript
// ANTES (causa error 500):
const response = await fetch(`http://localhost:8080/usuarios/${email}`);

// DESPUÉS (usa nuevo endpoint):
const response = await fetch(`http://localhost:8080/usuarios/email/${email}`);
```

### Opción 3: Detectar Tipo de Identificador (INTELIGENTE)

```javascript
function obtenerUsuario(identifier) {
  // Detectar si es email o ID numérico
  const isEmail = identifier.includes('@');
  const endpoint = isEmail 
    ? `http://localhost:8080/usuarios/email/${identifier}`
    : `http://localhost:8080/usuarios/${identifier}`;
  
  return fetch(endpoint).then(res => res.json());
}

// Uso:
obtenerUsuario('testjwt@test.com');  // Usa /usuarios/email/...
obtenerUsuario('12');                 // Usa /usuarios/12
```

---

## 📊 Flujo Correcto con Nuevo JWT

### 1. Login
```javascript
const loginResponse = await fetch('http://localhost:8080/auth/login', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({ email: 'user@example.com', password: '12345678' })
});

const data = await loginResponse.json();

// Guardar en localStorage
localStorage.setItem('token', data.token);
localStorage.setItem('idUsuario', data.idUsuario);  // ← Usar este ID
localStorage.setItem('nombre', data.nombre);
localStorage.setItem('email', data.email);
localStorage.setItem('rol', data.rol);
```

### 2. Obtener Datos del Usuario
```javascript
// Usar el ID del localStorage (MÁS EFICIENTE)
const idUsuario = localStorage.getItem('idUsuario');
const response = await fetch(`http://localhost:8080/usuarios/${idUsuario}`);
const usuario = await response.json();

console.log(usuario);
```

### 3. Decodificar Token (Opcional)
```javascript
function parseJwt(token) {
  const base64Url = token.split('.')[1];
  const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
  const jsonPayload = decodeURIComponent(
    atob(base64).split('').map(c => 
      '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2)
    ).join('')
  );
  return JSON.parse(jsonPayload);
}

const token = localStorage.getItem('token');
const decoded = parseJwt(token);

console.log('ID del token:', decoded.sub);      // "12"
console.log('Email del token:', decoded.email); // "testjwt@test.com"
console.log('Rol del token:', decoded.rol);     // "PACIENTE"

// Usar el ID del token
const response = await fetch(`http://localhost:8080/usuarios/${decoded.sub}`);
```

---

## 🔄 Migración de Tokens Antiguos

### Detectar Token Antiguo

```javascript
function esTokenAntiguo(token) {
  try {
    const decoded = parseJwt(token);
    // Token antiguo tiene email en 'sub'
    // Token nuevo tiene ID numérico en 'sub'
    return isNaN(decoded.sub);
  } catch {
    return true;
  }
}

// Uso:
const token = localStorage.getItem('token');
if (esTokenAntiguo(token)) {
  alert('Tu sesión es antigua. Por favor, vuelve a hacer login.');
  // Cerrar sesión y limpiar
  localStorage.clear();
  window.location.href = '/login';
}
```

---

## 🚀 Comandos de Testing

### Backend

```powershell
# Compilar
.\mvnw.cmd clean compile

# Iniciar backend
.\mvnw.cmd spring-boot:run

# Probar endpoint por email
Invoke-RestMethod -Uri "http://localhost:8080/usuarios/email/testjwt@test.com"

# Probar endpoint por ID
Invoke-RestMethod -Uri "http://localhost:8080/usuarios/12"
```

### Frontend

```javascript
// Limpiar sesión antigua
localStorage.clear();

// Hacer login nuevo
const login = await fetch('http://localhost:8080/auth/login', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    email: 'testjwt@test.com',
    password: '12345678'
  })
});

const data = await login.json();
console.log('Nuevo token:', data.token);
console.log('ID Usuario:', data.idUsuario);
```

---

## ✅ Estado Final

| Componente | Estado | Notas |
|------------|--------|-------|
| Backend | ✅ | Endpoint `/usuarios/email/{email}` añadido |
| Compilación | ✅ | BUILD SUCCESS |
| Backend corriendo | ✅ | Puerto 8080 |
| Endpoint por ID | ✅ | `GET /usuarios/12` funciona |
| Endpoint por Email | ✅ | `GET /usuarios/email/testjwt@test.com` funciona |
| Token JWT | ✅ | Genera con ID en "sub" |

---

## 📝 Recomendaciones

1. **Cerrar sesión en el frontend** y volver a hacer login para obtener el nuevo token
2. **Actualizar el frontend** para usar `idUsuario` en lugar de email al buscar usuarios
3. **Mantener el endpoint `/usuarios/email/{email}`** para retrocompatibilidad temporal
4. **En producción**: Implementar versionado de API (`/v1/usuarios/{id}`, `/v2/usuarios/{id}`)

---

**Fecha:** 24 de octubre de 2025  
**Problema:** ERROR 500 en GET /usuarios/{email}  
**Solución:** Endpoint adicional + Nuevo token JWT con ID  
**Estado:** ✅ RESUELTO
