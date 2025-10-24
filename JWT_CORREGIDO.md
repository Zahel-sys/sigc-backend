# ✅ JWT CORREGIDO - ID EN CAMPO "SUB"

## 📋 Resumen de Cambios

Se corrigió el backend Spring Boot para que el token JWT almacene el **ID numérico del usuario** en el campo `"sub"` en lugar del email, según los estándares JWT y las necesidades de la aplicación.

---

## 🔧 Archivos Modificados

### 1. **JwtUtil.java** ✅

**Cambios principales:**
- ✅ Método `generateToken()` ahora acepta 3 parámetros: `idUsuario`, `email`, `rol`
- ✅ El campo `"sub"` ahora contiene el ID numérico del usuario
- ✅ El email se almacena como claim adicional
- ✅ El rol se almacena como claim adicional
- ✅ Nuevos métodos para extraer información del token:
  - `getUserIdFromToken(String token)` → Retorna `Long`
  - `getEmailFromToken(String token)` → Retorna `String`
  - `getRolFromToken(String token)` → Retorna `String`

**Código:**
```java
public String generateToken(Long idUsuario, String email, String rol) {
    Map<String, Object> claims = new HashMap<>();
    claims.put("email", email);
    claims.put("rol", rol);

    return Jwts.builder()
            .setClaims(claims)
            .setSubject(String.valueOf(idUsuario)) // ← ID como subject
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
            .signWith(getSigningKey(), SignatureAlgorithm.HS256)
            .compact();
}
```

---

### 2. **AuthController.java** ✅

**Cambios principales:**
- ✅ El método `login()` ahora genera el token con `idUsuario`, `email` y `rol`
- ✅ La respuesta incluye todos los datos del usuario:
  - `message`: "Login exitoso"
  - `token`: Token JWT
  - `rol`: Rol del usuario
  - `idUsuario`: ID numérico
  - `nombre`: Nombre completo
  - `email`: Email
  - `dni`: DNI
  - `telefono`: Teléfono

**Código:**
```java
@PostMapping("/login")
public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> credentials) {
    String email = credentials.get("email");
    String password = credentials.get("password");

    Usuario usuario = usuarioRepository.findByEmail(email);

    Map<String, Object> response = new HashMap<>();
    
    if (usuario != null && passwordEncoder.matches(password, usuario.getPassword())) {
        // Generar token con ID como subject
        String token = jwtUtil.generateToken(usuario.getIdUsuario(), usuario.getEmail(), usuario.getRol());
        
        // Respuesta completa con todos los datos del usuario
        response.put("message", "Login exitoso");
        response.put("token", token);
        response.put("rol", usuario.getRol());
        response.put("idUsuario", usuario.getIdUsuario());
        response.put("nombre", usuario.getNombre());
        response.put("email", usuario.getEmail());
        response.put("dni", usuario.getDni());
        response.put("telefono", usuario.getTelefono());
        
        return ResponseEntity.ok(response);
    } else {
        response.put("error", "Credenciales inválidas");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }
}
```

---

## 📊 Estructura del Token JWT

### ✅ Token Decodificado (NUEVO)
```json
{
  "sub": "12",                    ← ID numérico del usuario (CORREGIDO)
  "email": "testjwt@test.com",    ← Email como claim adicional
  "rol": "PACIENTE",              ← Rol como claim adicional
  "iat": 1761313418,              ← Issued at (timestamp)
  "exp": 1761399818               ← Expiration (timestamp)
}
```

### ❌ Token Anterior (PROBLEMA)
```json
{
  "sub": "testjwt@test.com",  ← Email (INCORRECTO)
  "iat": 1761313418,
  "exp": 1761399818
}
```

---

## 🧪 Pruebas Realizadas

### Test: POST /auth/login

**Request:**
```json
{
  "email": "testjwt@test.com",
  "password": "12345678"
}
```

**Response:**
```json
{
  "idUsuario": 12,
  "message": "Login exitoso",
  "telefono": "999888777",
  "nombre": "Test JWT",
  "rol": "PACIENTE",
  "email": "testjwt@test.com",
  "dni": "88888888",
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJlbWFpbCI6InRlc3Rqd3RAdGVzdC5jb20iLCJyb2wiOiJQQUNJRU5URSIsInN1YiI6IjEyIiwiaWF0IjoxNzYxMzEzNDE4LCJleHAiOjE3NjEzOTk4MTh9.ESLHtVBstCk0rh7sLfxZK1uRZrI30MtUJiBxQ37pqbE"
}
```

**Verificación del token:**
- ✅ Campo `"sub"` contiene ID numérico: `"12"`
- ✅ Campo `"email"` presente: `"testjwt@test.com"`
- ✅ Campo `"rol"` presente: `"PACIENTE"`
- ✅ Token válido con expiración de 24 horas

