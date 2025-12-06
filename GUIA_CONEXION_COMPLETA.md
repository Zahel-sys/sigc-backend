# 🔌 GUÍA COMPLETA: Conectar BD, Backend y Frontend - SIGC

## 📊 Estado Actual

| Componente | Estado | Ubicación |
|-----------|--------|-----------|
| **Backend** | ✅ Corriendo | `http://localhost:8080` |
| **BD H2** | ✅ Activa | `jdbc:h2:mem:sigc_db` |
| **H2 Console** | ✅ Disponible | `http://localhost:8080/h2-console` |
| **Frontend** | ⏳ Parado | `http://localhost:5173` |

---

## 🔧 PASO 1: Acceder a H2 Console

### 1️⃣ Abre en el navegador:
```
http://localhost:8080/h2-console
```

### 2️⃣ Configura la conexión:
```
JDBC URL:    jdbc:h2:mem:sigc_db
User Name:   sa
Password:    (vacío - dejar en blanco)
```

### 3️⃣ Haz clic en **"Connect"**

---

## 📋 PASO 2: Verificar Datos en la BD

### 🔍 Ver todos los usuarios:
```sql
SELECT ID, EMAIL, NOMBRE, ROL, ACTIVO FROM USUARIOS;
```

**Resultado esperado:**
```
Admin:      admin@sigc.com / ADMIN
Doctor 1:   juan.perez@sigc.com / DOCTOR
Doctor 2:   maria.rodriguez@sigc.com / DOCTOR
Doctor 3:   roberto.fernandez@sigc.com / DOCTOR
Pacientes:  (ninguno aún)
```

### 📍 Ver especialidades:
```sql
SELECT * FROM ESPECIALIDAD;
```

### 🗓️ Ver horarios de doctores:
```sql
SELECT * FROM HORARIO_DOCTOR;
```

---

## 🆕 PASO 3: Crear Paciente de Prueba (Si no existe)

Ejecuta este SQL en H2 Console:

```sql
INSERT INTO USUARIOS (EMAIL, PASSWORD, NOMBRE, DNI, TELEFONO, ROL, ACTIVO, FECHA_CREACION)
VALUES (
    'paciente@sigc.com',
    '$2a$10$2qm6lXKPMQkZKCK3.VfIme8o9Y6bkHaKVMH8nZU3Oo9E8WvqmqX.e',
    'Juan Rodríguez Pérez',
    '12345678',
    '987654321',
    'PACIENTE',
    1,
    CURRENT_TIMESTAMP
);
```

**Contraseña del paciente:** `Paciente123456`

---

## ✅ PASO 4: Verificar Login en Backend

Abre una nueva terminal y ejecuta:

```powershell
$body = @{email="admin@sigc.com"; password="Admin123456"} | ConvertTo-Json
$response = Invoke-WebRequest -Uri "http://localhost:8080/auth/login" `
  -Method POST `
  -ContentType "application/json" `
  -Body $body `
  -UseBasicParsing
$response.Content | ConvertFrom-Json
```

**Respuesta esperada:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "email": "admin@sigc.com",
  "rol": "ADMIN"
}
```

---

## 🚀 PASO 5: Iniciar Frontend

### 1️⃣ Abre otra terminal en la carpeta del frontend:
```powershell
cd C:\Users\LEONARDO\sigc-frontend
```

### 2️⃣ Instala dependencias (si no las tienes):
```powershell
npm install
```

### 3️⃣ Inicia el servidor de desarrollo:
```powershell
npm run dev
```

### 4️⃣ Abre en el navegador:
```
http://localhost:5173
```

---

## 🔐 Credenciales de Prueba

| Usuario | Email | Contraseña | Rol |
|---------|-------|-----------|-----|
| Admin | admin@sigc.com | Admin123456 | ADMIN |
| Paciente | paciente@sigc.com | Paciente123456 | PACIENTE |
| Doctor 1 | juan.perez@sigc.com | (sin login) | DOCTOR |
| Doctor 2 | maria.rodriguez@sigc.com | (sin login) | DOCTOR |
| Doctor 3 | roberto.fernandez@sigc.com | (sin login) | DOCTOR |

---

## 🧪 Flujo de Prueba Completo

### 1. Login como Admin
```
Email:    admin@sigc.com
Password: Admin123456
```
→ Acceso: Panel de Administración

### 2. Login como Paciente
```
Email:    paciente@sigc.com
Password: Paciente123456
```
→ Acceso: Panel de Paciente → Ver Doctores → Reservar Cita

### 3. Operaciones de Admin
- Ver lista de doctores
- Crear nuevo doctor
- Editar especialidades
- Gestionar usuarios

---

## 🐛 Solución de Problemas

### ❌ "No puedo conectar a H2 Console"
```
→ Verifica que el backend esté corriendo en puerto 8080
→ Ejecuta: Get-Process java
→ Si no hay procesos: cd c:\Users\LEONARDO\sigc-backend && java -jar target/backend-0.0.1-SNAPSHOT.jar
```

### ❌ "Login fallido - Contraseña incorrecta"
```
→ Abre H2 Console
→ Ejecuta: SELECT EMAIL, PASSWORD FROM USUARIOS WHERE EMAIL='admin@sigc.com';
→ Si está vacío, crea el admin con la contraseña hasheada
```

### ❌ "Frontend no ve el backend"
```
→ Verifica el archivo .env en la carpeta frontend
→ Debe tener: VITE_API_URL=http://localhost:8080
→ Reinicia: npm run dev (después de cambiar .env)
```

### ❌ "Base de datos vacía al reiniciar"
```
Esto es NORMAL - H2 en memoria se reinicia con cada arranque del backend
→ Los inicializadores (DataInitializer, SampleDataInitializer) recrean datos automáticamente
```

---

## 📱 Arquitectura de Conexión

```
┌─────────────────┐         ┌──────────────────┐         ┌──────────────┐
│   Frontend      │ ───────▶│    Backend       │ ───────▶│  H2 Database │
│  localhost:5173 │ HTTP    │  localhost:8080  │  JDBC   │   In-Memory  │
└─────────────────┘         └──────────────────┘         └──────────────┘
     React+Vite         Spring Boot 3.5.8              H2 Database Engine
   (VITE_API_URL=      JWT Authentication            (Embedded Tomcat)
  http://localhost:8080)   + CORS Enabled
```

---

## 📚 Rutas de Acceso Importantes

| Ruta | Descripción | Acceso |
|------|-----------|--------|
| `http://localhost:8080` | Backend API | Público |
| `http://localhost:8080/h2-console` | BD Management | Público (TEMP) |
| `http://localhost:8080/swagger-ui.html` | API Documentation | Público |
| `http://localhost:5173` | Frontend Principal | Público |
| `http://localhost:8080/auth/login` | Login Endpoint | POST |
| `http://localhost:8080/auth/register` | Registro Endpoint | POST |

---

## ✨ Siguientes Pasos

1. ✅ Conectar H2 Console
2. ✅ Verificar datos de BD
3. ✅ Crear paciente de prueba
4. ✅ Verificar login en backend
5. ✅ Iniciar frontend
6. ✅ Probar login en frontend
7. ✅ Reservar una cita
8. ✅ Gestionar doctores como admin

---

**Generado:** 5 de Diciembre de 2025  
**Sistema:** SIGC Clínica v1.0.0  
**Estado:** ✅ Sistema Integrado y Funcional
