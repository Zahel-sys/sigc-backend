# ⚡ PRUEBAS RÁPIDAS DE INTEGRACIÓN (5 MINUTOS)

**Objetivo:** Verificar que frontend y backend están correctamente integrados  
**Duración:** ~5 minutos  
**Prerequisito:** Backend corriendo en :8080, Frontend corriendo en :5173

---

## 🚀 PASO 1: Verificar Backend (1 min)

### Terminal 1 - PowerShell

```powershell
# Verificar que backend responde
$response = Invoke-WebRequest -Uri "http://localhost:8080/health" -ErrorAction SilentlyContinue

if ($response.StatusCode -eq 200 -or $response.StatusCode -eq 404) {
    Write-Host "✅ Backend responde en :8080" -ForegroundColor Green
} else {
    Write-Host "❌ Backend NO responde" -ForegroundColor Red
    Write-Host "Solución: Ejecuta: java -jar target/backend-0.0.1-SNAPSHOT.jar"
    exit
}

# Verificar Swagger UI
$swagger = Invoke-WebRequest -Uri "http://localhost:8080/swagger-ui.html" -ErrorAction SilentlyContinue
if ($swagger.StatusCode -eq 200) {
    Write-Host "✅ Swagger UI está disponible en http://localhost:8080/swagger-ui.html" -ForegroundColor Green
}
```

---

## 🚀 PASO 2: Verificar Frontend (1 min)

### Terminal 2 - PowerShell (desde sigc-frontend/)

```powershell
# Verifica que npm run dev está ejecutándose
# Salida esperada:
#   ➜  Local:   http://localhost:5173/

# Abre el navegador en http://localhost:5173
Start-Process "http://localhost:5173"
```

---

## 🚀 PASO 3: Prueba Login (1 min)

### Navegador - Console (F12)

**Credenciales de prueba:**
- Email: `admin2@sigc.com`
- Password: `Admin123456`

**Opción A - Desde UI Frontend (Recomendado):**

1. Abre `http://localhost:5173`
2. Haz login con las credenciales anteriores
3. Verifica que redirige al dashboard

**Opción B - Desde Console (para debug):**

```javascript
// Copiar y pegar en F12 → Console

const loginResponse = await fetch('http://localhost:8080/auth/login', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    email: 'admin2@sigc.com',
    password: 'Admin123456'
  })
});

const data = await loginResponse.json();
console.log('✅ Login exitoso');
console.log('Token:', data.token.substring(0, 30) + '...');
console.log('Usuario:', data.usuario);

// Guardar para el siguiente test
localStorage.setItem('usuario', JSON.stringify(data));
```

**Resultado esperado:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "usuario": {
    "id": 1,
    "email": "admin2@sigc.com",
    "nombre": "Admin",
    "apellidos": "Sistema",
    "rol": "ADMIN"
  }
}
```

---

## 🚀 PASO 4: Prueba CORS (1 min)

### Navegador - Console (F12)

```javascript
// Verifica que CORS está funcionando

// Test 1: Endpoint público (sin token)
console.log('📋 Test 1: GET /especialidades (público)');
const resp1 = await fetch('http://localhost:8080/especialidades');
const data1 = await resp1.json();
console.log('✅ Resultado:', data1.length, 'especialidades');

// Test 2: Endpoint autenticado (con token)
console.log('\n📋 Test 2: GET /auth/me (autenticado)');
const usuario = JSON.parse(localStorage.getItem('usuario'));
const resp2 = await fetch('http://localhost:8080/auth/me', {
  headers: {
    'Authorization': 'Bearer ' + usuario.token,
    'Content-Type': 'application/json'
  }
});
const data2 = await resp2.json();
console.log('✅ Usuario:', data2.email);