---

## 📝 Uso en el Frontend

### 1. Login
```javascript
const response = await fetch('http://localhost:8080/auth/login', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    email: 'usuario@example.com',
    password: 'password123'
  })
});

const data = await response.json();

// Guardar en localStorage
localStorage.setItem('token', data.token);
localStorage.setItem('idUsuario', data.idUsuario);
localStorage.setItem('nombre', data.nombre);
localStorage.setItem('email', data.email);
localStorage.setItem('rol', data.rol);
localStorage.setItem('dni', data.dni);
localStorage.setItem('telefono', data.telefono);
```

### 2. Decodificar Token en el Frontend (opcional)
```javascript
function parseJwt(token) {
  const base64Url = token.split('.')[1];
  const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
  const jsonPayload = decodeURIComponent(
    atob(base64)
      .split('')
      .map(c => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
      .join('')
  );
  return JSON.parse(jsonPayload);
}

const token = localStorage.getItem('token');
const decoded = parseJwt(token);

console.log('ID Usuario:', decoded.sub);       // "12"
console.log('Email:', decoded.email);          // "testjwt@test.com"
console.log('Rol:', decoded.rol);              // "PACIENTE"
```

### 3. Enviar Token en Peticiones
```javascript
const response = await fetch('http://localhost:8080/citas', {
  method: 'GET',
  headers: {
    'Authorization': `Bearer ${localStorage.getItem('token')}`
  }
});
```

---

## 🔐 Uso en el Backend

### Extraer ID del Usuario del Token

Si necesitas extraer el ID del usuario en otros controladores:

```java
@GetMapping("/mi-perfil")
public ResponseEntity<?> obtenerPerfil(@RequestHeader("Authorization") String authHeader) {
    try {
        // Extraer token del header (quitar "Bearer ")
        String token = authHeader.substring(7);
        
        // Extraer ID del usuario
        Long idUsuario = jwtUtil.getUserIdFromToken(token);
        
        // Buscar usuario
        Usuario usuario = usuarioRepository.findById(idUsuario)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        return ResponseEntity.ok(usuario);
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido");
    }
}
```

---

## 🎯 Beneficios de Esta Solución

1. ✅ **Estándar JWT**: El campo `"sub"` ahora contiene un identificador único (ID numérico)
2. ✅ **Seguridad**: No se expone el email como identificador principal
3. ✅ **Eficiencia**: Los endpoints pueden extraer directamente el ID sin consultas adicionales
4. ✅ **Flexibilidad**: Email y rol están disponibles como claims adicionales
5. ✅ **Retrocompatibilidad**: Método `getUsernameFromToken()` marcado como `@Deprecated` pero aún funcional

---

## 📦 Archivos Generados

- ✅ `JwtUtil.java` - Generador y validador de tokens JWT
- ✅ `AuthController.java` - Endpoint de login con respuesta completa
- ✅ `test-jwt-fix.ps1` - Script de prueba del JWT
- ✅ `JWT_CORREGIDO.md` - Este documento

---

## ✅ Estado Final

| Componente | Estado | Descripción |
|------------|--------|-------------|
| JwtUtil.java | ✅ | Token genera con ID en "sub" |
| AuthController.java | ✅ | Login retorna datos completos |
| Test JWT | ✅ | Validado con usuario real |
| Compilación | ✅ | BUILD SUCCESS |
| Backend | ✅ | Ejecutando en puerto 8080 |

---

## 🚀 Comandos Útiles

```powershell
# Compilar proyecto
.\mvnw.cmd clean compile

# Iniciar backend
.\mvnw.cmd spring-boot:run

# Probar JWT
.\test-jwt-fix.ps1

# Registrar usuario de prueba
$body = @{ nombre = "Test User"; email = "test@example.com"; password = "12345678"; dni = "12345678"; telefono = "987654321"; rol = "PACIENTE" } | ConvertTo-Json
Invoke-RestMethod -Uri "http://localhost:8080/auth/register" -Method POST -ContentType "application/json" -Body $body

# Login
$login = @{ email = "test@example.com"; password = "12345678" } | ConvertTo-Json
Invoke-RestMethod -Uri "http://localhost:8080/auth/login" -Method POST -ContentType "application/json" -Body $login
```

---

## 📚 Documentación

- [JWT.io](https://jwt.io/) - Decodificador de tokens JWT
- [RFC 7519](https://tools.ietf.org/html/rfc7519) - Estándar JWT
- [JJWT](https://github.com/jwtk/jjwt) - Librería Java JWT usada en el proyecto

---

**Fecha:** 24 de octubre de 2025  
**Proyecto:** SIGC Backend (Sistema de Gestión de Citas)  
**Framework:** Spring Boot 3.5.7 + Java 21
