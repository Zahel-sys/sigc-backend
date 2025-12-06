# 🎯 REFERENCIA RÁPIDA - COMANDOS Y URLs

**Propósito:** Acceso inmediato a comandos, URLs y credenciales más usadas  
**Actualizado:** 2025-01-20

---

## 📋 TABLA DE CONTENIDOS

1. [URLs Principales](#urls-principales)
2. [Credenciales](#credenciales)
3. [Comandos Backend](#comandos-backend)
4. [Comandos Frontend](#comandos-frontend)
5. [Endpoints Principales](#endpoints-principales)
6. [Variables de Entorno](#variables-de-entorno)
7. [Archivos Importantes](#archivos-importantes)

---

## 🌐 URLs Principales

### Backend

| Descripción | URL | Método | Autenticación |
|-------------|-----|--------|---------------|
| **API Base** | http://localhost:8080 | - | - |
| **Swagger UI** | http://localhost:8080/swagger-ui.html | GET | NO |
| **OpenAPI JSON** | http://localhost:8080/v3/api-docs | GET | NO |
| **H2 Console** | http://localhost:8080/h2-console | GET | NO |
| **Health Check** | http://localhost:8080/health | GET | NO |

### Frontend

| Descripción | URL | Propósito |
|-------------|-----|----------|
| **App Principal** | http://localhost:5173 | App React |
| **Dev Server** | http://localhost:5173 | Vite dev |

---

## 👤 Credenciales

### Usuario Admin

```
Email:    admin2@sigc.com
Password: Admin123456
Rol:      ADMIN
Token:    Se obtiene en /auth/login
```

### Usuario Paciente

```
Email:    paciente@sigc.com
Password: Paciente123456
Rol:      PACIENTE
Token:    Se obtiene en /auth/login
```

### Acceso H2 Console

```
Usuario:  sa
Contraseña: (vacío)
JDBC URL: jdbc:h2:~/sigc_database/db;MODE=MySQL
```

---

## ⚙️ Comandos Backend

### Compilación

```powershell
# Compilar (sin tests)
mvn clean package -DskipTests

# Compilar (con tests)
mvn clean package

# Limpiar build
mvn clean

# Compilar sin empaquetar
mvn compile
```

### Ejecución

```powershell
# Desde JAR compilado
java -jar target/backend-0.0.1-SNAPSHOT.jar

# Con Spring Boot Maven Plugin
mvn spring-boot:run

# Con variables de entorno
$env:SERVER_PORT=8080
$env:JWT_SECRET="tu-secreto"
java -jar target/backend-0.0.1-SNAPSHOT.jar

# Con parámetros
java -Dspring.profiles.active=dev -jar target/backend-0.0.1-SNAPSHOT.jar
```

### Testing

```powershell
# Ejecutar tests
mvn test

# Ejecutar test específico
mvn test -Dtest=AuthControllerTest

# Con cobertura
mvn test jacoco:report
```

### Verificación

```powershell
# Ver si puerto 8080 está en uso
Get-NetTCPConnection -LocalPort 8080

# Ver procesos Java
Get-Process | Where-Object {$_.ProcessName -like "*java*"}

# Matar proceso en puerto 8080
Stop-Process -Id (Get-NetTCPConnection -LocalPort 8080).OwningProcess -Force
```

---

## 📦 Comandos Frontend

### Instalación

```powershell
# Desde directorio sigc-frontend/

# Instalar dependencias
npm install

# Limpiar caché
npm ci

# Verificar instalación
npm list
```

### Desarrollo

```powershell
# Iniciar dev server
npm run dev

# Dev server con puerto específico
npm run dev -- --port 5174

# Dev server con host específico
npm run dev -- --host 0.0.0.0
```

### Build

```powershell
# Compilar para producción
npm run build

# Preview del build
npm run preview

# Limpiar dist
rm -r dist
```

### Linting y Formatting

```powershell
# Lint con ESLint
npm run lint

# Format con Prettier (si está configurado)
npm run format

# Check types (si usas TypeScript)
npm run type-check
```

---

## 🔌 Endpoints Principales

### Autenticación

```
POST   /auth/login              Login (obtener JWT)
POST   /auth/register           Registrar usuario
GET    /auth/me                 Usuario autenticado actual
```

### Usuarios

```
GET    /usuarios                Listar usuarios
POST   /usuarios                Crear usuario
GET    /usuarios/{id}           Obtener usuario
PUT    /usuarios/{id}           Actualizar usuario
DELETE /usuarios/{id}           Eliminar usuario
```

### Doctores

```
GET    /doctores                Listar doctores
POST   /doctores                Crear doctor
GET    /doctores/{id}           Obtener doctor
PUT    /doctores/{id}           Actualizar doctor
DELETE /doctores/{id}           Eliminar doctor
```

### Especialidades

```
GET    /especialidades          Listar especialidades
POST   /especialidades          Crear especialidad
GET    /especialidades/{id}     Obtener especialidad
PUT    /especialidades/{id}     Actualizar especialidad
DELETE /especialidades/{id}     Eliminar especialidad
```

### Citas

```
GET    /citas                   Listar citas del usuario
POST   /citas                   Crear cita
GET    /citas/{id}              Obtener cita
PUT    /citas/{id}              Actualizar cita
DELETE /citas/{id}              Cancelar cita
```

### Horarios

```
GET    /horarios                Obtener horarios disponibles
GET    /horarios/doctor/{id}    Horarios de doctor específico
GET    /horarios/fecha/{fecha}  Horarios de fecha específica
```

### Órdenes Médicas

```
GET    /orders                  Listar órdenes
POST   /orders                  Crear orden
GET    /orders/{id}             Obtener orden
PUT    /orders/{id}             Actualizar orden
DELETE /orders/{id}             Eliminar orden
```

---

## 🔐 Variables de Entorno

### Backend - `.env`

```env
# Server
SERVER_PORT=8080

# Database
SPRING_DATASOURCE_URL=jdbc:h2:~/sigc_database/db;MODE=MySQL
SPRING_DATASOURCE_USERNAME=sa
SPRING_DATASOURCE_PASSWORD=
SPRING_DATASOURCE_DRIVER_CLASS_NAME=org.h2.Driver

# JPA
SPRING_JPA_HIBERNATE_DDL_AUTO=update
SPRING_JPA_SHOW_SQL=false
SPRING_JPA_DATABASE_PLATFORM=org.hibernate.dialect.H2Dialect

# H2 Console
SPRING_H2_CONSOLE_ENABLED=true
SPRING_H2_CONSOLE_PATH=/h2-console

# JWT
JWT_SECRET=sigc-secret-key-2025-security-sigc
JWT_EXPIRATION_MS=86400000  # 24 horas

# File Upload
APP_UPLOAD_DIR=uploads/

# CORS
CORS_ALLOWED_ORIGINS=http://localhost:5173,http://localhost:5174,http://localhost:5175
```

### Frontend - `.env`

```env
# API Backend
VITE_API_URL=http://localhost:8080

# Aplicación
VITE_APP_NAME=SIGC Clínica
VITE_APP_VERSION=1.0.0

# Features
VITE_CORS_ENABLED=true

# Logging
VITE_LOG_LEVEL=debug
```

---

## 📁 Archivos Importantes

### Backend

| Ruta | Descripción |
|------|-------------|
| `pom.xml` | Dependencias Maven |
| `src/main/resources/application.properties` | Configuración Spring |
| `src/main/java/com/sigc/backend/config/OpenApiConfig.java` | Configuración OpenAPI |
| `src/main/java/com/sigc/backend/config/SecurityConfig.java` | Configuración Seguridad |
| `src/main/java/com/sigc/backend/security/JwtUtil.java` | Utilidad JWT |
| `target/backend-0.0.1-SNAPSHOT.jar` | JAR ejecutable |

### Frontend

| Ruta | Descripción |
|------|-------------|
| `.env` | Variables de entorno (NO en Git) |
| `.env.example` | Template de variables |
| `package.json` | Dependencias npm |
| `vite.config.js` | Configuración Vite |
| `src/main.jsx` | Punto de entrada |
| `src/services/api.js` | Cliente Axios |
| `src/context/AuthContext.jsx` | Contexto autenticación |
| `dist/` | Build producción |

### Documentación

| Archivo | Propósito |
|---------|----------|
| `INTEGRACION_FRONTEND_BACKEND.md` | Guía completa integración |
| `PRUEBAS_RAPIDAS_INTEGRACION.md` | Pruebas en 5 minutos |
| `OPENAPI_REFERENCIA.md` | Referencia API rápida |
| `OPENAPI_VALIDACION_PRUEBAS.md` | Testing y validación |
| `CREDENCIALES_ADMIN.md` | Credenciales de prueba |

---

## 🧪 Pruebas Rápidas

### Test 1: Backend Responde

```powershell
curl http://localhost:8080/api-docs

# O en PowerShell:
Invoke-WebRequest -Uri "http://localhost:8080/api-docs" | Select-Object StatusCode
```

### Test 2: Login

```powershell
$body = @{
    email = "admin2@sigc.com"
    password = "Admin123456"
} | ConvertTo-Json

Invoke-WebRequest -Uri "http://localhost:8080/auth/login" `
    -Method POST `
    -ContentType "application/json" `
    -Body $body | Select-Object StatusCode, Content
```

### Test 3: Endpoint Autenticado

```powershell
# Primero obten token (ver Test 2)
$token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."

Invoke-WebRequest -Uri "http://localhost:8080/auth/me" `
    -Headers @{"Authorization"="Bearer $token"} | Select-Object StatusCode, Content
```

### Test 4: Frontend Carga

```powershell
# En navegador:
Start-Process "http://localhost:5173"

# O en PowerShell:
Invoke-WebRequest -Uri "http://localhost:5173" | Select-Object StatusCode
```

---

## 🔍 Debugging

### Ver Logs Backend

```powershell
# Con Spring Boot Maven Plugin
mvn spring-boot:run

# O desde JAR (si genera logs):
Get-Content logs/application.log -Tail 50 -Wait
```

### Ver Logs Frontend

```
F12 → Console → Filtrar por "🔗" para ver mensajes de API
F12 → Network → Filtrar por "localhost:8080" para ver peticiones
```

### Ver Base de Datos

```
1. Abre http://localhost:8080/h2-console
2. Login con: sa / (sin contraseña)
3. JDBC URL: jdbc:h2:~/sigc_database/db;MODE=MySQL
4. Ejecuta queries SQL
```

---

## 🚀 Scripts Útiles

### Script: Verificar Todo

```powershell
# Copiar en terminal PowerShell

Write-Host "Verificando sistema..." -ForegroundColor Cyan

# Backend
$backend = Test-NetConnection -ComputerName localhost -Port 8080 -WarningAction SilentlyContinue
Write-Host "Backend :8080 - $(if($backend.TcpTestSucceeded) {'✅'} else {'❌'})"

# Frontend
$frontend = Test-NetConnection -ComputerName localhost -Port 5173 -WarningAction SilentlyContinue
Write-Host "Frontend :5173 - $(if($frontend.TcpTestSucceeded) {'✅'} else {'❌'})"

# Swagger
try {
    $swagger = Invoke-WebRequest -Uri "http://localhost:8080/swagger-ui.html" -WarningAction SilentlyContinue -TimeoutSec 2
    Write-Host "Swagger UI - ✅"
} catch {
    Write-Host "Swagger UI - ❌"
}

# H2 Console
try {
    $h2 = Invoke-WebRequest -Uri "http://localhost:8080/h2-console" -WarningAction SilentlyContinue -TimeoutSec 2
    Write-Host "H2 Console - ✅"
} catch {
    Write-Host "H2 Console - ❌"
}
```

### Script: Iniciar Todo

```powershell
# Terminal 1
Write-Host "Iniciando Backend..." -ForegroundColor Green
cd C:\Users\LEONARDO\sigc-backend
java -jar target/backend-0.0.1-SNAPSHOT.jar

# Terminal 2
Write-Host "Iniciando Frontend..." -ForegroundColor Green
cd C:\Users\LEONARDO\sigc-frontend
npm run dev
```

---

## 🔗 Cheat Sheet - Formato HTTP

### Request GET

```http
GET /auth/me HTTP/1.1
Host: localhost:8080
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
Content-Type: application/json
```

### Request POST

```http
POST /auth/login HTTP/1.1
Host: localhost:8080
Content-Type: application/json

{
  "email": "admin2@sigc.com",
  "password": "Admin123456"
}
```

### Response 200 OK

```http
HTTP/1.1 200 OK
Content-Type: application/json
Content-Length: 250

{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "usuario": {
    "id": 1,
    "email": "admin2@sigc.com",
    "nombre": "Admin"
  }
}
```

### Response 401 Unauthorized

```http
HTTP/1.1 401 Unauthorized
Content-Type: application/json

{
  "status": 401,
  "message": "Invalid or missing token",
  "timestamp": "2025-01-20T10:30:00Z"
}
```

---

## 📞 Contactos y Recursos

| Recurso | Link |
|---------|------|
| **GitHub Backend** | https://github.com/Zahel-sys/sigc-backend |
| **GitHub Frontend** | https://github.com/Zahel-sys/sigc-frontend |
| **JWT.io** | https://jwt.io |
| **Spring Boot Docs** | https://spring.io/projects/spring-boot |
| **React Docs** | https://react.dev |
| **Axios Docs** | https://axios-http.com |
| **Vite Docs** | https://vitejs.dev |

---

**Última actualización:** 2025-01-20  
**Versión:** 1.0.0  
**Estado:** ✅ Completo

Mantén este archivo marcado en favoritos para acceso rápido. 🚀
