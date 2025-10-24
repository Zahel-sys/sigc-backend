# 🎯 SOLUCIÓN COMPLETA - JWT CON ID EN "SUB"

## 📋 Problema Identificado

Tu frontend recibe **ERROR 500** porque el token JWT almacenado tiene el **EMAIL en el campo "sub"** en lugar del **ID numérico**.

**Token antiguo (incorrecto):**
```json
{
  "sub": "1flores@gmail.com",  ← EMAIL (causa errores 500)
  "iat": 1761312509,
  "exp": 1761396909
}
```

**Token nuevo (correcto):**
```json
{
  "sub": "12",                  ← ID NUMÉRICO
  "email": "testjwt@test.com",  ← Email como claim
  "rol": "PACIENTE",            ← Rol como claim
  "iat": 1761313418,
  "exp": 1761399818
}
```

---

## ✅ Soluciones Implementadas

### 1. **Backend Corregido** ✅

#### JwtUtil.java
- Genera tokens con ID en "sub"
- Incluye email y rol como claims adicionales

#### AuthController.java
- Login devuelve token nuevo + datos completos del usuario

#### UsuarioController.java
- `GET /usuarios/{id}` - Buscar por ID (NUEVO ESTÁNDAR)
- `GET /usuarios/email/{email}` - Buscar por email (RETROCOMPATIBILIDAD)

#### TokenController.java (NUEVO) ✅
- `POST /auth/validate-token` - Validar token
- `POST /auth/refresh-token` - Renovar token antiguo a nuevo
- `GET /auth/token-info` - Obtener info del token

---

## 🚀 SOLUCIONES PARA EL FRONTEND

### 🎯 Solución 1: CERRAR SESIÓN (MÁS SIMPLE)

**Pasos:**
1. Cerrar sesión en el frontend
2. Hacer login nuevamente
3. El nuevo token tendrá el ID en "sub"

**Código JavaScript:**
```javascript
// Limpiar localStorage y redirigir al login
localStorage.clear();
window.location.href = '/login';
```

---

### 🔄 Solución 2: RENOVAR TOKEN AUTOMÁTICAMENTE

#### Opción A: Detectar y Renovar al Cargar la Página

```javascript
// En el archivo principal de tu app (App.jsx, main.js, etc.)
async function verificarYRenovarToken() {
  const token = localStorage.getItem('token');
  
  if (!token) {
    // No hay token, redirigir al login
    window.location.href = '/login';
    return;
  }

  try {
    // Verificar tipo de token
    const response = await fetch('http://localhost:8080/auth/token-info', {
      headers: { 'Authorization': `Bearer ${token}` }
    });

    const data = await response.json();

    if (data.tokenType === 'old') {
      console.log('Token antiguo detectado, renovando...');
      await renovarToken();
    } else {
      console.log('Token válido y actualizado');
    }
  } catch (error) {
    console.error('Error al verificar token:', error);
    // Token inválido, cerrar sesión
    localStorage.clear();
    window.location.href = '/login';
  }
}

async function renovarToken() {
  const token = localStorage.getItem('token');

  try {
    const response = await fetch('http://localhost:8080/auth/refresh-token', {
      method: 'POST',
      headers: { 'Authorization': `Bearer ${token}` }
    });

    if (!response.ok) {
      throw new Error('No se pudo renovar el token');
    }

    const data = await response.json();

    // Actualizar localStorage con nuevo token y datos
    localStorage.setItem('token', data.token);
    localStorage.setItem('idUsuario', data.idUsuario);
    localStorage.setItem('nombre', data.nombre);
    localStorage.setItem('email', data.email);
    localStorage.setItem('dni', data.dni);
    localStorage.setItem('telefono', data.telefono);
    localStorage.setItem('rol', data.rol);

    console.log('✅ Token renovado exitosamente');
    
    // Recargar la página para aplicar cambios
    location.reload();
  } catch (error) {
    console.error('Error al renovar token:', error);
    localStorage.clear();
    window.location.href = '/login';
  }
}

// Ejecutar al cargar la app
verificarYRenovarToken();
```

#### Opción B: Interceptor de Axios (si usas Axios)

```javascript
import axios from 'axios';

// Crear instancia de axios
const api = axios.create({
  baseURL: 'http://localhost:8080'
});

// Interceptor de respuesta
api.interceptors.response.use(
  response => response,
  async error => {
    const originalRequest = error.config;

    // Si recibe 500 y no se ha intentado renovar
    if (error.response?.status === 500 && !originalRequest._retry) {
      originalRequest._retry = true;

      try {
        const token = localStorage.getItem('token');
        const refreshResponse = await axios.post(
          'http://localhost:8080/auth/refresh-token',
          {},
          { headers: { 'Authorization': `Bearer ${token}` } }
        );

        const newToken = refreshResponse.data.token;
        
        // Actualizar localStorage
        localStorage.setItem('token', newToken);
        localStorage.setItem('idUsuario', refreshResponse.data.idUsuario);
        
        // Reintentar request original con nuevo token
        originalRequest.headers['Authorization'] = `Bearer ${newToken}`;
        return api(originalRequest);
      } catch (refreshError) {
        // Si falla la renovación, cerrar sesión
        localStorage.clear();
        window.location.href = '/login';
        return Promise.reject(refreshError);
      }
    }

    return Promise.reject(error);
  }
);

export default api;
```

#### Opción C: Función Fetch con Auto-Renovación