// Test 3: Petición con POST
console.log('\n📋 Test 3: POST /citas (crear cita)');
const nuevaCita = {
  fecha: '2025-02-10T14:00:00',
  doctorId: 1,
  pacienteId: 1,
  notas: 'Prueba de integración'
};
const resp3 = await fetch('http://localhost:8080/citas', {
  method: 'POST',
  headers: {
    'Authorization': 'Bearer ' + usuario.token,
    'Content-Type': 'application/json'
  },
  body: JSON.stringify(nuevaCita)
});
if (resp3.ok) {
  const data3 = await resp3.json();
  console.log('✅ Cita creada:', data3.id);
} else {
  console.error('❌ Error:', resp3.status);
}
```

---

## 🚀 PASO 5: Prueba Rutas Frontend (1 min)

### Navegador - UI

**Verificar que estas páginas cargan datos correctamente:**

| Ruta | Debe mostrar | Fuente datos |
|------|--------------|-------------|
| `/` o `/dashboard` | Dashboard principal | Backend |
| `/login` | Formulario login | Componente local |
| `/doctores` | Lista de doctores | GET /doctores |
| `/especialidades` | Especialidades | GET /especialidades |
| `/citas` | Citas del usuario | GET /citas |
| `/horarios` | Disponibilidad | GET /horarios |

**Para cada página:**

1. Abre en navegador
2. Abre F12 → Network tab
3. Verifica que hay peticiones GET/POST a `http://localhost:8080/*`
4. Status debe ser 200 o 201 (no 500, no CORS error)

---

## 📊 Resultados Esperados

### ✅ Backend - Swagger UI

```
http://localhost:8080/swagger-ui.html

Debe mostrar:
✅ "SIGC - Sistema Integral de Gestión de Citas"
✅ 22 endpoints documentados
✅ Botón "Authorize" con JWT scheme
✅ Ejemplos de requests/responses
✅ Try it out funciona
```

### ✅ Frontend - Consola (F12)

```
Mensajes esperados en la consola:
✅ "🔗 API URL configurada: http://localhost:8080"
✅ "🔐 Token agregado a petición: GET /auth/me"
✅ "✅ Respuesta recibida: 200 GET /especialidades"

ERRORES que NO deben aparecer:
❌ "Access to XMLHttpRequest... CORS policy..."
❌ "401 Unauthorized"
❌ "Cannot GET /"
```

### ✅ Network Tab (F12)

```
Todas las peticiones a http://localhost:8080/* deben ser:
- Status: 200, 201, 204 (OK)
- Content-Type: application/json
- Authorization header presente en peticiones autenticadas
```

---

## 🔧 Troubleshooting Rápido

### ❌ Error: CORS policy blocked

**Síntoma:**
```
Access to XMLHttpRequest at 'http://localhost:8080/...' 
from origin 'http://localhost:5173' has been blocked
```

**Solución rápida:**

```powershell
# Backend: Reinicia con CORS enabled
# En src/main/java/com/sigc/backend/security/SecurityConfig.java, 
# verifica que localhost:5173 está en allowedOrigins

# Luego reinicia:
mvn clean package -DskipTests
java -jar target/backend-0.0.1-SNAPSHOT.jar
```

---

### ❌ Error: 401 Unauthorized

**Síntoma:**
```json
{ "status": 401, "message": "Invalid or missing token" }
```

**Verificar:**

1. ¿El token se envía correctamente?
   ```javascript
   const token = JSON.parse(localStorage.getItem('usuario')).token;
   console.log('Token:', token.substring(0, 20) + '...');
   ```

2. ¿Tiene formato correcto?
   ```javascript
   headers: { 'Authorization': 'Bearer ' + token }  // ✅ CORRECTO
   headers: { 'Authorization': token }              // ❌ INCORRECTO
   ```

3. ¿El token expiró?
   ```javascript
   // Los tokens duran 24 horas. Hacer login nuevo:
   localStorage.removeItem('usuario');
   // Hacer login nuevamente
   ```

---

### ❌ Error: Backend no responde

**Síntoma:**
```
net::ERR_CONNECTION_REFUSED
```

**Verificar:**

```powershell
# ¿Backend está corriendo?
Get-NetTCPConnection -LocalPort 8080 | Select-Object *

# Si no hay resultado, iniciar:
cd C:\Users\LEONARDO\sigc-backend
java -jar target/backend-0.0.1-SNAPSHOT.jar

# Si el JAR no existe:
mvn clean package -DskipTests
java -jar target/backend-0.0.1-SNAPSHOT.jar
```

---

### ❌ Error: Frontend en puerto equivocado

**Síntoma:**
```
Cannot GET / (cuando abres http://localhost:8080)
```

**Solución:**
- Abre `http://localhost:5173` (NO :8080)
- Verifica que `npm run dev` está ejecutándose
- Verifica `.env` tiene `VITE_API_URL=http://localhost:8080`

