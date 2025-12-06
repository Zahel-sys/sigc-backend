# 🔗 GUÍA COMPLETA DE INTEGRACIÓN FRONTEND-BACKEND

**Última actualización:** 2025-01-20  
**Estado:** ✅ LISTO PARA INTEGRACIÓN  
**Versiones:** Spring Boot 3.5.8 | React + Vite | Node 18+

---

## 📋 TABLA DE CONTENIDOS

1. [Estado Actual del Sistema](#estado-actual)
2. [Requisitos Previos](#requisitos)
3. [Arquitectura de Integración](#arquitectura)
4. [Backend - Verificación](#backend-verificación)
5. [Frontend - Configuración](#frontend-configuración)
6. [Pruebas de Integración](#pruebas)
7. [Troubleshooting](#troubleshooting)

---

## 🎯 Estado Actual

### Backend ✅

```
✅ OPERACIONAL - Spring Boot 3.5.8
├── Puerto: 8080
├── Database: H2 Persistente (~/sigc_database/db)
├── Autenticación: JWT HS256 (24h expiration)
├── CORS: Configurado para localhost:5173-5175
├── Endpoints: 51+ operacionales
├── Swagger UI: http://localhost:8080/swagger-ui.html
└── OpenAPI: Completamente documentado (22 endpoints principales)

Usuarios Preconfigurados:
├── admin2@sigc.com (ADMIN) - Password: Admin123456
└── paciente@sigc.com (PACIENTE) - Password: Paciente123456
```

### Frontend ⏳

```
⏳ REQUIERE CONFIGURACIÓN
├── .env - debe crearse
├── .env.example - debe crearse
├── src/services/api.js - debe actualizarse
├── npm install - debe ejecutarse
└── npm run dev - debe iniciarse
```

---

## 📦 Requisitos Previos

### Herramientas Necesarias

| Herramienta | Versión | Propósito |
|-------------|---------|----------|
| **Node.js** | 18+ LTS | Runtime JavaScript |
| **npm** | 9+ | Package manager |
| **Git** | 2.40+ | Control de versiones |
| **Java JDK** | 21.0.7 | Backend runtime |
| **Maven** | 3.9+ | Build tool Java |

### Verificar Instalaciones

```powershell
# Verifica versiones
node --version      # v18.x.x o mayor
npm --version       # 9.x.x o mayor
java -version       # Java 21.0.7
mvn --version       # Apache Maven 3.9.x
git --version       # git version 2.40+
```

---

## 🏗️ Arquitectura de Integración

### Flujo de Peticiones HTTP

```
FRONTEND (React + Vite en :5173)
         ↓ (HTTP + JWT Token)
    CORS Policy Check ✅
         ↓
BACKEND (Spring Boot en :8080)
         ↓
    Security Filter ✅ JWT Validation
         ↓
    Controller → Service → Repository
         ↓
    H2 Database
         ↓ (JSON Response)
FRONTEND (Renderiza UI)
```

### Flujo de Autenticación JWT

```
1. USUARIO INGRESA CREDENCIALES
   ↓
2. POST /auth/login
   { email: "usuario@sigc.com", password: "..." }
   ↓
3. BACKEND VALIDA Y RETORNA JWT
   {
     token: "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
     usuario: { id, email, rol, nombre }
   }
   ↓
4. FRONTEND ALMACENA EN localStorage
   localStorage.setItem("usuario", JSON.stringify({ token, ... }))
   ↓
5. PETICIONES POSTERIORES INCLUYEN TOKEN
   Authorization: Bearer <token>
   ↓
6. BACKEND VALIDA TOKEN EN CADA PETICIÓN
   ✅ Valid → Procesa petición
   ❌ Invalid → Retorna 401 Unauthorized
```

### Estructura de Directorios (Frontend)

```
sigc-frontend/
├── .env                          # Variables de entorno (GIT-IGNORED)
├── .env.example                  # Template para .env
├── .gitignore
├── package.json
├── vite.config.js
├── public/
├── src/
│   ├── main.jsx
│   ├── App.jsx
│   ├── components/               # Componentes reutilizables
│   │   ├── Navbar.jsx
│   │   ├── Layout.jsx
│   │   └── ...
│   ├── pages/                    # Páginas (rutas)
│   │   ├── LoginPage.jsx
│   │   ├── DashboardPage.jsx
│   │   ├── DoctoresPage.jsx
│   │   ├── CitasPage.jsx
│   │   └── ...
│   ├── services/                 # Integración con API
│   │   ├── api.js                # Cliente Axios configurado
│   │   ├── authService.js
│   │   ├── doctorService.js
│   │   ├── appointmentService.js
│   │   └── ...
│   ├── context/                  # Estado global (React Context)
│   │   ├── AuthContext.jsx
│   │   └── ...
│   ├── hooks/                    # Custom hooks
│   │   ├── useAuth.js
│   │   ├── useFetch.js
│   │   └── ...
│   ├── styles/                   # CSS global
│   │   └── index.css
│   └── utils/                    # Funciones auxiliares
│       ├── validators.js
│       ├── formatters.js
│       └── ...
└── tests/
    └── ...
```

---

## ✅ Backend - Verificación

### 1. Verificar Backend Está Corriendo

```powershell
# Opción A: Verificar puerto 8080
$null = Test-NetConnection -ComputerName localhost -Port 8080 -WarningAction SilentlyContinue

# Opción B: Hacer petición HTTP
$response = Invoke-WebRequest -Uri "http://localhost:8080/api-docs" -ErrorAction SilentlyContinue
if ($response.StatusCode -eq 200) { 
    Write-Host "✅ Backend está operacional en :8080" -ForegroundColor Green 
}
```

### 2. Verificar Swagger UI

```
URL: http://localhost:8080/swagger-ui.html

Debe mostrar:
✅ Título: "SIGC - Sistema Integral de Gestión de Citas"
✅ Servidores: Development (localhost:8080), Production
✅ 22 Endpoints documentados
✅ Security: JWT Bearer Authentication
✅ Schemas: 12 modelos de datos
```

### 3. Prueba de Login Backend

```powershell
$loginBody = @{
    email = "admin2@sigc.com"
    password = "Admin123456"
} | ConvertTo-Json

$response = Invoke-WebRequest `
    -Uri "http://localhost:8080/auth/login" `
    -Method POST `
    -ContentType "application/json" `
    -Body $loginBody

$token = ($response.Content | ConvertFrom-Json).token
Write-Host "✅ Token obtenido: $($token.Substring(0, 20))..." -ForegroundColor Green
```

### 4. Prueba de Endpoint Autenticado

```powershell
$headers = @{
    "Authorization" = "Bearer $token"
    "Content-Type" = "application/json"
}

$response = Invoke-WebRequest `
    -Uri "http://localhost:8080/auth/me" `
    -Method GET `
    -Headers $headers

Write-Host "✅ Usuario autenticado: $(($response.Content | ConvertFrom-Json).email)" -ForegroundColor Green
```

---

## 🚀 Frontend - Configuración

### PASO 1: Clonar el Repositorio Frontend

```powershell
# Navega al directorio padre (NO dentro de sigc-backend)
cd C:\Users\LEONARDO

# Clona el repositorio
git clone https://github.com/Zahel-sys/sigc-frontend.git

# Entra al directorio
cd sigc-frontend
```

### PASO 2: Crear `.env`

**Ubicación:** `sigc-frontend/.env` (crear nuevo archivo)

**Contenido:**

```env
# ================================
# SIGC Frontend - Configuración
# ================================

# API Backend
VITE_API_URL=http://localhost:8080

# Aplicación
VITE_APP_NAME=SIGC Clínica
VITE_APP_VERSION=1.0.0

# Características
VITE_CORS_ENABLED=true

# Logging
VITE_LOG_LEVEL=debug
```

**Importante:** Este archivo NO debe ir en Git. Verificar `.gitignore`:

```bash
# Debe contener:
.env
.env.local
.env.*.local
```

### PASO 3: Crear `.env.example`

**Ubicación:** `sigc-frontend/.env.example`

**Contenido:**

```env
# ================================
# SIGC Frontend - Variables de Ejemplo
# ================================
# Copia este archivo a .env y configura tus valores

# API Backend
# Desarrollo: http://localhost:8080
# Producción: https://sigc-backend.onrender.com
VITE_API_URL=http://localhost:8080

# Aplicación
VITE_APP_NAME=SIGC Clínica
VITE_APP_VERSION=1.0.0

# Características
VITE_CORS_ENABLED=true

# Logging: debug, info, warn, error
VITE_LOG_LEVEL=debug
```

### PASO 4: Actualizar `src/services/api.js`

**Ubicación:** `src/services/api.js`

**Contenido Completo (reemplazar todo):**

```javascript
import axios from "axios";

// Leer la URL base desde la variable de entorno
const API_URL = import.meta.env.VITE_API_URL || "http://localhost:8080";

console.log("🔗 API URL configurada:", API_URL);

const api = axios.create({
  baseURL: API_URL,
  timeout: 10000,
  headers: {
    "Content-Type": "application/json",
  },
});

// Interceptor para agregar el token automáticamente a todas las peticiones
api.interceptors.request.use(
  (config) => {
    const usuario = localStorage.getItem("usuario");
    
    if (usuario) {
      try {
        const user = JSON.parse(usuario);
        if (user.token) {
          config.headers.Authorization = `Bearer ${user.token}`;
          console.debug("🔐 Token agregado a petición:", config.url);
        }
      } catch (e) {
        console.error("❌ Error al parsear usuario del localStorage:", e);
      }
    }
    
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Interceptor para manejo de errores
api.interceptors.response.use(
  (response) => {
    console.debug("✅ Respuesta recibida:", response.status, response.config.url);
    return response;
  },
  (error) => {
    if (error.response?.status === 401) {
      console.warn("⚠️ No autorizado (401). Limpiando tokens...");
      localStorage.removeItem("usuario");
      localStorage.removeItem("token");
      // Redirigir a login (si usas React Router)
      // window.location.href = "/login";
    } else if (error.response?.status === 403) {
      console.error("❌ Acceso denegado (403)");
    } else if (error.response?.status === 404) {
      console.error("❌ Recurso no encontrado (404)");
    } else if (error.response?.status >= 500) {
      console.error("❌ Error del servidor:", error.response.status);
    }
    
    return Promise.reject(error);
  }
);

export default api;
```

### PASO 5: Instalar Dependencias

```powershell
# Desde la carpeta sigc-frontend
npm install

# Verifica que se instaló correctamente
npm list axios
npm list react-router-dom
```

### PASO 6: Iniciar Frontend en Modo Desarrollo

```powershell
# Desde la carpeta sigc-frontend
npm run dev

# Salida esperada:
# > vite
# 
#   VITE v5.x.x  ready in xxx ms
#
#   ➜  Local:   http://localhost:5173/
#   ➜  press h + enter to show help
```

---

## 🧪 Pruebas de Integración

### Test 1: Backend Accesible desde Frontend

```javascript
// En la consola del navegador (F12 → Console):

// Verificar que API_URL está bien
console.log(import.meta.env.VITE_API_URL);

// Hacer una petición simple
fetch("http://localhost:8080/especialidades")
  .then(r => r.json())
  .then(data => console.log("✅ Especialidades:", data))
  .catch(e => console.error("❌ Error:", e));
```

### Test 2: Login Funciona

```javascript
// En la consola del navegador:

const loginData = {
  email: "admin2@sigc.com",
  password: "Admin123456"
};

fetch("http://localhost:8080/auth/login", {
  method: "POST",
  headers: { "Content-Type": "application/json" },
  body: JSON.stringify(loginData)
})
  .then(r => r.json())
  .then(data => {
    console.log("✅ Token recibido:", data.token.substring(0, 20) + "...");
    localStorage.setItem("usuario", JSON.stringify(data));
  })
  .catch(e => console.error("❌ Error:", e));
```

### Test 3: Petición Autenticada

```javascript
// En la consola del navegador (después de ejecutar Test 2):

const usuario = JSON.parse(localStorage.getItem("usuario"));

fetch("http://localhost:8080/auth/me", {
  method: "GET",
  headers: {
    "Authorization": "Bearer " + usuario.token,
    "Content-Type": "application/json"
  }
})
  .then(r => r.json())
  .then(data => console.log("✅ Usuario autenticado:", data))
  .catch(e => console.error("❌ Error:", e));
```

---

## 🐛 Troubleshooting

### Error: CORS Policy Block

**Síntoma:**
```
Access to XMLHttpRequest at 'http://localhost:8080/auth/login' 
from origin 'http://localhost:5173' has been blocked by CORS policy
```

**Solución:**

Verificar que `SecurityConfig.java` tenga:

```java
configuration.setAllowedOrigins(Arrays.asList(
    "http://localhost:5173",
    "http://localhost:5174",
    "http://localhost:5175",
    "http://localhost:8080"
));
```

Si cambió, recompilar backend:
```powershell
mvn clean package -DskipTests
java -jar target/backend-0.0.1-SNAPSHOT.jar
```

---

### Error: 401 Unauthorized

**Síntoma:**
```json
{
  "status": 401,
  "message": "Unauthorized - Missing or invalid token"
}
```

**Soluciones Posibles:**

1. **Token expirado (24h):** Volver a hacer login
2. **Token no enviado correctamente:** Verificar formato
   ```javascript
   // ❌ INCORRECTO
   headers: { "Authorization": "eyJhbGci..." }
   
   // ✅ CORRECTO
   headers: { "Authorization": "Bearer eyJhbGci..." }
   ```
3. **Token corrupto:** Limpiar localStorage y hacer login nuevo

---

### Error: Cannot GET /

**Síntoma:**
```
Cannot GET /
```

**Solución:**
- Verificar que estás navegando a `http://localhost:5173`, NO `http://localhost:8080`
- El puerto 8080 es solo para la API, frontend va en 5173

---

### Error: Backend No Responde

**Síntoma:**
```
net::ERR_CONNECTION_REFUSED
```

**Soluciones:**

1. **Verificar que backend está corriendo:**
   ```powershell
   curl http://localhost:8080/api-docs
   ```

2. **Si no responde, iniciar:**
   ```powershell
   cd C:\Users\LEONARDO\sigc-backend
   java -jar target/backend-0.0.1-SNAPSHOT.jar
   ```

3. **Si JAR no existe, compilar:**
   ```powershell
   mvn clean package -DskipTests
   ```

---

### Error: npm: command not found

**Solución:**
```powershell
# Instalar Node.js desde https://nodejs.org (versión LTS)
# Luego, en PowerShell NUEVO (reiniciar):
npm --version
```

---

## 📊 Checklist Final

### Antes de Iniciar

- [ ] **Backend**: `java -jar target/backend-0.0.1-SNAPSHOT.jar` ejecutando
- [ ] **Swagger UI**: `http://localhost:8080/swagger-ui.html` accesible
- [ ] **H2 Console**: `http://localhost:8080/h2-console` accesible
- [ ] **Login**: `admin2@sigc.com / Admin123456` funciona en Swagger

### Frontend - Configuración

- [ ] `.env` creado con `VITE_API_URL=http://localhost:8080`
- [ ] `.env.example` creado como template
- [ ] `src/services/api.js` actualizado con cliente Axios
- [ ] `.gitignore` incluye `.env`
- [ ] `npm install` ejecutado sin errores

### Frontend - Desarrollo

- [ ] `npm run dev` iniciado en `http://localhost:5173`
- [ ] Console (F12) sin errores CORS
- [ ] Consola muestra "🔗 API URL configurada: http://localhost:8080"
- [ ] Login funciona desde la interfaz
- [ ] Datos se cargan desde el backend

### Producción (Después)

- [ ] `.env` con `VITE_API_URL=https://sigc-backend.onrender.com`
- [ ] `npm run build` genera `dist/` correctamente
- [ ] Backend desplegado en Render/Vercel
- [ ] Frontend desplegado en Vercel/Netlify

---

## 🚀 Próximos Pasos

1. **Completar configuración frontend** (5-10 min)
   - Seguir pasos 1-6 de la sección "Frontend - Configuración"

2. **Ejecutar pruebas de integración** (5 min)
   - Seguir sección "Pruebas de Integración"

3. **Desarrollo de features** (según proyecto)
   - Usar Swagger UI como referencia de endpoints
   - Usar AuthContext para manejar autenticación

4. **Desplegar a producción** (cuando esté listo)
   - Cambiar `VITE_API_URL` en `.env`
   - Actualizar backend URL en `SecurityConfig` CORS
   - Hacer `npm run build` y deploy

---

## 📞 Contacto y Soporte

**Documentación del Backend:**
- OpenAPI: `http://localhost:8080/swagger-ui.html`
- H2 Console: `http://localhost:8080/h2-console`

**Documentación del Proyecto:**
- `OPENAPI_REFERENCIA.md` - Quick API reference
- `OPENAPI_VALIDACION_PRUEBAS.md` - Testing workflows
- `CREDENCIALES_FUNCIONANDO.md` - User credentials

**Generador de Cliente (Opcional):**
```bash
# Generar cliente TypeScript desde OpenAPI
npx openapi-generator-cli generate \
  -i http://localhost:8080/v3/api-docs \
  -g typescript-axios \
  -o src/generated
```

---

**Estado:** ✅ Completo y listo para implementar  
**Última revisión:** 2025-01-20  
**Versión:** 1.0.0