```javascript
async function fetchConAutoRenovacion(url, options = {}) {
  const token = localStorage.getItem('token');
  
  // Agregar token a los headers
  const headers = {
    ...options.headers,
    'Authorization': `Bearer ${token}`
  };

  try {
    const response = await fetch(url, { ...options, headers });

    if (response.status === 500) {
      // Intentar renovar token
      const refreshResponse = await fetch('http://localhost:8080/auth/refresh-token', {
        method: 'POST',
        headers: { 'Authorization': `Bearer ${token}` }
      });

      if (refreshResponse.ok) {
        const data = await refreshResponse.json();
        
        // Actualizar localStorage
        localStorage.setItem('token', data.token);
        localStorage.setItem('idUsuario', data.idUsuario);

        // Reintentar request con nuevo token
        headers['Authorization'] = `Bearer ${data.token}`;
        return fetch(url, { ...options, headers });
      } else {
        // No se pudo renovar, cerrar sesión
        localStorage.clear();
        window.location.href = '/login';
      }
    }

    return response;
  } catch (error) {
    console.error('Error en fetch:', error);
    throw error;
  }
}

// Uso:
const response = await fetchConAutoRenovacion('http://localhost:8080/usuarios/12');
const usuario = await response.json();
```

---

### 🛠️ Solución 3: ACTUALIZAR CÓDIGO DEL DASHBOARD

Si tu dashboard está intentando cargar datos del usuario así:

```javascript
// ❌ ANTES (causa error 500 con token antiguo)
const email = parseJwt(token).sub;  // Podría ser email
const response = await fetch(`http://localhost:8080/usuarios/${email}`);
```

Cámbialo a:

```javascript
// ✅ OPCIÓN 1: Usar ID del localStorage
const idUsuario = localStorage.getItem('idUsuario');
const response = await fetch(`http://localhost:8080/usuarios/${idUsuario}`);

// ✅ OPCIÓN 2: Usar ID del token
const decoded = parseJwt(token);
const idUsuario = decoded.sub;  // Ahora es ID numérico
const response = await fetch(`http://localhost:8080/usuarios/${idUsuario}`);

// ✅ OPCIÓN 3: Detectar automáticamente
const identifier = decoded.sub;
const isEmail = identifier.includes('@');
const endpoint = isEmail 
  ? `http://localhost:8080/usuarios/email/${identifier}`
  : `http://localhost:8080/usuarios/${identifier}`;
const response = await fetch(endpoint);
```

---

## 📦 Archivos Actualizados

### Backend:
- ✅ `JwtUtil.java` - Genera tokens con ID en "sub"
- ✅ `AuthController.java` - Login devuelve datos completos
- ✅ `UsuarioController.java` - Endpoint adicional por email
- ✅ `TokenController.java` - Endpoints de validación y renovación

### Scripts de Testing:
- ✅ `test-token-simple.ps1` - Test de endpoints de token
- ✅ `test-jwt-fix.ps1` - Test del JWT corregido

### Documentación:
- ✅ `JWT_CORREGIDO.md` - Documentación del JWT
- ✅ `SOLUCION_ERROR_500_USUARIOS.md` - Solución de errores 500
- ✅ `GUIA_COMPLETA_JWT.md` - Este documento

---

## 🧪 Endpoints Disponibles

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/auth/login` | Login (devuelve token nuevo) |
| POST | `/auth/register` | Registro de usuario |
| POST | `/auth/validate-token` | Validar si token es válido |
| POST | `/auth/refresh-token` | Renovar token antiguo a nuevo |
| GET | `/auth/token-info` | Obtener info del token |
| GET | `/usuarios/{id}` | Buscar usuario por ID |
| GET | `/usuarios/email/{email}` | Buscar usuario por email |

---

## 🎯 Recomendación Final

**Para solucionar INMEDIATAMENTE:**

1. **En el frontend**: Ejecuta en la consola del navegador (F12):
   ```javascript
   localStorage.clear();
   location.reload();
   ```

2. **Haz login nuevamente**

3. **Verificar el nuevo token**: En la consola:
   ```javascript
   function parseJwt(token) {
     const base64Url = token.split('.')[1];
     const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
     return JSON.parse(atob(base64));
   }
   
   const token = localStorage.getItem('token');
   console.log(parseJwt(token));
   // Deberías ver: {sub: "12", email: "...", rol: "..."}
   ```

---

## ✅ Verificación Final

**Backend:**
```powershell
# Verificar que el backend esté corriendo
Test-NetConnection localhost -Port 8080

# Probar login
$body = '{"email":"testjwt@test.com","password":"12345678"}'
Invoke-RestMethod -Uri "http://localhost:8080/auth/login" -Method POST -ContentType "application/json" -Body $body
```

**Frontend:**
```javascript
// Verificar token en localStorage
const token = localStorage.getItem('token');
console.log('Token:', token ? 'Presente' : 'No presente');

// Verificar estructura del token
if (token) {
  const decoded = parseJwt(token);
  console.log('sub:', decoded.sub);
  console.log('Es numérico:', !isNaN(decoded.sub));
}
```

---

## 📞 Soporte

Si persisten los errores:

1. Verifica que el backend esté corriendo (puerto 8080)
2. Limpia completamente localStorage
3. Haz login nuevamente
4. Verifica la consola del navegador para errores

**Backend funcionando:**
- ✅ Compilación exitosa
- ✅ Puerto 8080 activo
- ✅ Endpoints de token disponibles
- ✅ JWT genera con ID en "sub"

---

**Fecha:** 24 de octubre de 2025  
**Proyecto:** SIGC Backend  
**Estado:** ✅ RESUELTO