---

## ✅ Checklist de Éxito

- [ ] Backend responde en `http://localhost:8080`
- [ ] Swagger UI muestra 22 endpoints
- [ ] Frontend carga en `http://localhost:5173`
- [ ] Login funciona con `admin2@sigc.com`
- [ ] Console muestra "API URL configurada"
- [ ] Network tab muestra peticiones a :8080
- [ ] Status codes son 200/201 (no 500)
- [ ] Datos cargan en Dashboard
- [ ] CORS errors no aparecen
- [ ] 401 errors solo cuando no hay token

---

## 📝 Script de Prueba Completo (PowerShell)

```powershell
# Copia y pega todo en PowerShell

Write-Host "════════════════════════════════════════" -ForegroundColor Cyan
Write-Host "  PRUEBA DE INTEGRACIÓN FRONTEND-BACKEND" -ForegroundColor Cyan
Write-Host "════════════════════════════════════════" -ForegroundColor Cyan

# TEST 1: Backend
Write-Host "`n📋 TEST 1: Backend en :8080" -ForegroundColor Yellow
try {
    $backend = Invoke-WebRequest -Uri "http://localhost:8080/api-docs" -WarningAction SilentlyContinue
    Write-Host "✅ Backend responde" -ForegroundColor Green
} catch {
    Write-Host "❌ Backend no responde" -ForegroundColor Red
    Write-Host "   Ejecuta: java -jar target/backend-0.0.1-SNAPSHOT.jar"
    exit
}

# TEST 2: Swagger UI
Write-Host "`n📋 TEST 2: Swagger UI" -ForegroundColor Yellow
try {
    $swagger = Invoke-WebRequest -Uri "http://localhost:8080/swagger-ui.html" -WarningAction SilentlyContinue
    Write-Host "✅ Swagger UI disponible en http://localhost:8080/swagger-ui.html" -ForegroundColor Green
} catch {
    Write-Host "❌ Swagger UI no disponible" -ForegroundColor Red
}

# TEST 3: Login
Write-Host "`n📋 TEST 3: Login" -ForegroundColor Yellow
$loginBody = @{
    email = "admin2@sigc.com"
    password = "Admin123456"
} | ConvertTo-Json

try {
    $loginResponse = Invoke-WebRequest `
        -Uri "http://localhost:8080/auth/login" `
        -Method POST `
        -ContentType "application/json" `
        -Body $loginBody -WarningAction SilentlyContinue
    
    $token = ($loginResponse.Content | ConvertFrom-Json).token
    Write-Host "✅ Login exitoso" -ForegroundColor Green
    Write-Host "   Token: $($token.Substring(0, 20))..." -ForegroundColor Gray
} catch {
    Write-Host "❌ Login falló" -ForegroundColor Red
}

# TEST 4: Endpoint autenticado
Write-Host "`n📋 TEST 4: Endpoint autenticado" -ForegroundColor Yellow
if ($token) {
    try {
        $headers = @{
            "Authorization" = "Bearer $token"
            "Content-Type" = "application/json"
        }
        
        $meResponse = Invoke-WebRequest `
            -Uri "http://localhost:8080/auth/me" `
            -Method GET `
            -Headers $headers -WarningAction SilentlyContinue
        
        $user = $meResponse.Content | ConvertFrom-Json
        Write-Host "✅ Autenticación funciona" -ForegroundColor Green
        Write-Host "   Usuario: $($user.email)" -ForegroundColor Gray
    } catch {
        Write-Host "❌ Autenticación falló" -ForegroundColor Red
    }
}

Write-Host "`n════════════════════════════════════════" -ForegroundColor Cyan
Write-Host "✅ TODAS LAS PRUEBAS COMPLETADAS" -ForegroundColor Green
Write-Host "════════════════════════════════════════" -ForegroundColor Cyan
Write-Host "`n🚀 Próximos pasos:" -ForegroundColor Cyan
Write-Host "   1. Abre http://localhost:5173 en el navegador"
Write-Host "   2. Verifica que npm run dev está corriendo"
Write-Host "   3. Intenta hacer login desde la UI"
Write-Host "   4. Abre F12 → Console para ver logs"
```

---

**Estado:** ✅ Listo para ejecutar  
**Última actualización:** 2025-01-20
